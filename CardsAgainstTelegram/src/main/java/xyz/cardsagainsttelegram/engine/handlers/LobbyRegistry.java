package xyz.cardsagainsttelegram.engine.handlers;


import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.menu.InlineMenu;
import pro.zackpollard.telegrambot.api.menu.InlineMenuBuilder;
import xyz.cardsagainsttelegram.bean.game.Lobby;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;
import xyz.cardsagainsttelegram.utils.Strings;

import java.util.*;

public class LobbyRegistry {
    // Key -> Lobby
    private static Map<String, Lobby> lobbyMap = new HashMap<>();
    private static Set<Lobby> lobbies = new HashSet<>();
    private static Timer timer = new Timer();

    public static boolean createLobby(Player player) {
        if (player.hasLobby()) {
            return false;
        }

        String key = getUniqueString();

        if (key == null) {
            System.out.println("Death is here.");
            return false;
        }

        Lobby lobby = new Lobby(player.getInstance(), key, player);
        lobbies.add(lobby);
        lobbyMap.put(key, lobby);

        player.setLobby(lobby);

        timer.schedule(lobby, 0L, 1000L);
        return true;
    }

    /**
     * Creates a unique string used for a key.
     *
     * @return A unique string as a key. If it is null, then death is here.
     */
    private static String getUniqueString() {
        int i;

        i = 0;
        while (++i < Integer.MAX_VALUE) {
            String key = Strings.randomString(6).toUpperCase();
            if (lobbyMap.containsKey(key)) continue;
            return key;
        }

        i = 0;
        while (++i < Integer.MAX_VALUE) {
            String key = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            if (lobbyMap.containsKey(key)) continue;
            return key;
        }

        return null;
    }

    public static InlineMenu lobbySubMenu(Player player, InlineMenuBuilder builder) {
        boolean res = createLobby(player);

        Lobby lobby = player.getLobby();

        InlineMenu menu = builder.subMenu()
                .newRow()
                .inputButton(Strings.NAME + " Name: " + lobby.getName())
                .buttonCallback(b -> "Send me a new name!")
                .textCallback((button, value) -> {
                    lobby.setName(value);
                    button.setText(Strings.NAME + " Name: " + lobby.getName());
                })
                .buildRow()
                .newRow()
                .toggleButton(Strings.INVITE_OTHERS + " Invite others")
                .toggleCallback((b, v) -> {
                    player.send("Send your friends the following message so they can join you!");
                    player.send(ParseMode.MARKDOWN, "Click [here](https://t.me/%s?start=%s) to join %s's Cards Against Telegram Lobby!", player.getInstance().getBot().getBotUsername().substring(1), lobby.getKey(), lobby.getOwner().getName());
                    return null;
                })
                .newRow()
                .backButton(Strings.GO_BACK + " Back")
                .buildMenu();

        return menu;
    }

    /**
     * Gets a lobby using its key.
     *
     * @param key Key to access lobby.
     * @return Lobby if it found one.
     */
    public static Lobby getLobby(String key) {
        return lobbyMap.get(key);
    }

    /**
     * This method must be called through the player.
     *
     * @param player
     * @param key
     * @return
     */
    public static LobbyResult joinLobby(Player player, String key) {
        return joinLobby(player, getLobby(key));
    }

    /**
     * Call to let a player join a lobby with others.
     *
     * @param player The player joining
     * @param lobby  The lobby of the player.
     * @return
     */
    public static LobbyResult joinLobby(Player player, Lobby lobby) {
        if (lobby == null) return LobbyResult.LOBBY_NOT_FOUND;

        return lobby.playerJoin(player);
    }

    /**
     * Call to let a player leave a lobby.
     *
     * @param player The player leaving
     * @param lobby  The lobby of the player.
     * @return
     */
    public static LobbyResult leaveLobby(Player player, Lobby lobby) {
        if (lobby == null) return LobbyResult.LOBBY_NOT_FOUND;

        if (!player.hasLobby()) return LobbyResult.PLAYER_NOT_IN_LOBBY;

        return lobby.playerLeave(player);
    }

    public static void unloadLobby(Lobby lobby) {
        lobbyMap.remove(lobby.getKey());
        lobbies.remove(lobby);
    }
}
