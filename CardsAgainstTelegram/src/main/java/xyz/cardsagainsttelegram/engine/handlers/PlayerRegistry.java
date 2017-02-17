package xyz.cardsagainsttelegram.engine.handlers;

import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.bean.game.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerRegistry {
    private static Set<Player> players = new HashSet<>();
    private static Map<String, Player> idPlayerMap = new HashMap<>();

    public static Player getPlayer(User user) {
        Player player = idPlayerMap.computeIfAbsent(String.valueOf(user.getId()), p -> new Player(user));
        players.add(player);

        return player;
    }
}
