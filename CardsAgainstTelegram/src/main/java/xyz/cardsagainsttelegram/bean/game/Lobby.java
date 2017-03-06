package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyState;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;
import xyz.cardsagainsttelegram.engine.handlers.TelegramHandler;
import xyz.cardsagainsttelegram.utils.Strings;

import java.util.*;

public class Lobby extends TimerTask {
    @Getter
    private final String key;
    @Getter
    private final long creation;
    @Getter
    private final Player owner;

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
    private LinkedList<Player> players = new LinkedList<>();

    // ROUND INFO
    @Getter
    @Setter
    private Player czar;

    // History of Lobby
    @Getter
    @Setter
    private List<RoundStats> roundstats = new ArrayList<>();

    private int lobbyCountdown;
    private long lastTick;

    public Lobby(String key, Player player) {
        this.key = key;
        this.creation = System.currentTimeMillis();
        this.name = player.getName() + "'s Lobby";
        this.owner = player;

        this.players.add(owner);
        setLobbyState(LobbyState.WAIT);
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
        if (getPlayerCount() >= maxPlayers) return LobbyResult.LOBBY_FULL;

        // This should really never happen.
        if (players.contains(player)) return LobbyResult.PLAYER_IN_LOBBY;

        if (player.hasLobby()) return LobbyResult.PLAYER_HAS_LOBBY;

        if (getLobbyState() == LobbyState.DISBANDED) return LobbyResult.LOBBY_NOT_FOUND;

        if (getLobbyState() != LobbyState.WAIT) return LobbyResult.LOBBY_STARTED;

        players.add(player);
        sendMessageToAll("%s joined the lobby!", Strings.escape(player.getEffectiveName(), true));

        player.setLobby(this);
        return LobbyResult.SUCCESS;
    }

    /**
     * Call to let a player leave this lobby. (Call this through Player#leave
     *
     * @param player
     * @return SUCCESS If lobby leave was successful.
     */
    public LobbyResult playerLeave(Player player) {
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
        lastTick = System.currentTimeMillis();
        checkPlayers();
        handleCountdown();
    }

    private void checkPlayers() {
        if ((lastTick / 1000) % 5 != 0) return; // Check every 5 seconds.

        if (getLobbyState() != LobbyState.WAIT) return;
        if (players.size() >= startingPlayers) {
            setLobbyState(LobbyState.GAME_STARTING);
            lobbyCountdown = 10;
        }
    }

    private void handleCountdown() {
        switch (getLobbyState()) {
            case WAIT:
                return;
            case GAME_STARTING: {
                if (lobbyCountdown == 0) {
                    startGame();
                    return;
                }
                if (lobbyCountdown <= 5 || lobbyCountdown % 5 == 0) {
                    sendMessageToAll("Game starting in %d seconds.", lobbyCountdown);
                }
                lobbyCountdown--;
                return;
            }
            case IN_GAME:
                break;
            case DISBANDED:
                break;
        }
    }

    private void startGame() {
        setLobbyState(LobbyState.IN_GAME);
        czar = pickCzar();
        sendMessageToAll("%s Game has started! %s is the Czar for this round.", Strings.CZAR, czar.getEffectiveName());
        Collections.shuffle(players);

    }

    private Player pickCzar() {
        Player player = players.pop();
        players.addLast(player);
        return player;
    }
}
