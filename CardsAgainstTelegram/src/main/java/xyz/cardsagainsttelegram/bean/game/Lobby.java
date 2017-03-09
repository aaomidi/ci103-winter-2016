package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.message.send.*;
import pro.zackpollard.telegrambot.api.menu.InlineMenu;
import pro.zackpollard.telegrambot.api.menu.InlineMenuBuilder;
import pro.zackpollard.telegrambot.api.menu.InlineMenuRowBuilder;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
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

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Lobby extends TimerTask {
    public static SecureRandom RANDOM = new SecureRandom();
    private final CardsAgainstTelegram instance;
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
    private int cardPickTime = 40;

    @Getter
    @Setter
    private List<Player> players = Collections.synchronizedList(new LinkedList<>());

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

    public Lobby(CardsAgainstTelegram instance, String key, Player player) {
        this.instance = instance;
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

        Collections.shuffle(blackCards, RANDOM);
        Collections.shuffle(whiteCards, RANDOM);

        //pickBlackCard();
        //sendBlackCardToPlayers(blackCard, false, null);
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
            player.setPlayerState(PlayerState.WAITING);
            joinTimer = 10;
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
        try {
            lock.lock();
            SendableTextMessage sendableTextMessage = SendableTextMessage.builder().textBuilder()
                    .plain(Strings.PERSON_TALKING)
                    .bold(sender.getEffectiveName())
                    .plain(": " + msg).buildText().build();

            for (Player player : players) {
                if (player.equals(sender)) continue;

                player.send(sendableTextMessage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
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

    /**
     * This method handles the timeout for card picking for both the players and the czar.
     */
    private void handleCardPicking() {
        try {
            lock.lock();
            if (getLobbyState() == LobbyState.PLAYERS_PICKING) {
                boolean allPicked = true;
                boolean noOnePicked = true;

                for (Player player : getPlayers()) {
                    assert player.isAPlayer(); // Sanity Check

                    if (player.getPlayerState() == PlayerState.CZAR) continue;
                    if (player.getPlayerState() == PlayerState.PICKED) {
                        noOnePicked = false;
                        continue;
                    }
                    if (player.getPlayerState() != PlayerState.PICKING) continue;

                    if ((countdown <= 15 && countdown % 5 == 0) || countdown <= 3) {
                        String alert = "";
                        if (countdown <= 3) {
                            alert = Strings.ALERT;
                        }
                        player.cooldownMessage(ParseMode.MARKDOWN, "%sYou have **%d** seconds left to pick...", alert, countdown);

                        if (countdown == 1) {
                            player.send("Dude wtf, play the game you fuck.");
                        }
                    }

                    allPicked = false;
                }
                countdown--;
                if (allPicked || countdown == 0) {
                    // This handles at the end of players picking their choices.
                    handlePlayerPickingEnd(noOnePicked);
                }
            } else if (getLobbyState() == LobbyState.CZAR_PICKING) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    private void handlePlayerPickingEnd(boolean noOnePicked) {
        if (noOnePicked) {
            sendMessageToAll("No one picked. The czar gets 3 points because the players are idiots.");
            // TODO: Give points to czar.
            czar.addWin(3);
            setLobbyState(LobbyState.PRE_ROUND);
            ignoreTimer = 5;

            if (handleWinCase()) return;
            return;
        }

        // CZAR is going to be picking
        setLobbyState(LobbyState.CZAR_PICKING);
        countdown = cardPickTime;
        LinkedList<Pick> picks = new LinkedList<>();
        for (Player player : players) {
            if (player.getPlayerState() != PlayerState.PICKED) continue;
            picks.add(new Pick(player, player.getSelectedCards()));
        }
        Collections.shuffle(picks, RANDOM);

        int i = 1;

        // Text Builder
        StringBuilder sb = new StringBuilder("Cards Picked").append("\n\n");
        for (Pick pick : picks) {
            sb.append(String.format("**%d**. ", i));
            for (Iterator<WhiteCard> iterator = pick.getPicks().iterator(); iterator.hasNext(); ) {
                WhiteCard whiteCard = iterator.next();
                sb.append(whiteCard.getText());
                if (iterator.hasNext()) {
                    sb.append(" - ");
                }
            }
            sb.append("\n");
            ++i;
        }

        // Send the players other than the czar the cards.
        for (Player player : players) {
            //if (player.getPlayerState() == PlayerState.CZAR) continue;

            player.pickedCardChoices(ParseMode.MARKDOWN, sb.toString());
        }

        InlineMenuBuilder builder = InlineMenu.builder(instance.getBot()).forWhom(czar.getChat()).
                message(SendableTextMessage.plain("Pick the best response:"));

        builder.userFilter(user -> String.valueOf(user.getId()).equals(czar.getId()));
        InlineMenuRowBuilder<InlineMenuBuilder> rowBuilder = builder.newRow();

        i = 1;
        for (Pick pick : picks) {
            if ((i + 1) % 5 == 0) {
                rowBuilder = rowBuilder.build().newRow();
            }

            rowBuilder.dummyButton(Strings.getNumber(i)).callback((dummyButton, query) -> handleCzarCardPick(pick, picks)).build();
        }

        rowBuilder.build().buildMenu().start();
    }

    /**
     * Called when the Czar picks a card.
     *
     * @param pick
     */
    private void handleCzarCardPick(Pick pick, List<Pick> picks) {
        blackCard.setChoices(pick.getPicks());

        SendablePhotoMessage.builder().photo(new InputFile(blackCard.drawImage(), "newBCard.png")).caption("Winning Card\n" + blackCard.getTextAlternative());

        sendBlackCardToPlayers(blackCard, true, pick);

        // Update the old message to include the names of the people who picked the cards.

        int i = 1;
        StringBuilder sb = new StringBuilder("Cards Picked").append("\n\n");
        for (Pick p : picks) {
            sb.append(String.format("**%d**. ", i));
            for (Iterator<WhiteCard> iterator = p.getPicks().iterator(); iterator.hasNext(); ) {
                WhiteCard whiteCard = iterator.next();
                sb.append(whiteCard.getText());
                if (iterator.hasNext()) {
                    sb.append(" - ");
                }
            }
            sb.append(String.format(" %s Picked by __%s__", Strings.RIGHT_ARROW, p.getPlayer().getEffectiveName()));
            sb.append("\n");
            ++i;
        }

        for (Player player : players) {
            player.pickedCardChoices(ParseMode.MARKDOWN, sb.toString());
            player.resetPickedCardChoices();
            player.setPlayerState(PlayerState.WAITING);
        }
        //TODO Give point to winner.

        pick.getPlayer().addWin(1);

        if (handleWinCase()) return;

        setLobbyState(LobbyState.PRE_ROUND);
        ignoreTimer = 2;
    }

    /**
     * Checks if a player has won
     */
    private boolean handleWinCase() {
        Player winner = null;
        for (Player player : players) {
            if (player.getGameWins() >= getPointsToEnd()) {
                winner = player;
                break;
            }
        }
        if (winner == null) return false;

        sendMessageToAll("%s is the worst person of you all. You should all be ashamed of yourselves. K Bye", winner.getEffectiveName());
        disband();
        return true;
    }

    private void handlePreRound() {
        if (getLobbyState() != LobbyState.PRE_ROUND) return; // Handle only game situations.

        assert ignoreTimer >= -1; // Sanity check

        if (ignoreTimer > 0) { // Count down until 0.
            --ignoreTimer;
            return;
        }

        if (ignoreTimer <= 0) {
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
            for (Player player : players) {
                player.cooldownMessage(ParseMode.NONE, "Game starting in %d seconds.", countdown);
            }
        }
        countdown--;
        return;

    }

    private void startGame() {
        Collections.shuffle(players, RANDOM);

        setLobbyState(LobbyState.PRE_ROUND);
    }

    private void startRound() {
        // Reset the stuff.
        for (Player p : players) {
            if (!p.isAPlayer()) continue;
            p.resetCooldownMessage();
            p.resetPickedCardChoices();
            p.setPlayerState(PlayerState.PICKING);
        }

        setLobbyState(LobbyState.PLAYERS_PICKING);

        czar = pickCzar();

        // have a 30 second counter.
        countdown = cardPickTime;

        pickBlackCard();
        sendBlackCardToPlayers(blackCard, false, null);


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

    private void sendBlackCardToPlayers(BlackCard blackCard, boolean winning, Pick pick) {
        for (Player player : getPlayers()) {
            SendablePhotoMessage.SendablePhotoMessageBuilder builder = SendablePhotoMessage.builder().photo(new InputFile(blackCard.drawImage(), "bcard.png"));
            String winningCard = "";
            String winningPlayer = "";
            if (winning) {
                winningCard = "Winning Card\n";
                winningPlayer = "\nBy " + pick.getPlayer().getEffectiveName();
            }
            builder.caption(winningCard + blackCard.getTextAlternative() + winningPlayer);

            player.send(builder.build());
        }

    }


    private void pickBlackCard() {
        blackCard = blackCards.poll();
    }

    private Player pickCzar() {
        Player player = players.remove(0);
        players.add(player);

        player.setPlayerState(PlayerState.CZAR);
        return player;
    }
}
