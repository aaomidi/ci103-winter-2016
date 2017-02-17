package xyz.cardsagainsttelegram.engine.handlers;


import xyz.cardsagainsttelegram.bean.game.Lobby;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.utils.Strings;

import java.util.*;

public class LobbyRegistry {
    // Key -> Lobby
    private static Map<String, Lobby> lobbyMap = new HashMap<>();
    private static Set<Lobby> lobbies = new HashSet<>();

    public static boolean createLobby(Player player) {
        if (!player.canCreateLobby()) {
            return false;
        }

        String key = getUniqueString();

        if (key == null) {
            System.out.println("Death is here.");
            return false;
        }

        Lobby lobby = new Lobby(key, player);
        lobbies.add(lobby);
        lobbyMap.put(key, lobby);

        player.setLobby(lobby);
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
}
