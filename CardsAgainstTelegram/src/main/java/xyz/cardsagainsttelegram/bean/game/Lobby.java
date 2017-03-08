package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.message.send.*;
import xyz.cardsagainsttelegram.bean.card.BlackCard;
import xyz.cardsagainsttelegram.bean.card.Pack;
import xyz.cardsagainsttelegram.bean.card.WhiteCard;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyState;
import xyz.cardsagainsttelegram.bean.game.enums.PlayerState;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;
import xyz.cardsagainsttelegram.engine.handlers.PackRegistry;
import xyz.cardsagainsttelegram.engine.handlers.TelegramHandler;
import xyz.cardsagainsttelegram.utils.Strings;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Lobby extends TimerTask {
    @Getter
    private final String key;
    @Getter
    private final long creation;
    @Getter
    private final Player owner;
    private transient ReentrantLock lock = new ReentrantLock();
    @Getter
    private String name;
    @Getter
    @Setter
    private boolean privateGame;
    @Getter
    @Setter
    private long lastUse;
    @Getter
    @Setter
    private LobbyState lobbyState;

    @Getter
    private int maxPlayers = 6;
    @Getter
    private int minPlayers = 2;
    @Getter
    private int startingPlayers = 2;
    @Getter
    @Setter
    private int pointsToEnd = 5;
    @Getter
    @Setter
    private int cardPickTime = 20;

    @Getter
    @Setter
    private LinkedList<Player> players = new LinkedList<>();

    // ROUND INFO
    @Getter
    @Setter
    private Player czar;

    // History of Lobby
    @Getter
    @Setter
    private List<RoundStats> roundStats = new ArrayList<>();

    private int countdown;
    private int ignoreTimer;
    private int joinTimer;
    private long lastTick;

    private List<String> packs;
    private LinkedList<BlackCard> blackCards = new LinkedList<>();
    private LinkedList<WhiteCard> whiteCards = new LinkedList<>();
    @Getter
    private BlackCard blackCard = null;

    public Lobby(String key, Player player) {
        this.key = key;
        this.creation = System.currentTimeMillis();
        this.name = player.getName() + "'s Lobby";
        this.owner = player;

        this.players.add(owner);
        setLobbyState(LobbyState.WAIT);
        packs = PackRegistry.getPacksString();

        for (String packName : packs) {
            Pack pack = PackRegistry.getPack(packName);
            blackCards.addAll(pack.getBlacks());
            whiteCards.addAll(pack.getWhites());
        }

        Collections.shuffle(blackCards);
    }

    public int getPlayerCount() {
        return players.size();
    }

    /**
     * Call to let a player join this lobby. (Call this through Player#join(String)
     *
     * @param player
     * @return SUCCESS If lobby join was successful.
     */
    public LobbyResult playerJoin(Player player) {
        try {
            lock.lock();
            if (getPlayerCount() >= maxPlayers) return LobbyResult.LOBBY_FULL;

            // This should really never happen.
            if (players.contains(player)) return LobbyResult.PLAYER_IN_LOBBY;

            if (player.hasLobby()) return LobbyResult.PLAYER_HAS_LOBBY;

            if (getLobbyState() == LobbyState.DISBANDED) return LobbyResult.LOBBY_NOT_FOUND;

            if (getLobbyState() != LobbyState.WAIT) return LobbyResult.LOBBY_STARTED;

            players.add(player);
            sendMessageToAll("%s joined the lobby!", Strings.escape(player.getEffectiveName(), true));

            player.setLobby(this);
            joinTimer = 1;
            return LobbyResult.SUCCESS;
        } catch (Exception ex) {
            return LobbyResult.UNKNOWN;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Call to let a player leave this lobby. (Call this through Player#leave
     *
     * @param player
     * @return SUCCESS If lobby leave was successful.
     */
    public LobbyResult playerLeave(Player player) {
        try {
            lock.lock();
            if (!players.contains(player)) {
                return LobbyResult.PLAYER_NOT_IN_LOBBY;
            }

            if (owner.equals(player)) { // Lobby has to be disbanded with all players kicked.
                sendMessageToAll("Lobby disbanded.");
                disband();

            } else {
                // handle stuff.
                players.remove(player);
                sendMessageToAll("%s left the lobby!", Strings.escape(player.getEffectiveName(), true));
            }

            player.setLobby(null);
            return LobbyResult.SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return LobbyResult.UNKNOWN;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Send a message to everyone in the lobby.
     *
     * @param msg
     * @param obj
     */
    public void sendMessageToAll(String msg, Object... obj) {
        for (Player player : players) {
            player.send(msg, obj);
        }
    }

    public void sendMessageToAll(SendableMessage message) {
        for (Player player : players) {
            player.send(message);
        }
    }

    /**
     * Relay a message from one player to others.
     *
     * @param sender The sender of the message.
     * @param msg    The message to be relayed.
     */
    public void relayMessage(Player sender, String msg) {
        SendableTextMessage sendableTextMessage = SendableTextMessage.builder().textBuilder()
                .plain(Strings.PERSON_TALKING)
                .bold(sender.getEffectiveName())
                .plain(": " + msg).buildText().build();

        for (Player player : players) {
            if (player.equals(sender)) continue;

            player.send(sendableTextMessage);
        }
    }

    /**
     * Returns the URL you can use to share the game with others.
     *
     * @return
     */
    public String getShareLink() {
        return String.format("https://t.me/%s?start=%s", TelegramHandler.getBOT_USERNAME().substring(1), getKey());
    }

    /**
     * Call when you're disbanding this lobby.
     */
    private void disband() {
        players.clear();
        setLobbyState(LobbyState.DISBANDED);
        for (Player player : players) {
            player.setLobby(null);
        }
        // End Game

        LobbyRegistry.unloadLobby(this);
    }

    public void setName(String newName) {
        this.name = newName;

        sendMessageToAll("The name of the lobby changed to %s.", newName);
    }

    // This method is called every 1 second(s).
    @Override
    public void run() {

        try {
            lock.lock();
            lastTick = System.currentTimeMillis();
            checkPlayers();
            handleGameStarting();
            handlePreRound();
            handleCardPicking();
        } catch (Exception ex) {

        } finally {
            lock.unlock();
        }
    }

    private void handleCardPicking() {
        if (getLobbyState() == LobbyState.PLAYERS_PICKING) {
            boolean allPicked = true;
            for (Player player : getPlayers()) {
                assert player.getPlayerState() == PlayerState.PICKING || player.getPlayerState() == PlayerState.PICKED || player.getPlayerState() == PlayerState.CZAR; // Sanity Check

                if (player.getPlayerState() == PlayerState.CZAR) continue;
                if (player.getPlayerState() != PlayerState.PICKING) continue;

                if ((countdown <= 15 && countdown % 5 == 0) || countdown <= 3) {
                    String alert = "";
                    if (countdown <= 3) {
                        alert = Strings.ALERT;
                    }
                    player.send(ParseMode.MARKDOWN, "%sYou have **%d** seconds left to pick...", alert, countdown);

                    if (countdown == 1) {
                        player.send("Dude wtf, play the game you fuck.");
                    }
                }

                allPicked = false;
            }
            countdown--;
            if (allPicked) {
                // TODO Everyone has picked, stop the countdown and change game state.
            } else {
                if (countdown == 0) {
                    // handle stuff.
                }
            }
        }
    }

    private void handlePreRound() {
        if (getLobbyState() != LobbyState.PRE_ROUND) return; // Handle only game situations.

        assert ignoreTimer >= -1; // Sanity check

        if (ignoreTimer > 0) { // Count down until 0.
            --ignoreTimer;
            return;
        }

        if (ignoreTimer == 0) {
            startRound();
            ignoreTimer = -1; // At -1 the variable is ignored.
            return;
        }
    }

    private void checkPlayers() {
        if (getLobbyState() != LobbyState.WAIT) return;

        if (joinTimer > 0) {
            joinTimer--;
            return;
        }

        if (players.size() >= startingPlayers) {
            setLobbyState(LobbyState.GAME_STARTING);
            countdown = 10;
        }
    }

    private void handleGameStarting() {
        if (getLobbyState() != LobbyState.GAME_STARTING) return;
        if (countdown == 0) {
            startGame();
            return;
        }
        if (countdown <= 5 || countdown % 5 == 0) {
            sendMessageToAll("Game starting in %d seconds.", countdown);
        }
        countdown--;
        return;

    }

    private void startGame() {
        Collections.shuffle(players);

        setPlayersState(PlayerState.PICKING);
        ignoreTimer = -1; // Start round instantly
        startRound();
    }

    private void endGame() {

    }

    private void startRound() {
        setLobbyState(LobbyState.PLAYERS_PICKING);

        czar = pickCzar();

        // have a 30 second counter.
        countdown = cardPickTime;

        pickBlackCard();
        sendBlackCardToPlayers(blackCard);

        //TODO Give cards to players.

        for (Player player : players) {
            player.send("Round %d started.\n%s: %s is the Czar for this round.", roundStats.size() + 1, Strings.CZAR, czar.getEffectiveName());

            if (player.getPlayerState() != PlayerState.PICKING) continue;

            int deckSize = player.getDeck().size();
            int cardsRequired = 10 - deckSize; // number of cards per players.
            assert cardsRequired > 0; // Sanity check

            while (cardsRequired != 0) {
                player.getDeck().add(whiteCards.poll());
                cardsRequired--;
            }

            player.showCards();
        }
    }

    private void sendBlackCardToPlayers(BlackCard blackCard) {
        for (Player player : getPlayers()) {
            player.send(SendablePhotoMessage.builder().photo(new InputFile(blackCard.drawImage(), "bcard.png")).caption(blackCard.getTextAlternative()).build());
        }

    }

    private void setPlayersState(PlayerState state) {
        players.forEach(p -> p.setPlayerState(state));
    }

    private void pickBlackCard() {
        blackCard = blackCards.poll();
    }

    private Player pickCzar() {
        Player player = players.pop();
        players.addLast(player);

        player.setPlayerState(PlayerState.CZAR);
        return player;
    }
}
