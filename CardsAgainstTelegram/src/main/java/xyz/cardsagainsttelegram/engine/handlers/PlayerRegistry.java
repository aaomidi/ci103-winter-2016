package xyz.cardsagainsttelegram.engine.handlers;

import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.game.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerRegistry {
    private static HashSet<Long> admins = new HashSet<>();

    private static Set<Player> players = new HashSet<>();
    private static Map<String, Player> idPlayerMap = new HashMap<>();

    static {
        // Hardcoded admins for now
        // My ID is literally an order of magnitude shorter than you guys... Jesus christ I've been using this app for a while.
        admins.add(55395012L);  // Amir
        admins.add(295752642L); // Sagar
        admins.add(272350063L); // Jess
    }

    public static Player getPlayer(CardsAgainstTelegram instance, User user) {
        Player player = idPlayerMap.get(String.valueOf(user.getId()));

        if (player == null) {
            player = new Player(instance, user);
            idPlayerMap.put(String.valueOf(user.getId()), player);
            players.add(player);
        }

        if (admins.contains(user.getId())) {
            player.setAdmin(true);
        }
        return player;
    }

    public static Set<Player> getPlayers() {
        return players;
    }

}
