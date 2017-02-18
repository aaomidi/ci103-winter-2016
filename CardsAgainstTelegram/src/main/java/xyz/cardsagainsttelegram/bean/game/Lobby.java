package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;
import xyz.cardsagainsttelegram.engine.handlers.TelegramHandler;
import xyz.cardsagainsttelegram.utils.Strings;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    @Getter
    private final String key;
    @Getter
    private final long creation;
    @Getter
    private final Player owner;

    @Getter
    @Setter
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
    @Setter
    private List<Player> players = new ArrayList<>();

    // ROUND INFO
    @Getter
    @Setter
    private Player czar;

    // History of Lobby
    @Getter
    @Setter
    private List<RoundStats> roundstats = new ArrayList<>();

    public Lobby(String key, Player player) {
        this.key = key;
        this.creation = System.currentTimeMillis();
        this.name = player.getName() + "'s Lobby";
        this.owner = player;

        this.players.add(owner);
        lobbyState = LobbyState.WAIT;
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
    public LobbyConnectionResult playerJoin(Player player) {
        if (getPlayerCount() >= maxPlayers) return LobbyConnectionResult.LOBBY_FULL;

        // This should really never happen.
        if (players.contains(player)) return LobbyConnectionResult.PLAYER_IN_LOBBY;

        if (player.hasLobby()) return LobbyConnectionResult.PLAYER_HAS_LOBBY;

        players.add(player);
        sendMessageToAll("%s joined the lobby!", Strings.escape(player.getEffectiveName(), true));

        player.setLobby(this);
        return LobbyConnectionResult.SUCCESS;
    }

    /**
     * Call to let a player leave this lobby. (Call this through Player#leave
     *
     * @param player
     * @return SUCCESS If lobby leave was successful.
     */
    public LobbyConnectionResult playerLeave(Player player) {
        if (!players.contains(player)) {
            return LobbyConnectionResult.PLAYER_NOT_IN_LOBBY;
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
        return LobbyConnectionResult.SUCCESS;
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
        return String.format("https://t.me/%s/%s", TelegramHandler.getBOT_USERNAME(), getKey());
    }

    /**
     * Call when you're disbanding this lobby.
     */
    private void disband() {
        players.clear();
        lobbyState = LobbyState.DISBANDED;
        for (Player player : players) {
            player.setLobby(null);
        }
        // End Game

        LobbyRegistry.unloadLobby(this);
    }
}
