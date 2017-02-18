package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
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
    private int maxPlayers = 6;
    @Getter
    private int minPlayers = 6;

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
    }

    public int getPlayerCount() {
        return players.size();
    }


    public boolean playerJoin(Player player) {
        if (getPlayerCount() >= maxPlayers) return false;

        // This should really never happen.
        if (players.contains(player)) return false;

        players.add(player);
        sendMessageToAll("%s joined the lobby!", player.getEffectiveName());
        return true;
    }

    public void sendMessageToAll(String msg, Object... obj) {
        for (Player player : players) {
            player.send(msg, obj);
        }
    }

    public void relayMessage(Player sender, String msg) {
        for (Player player : players) {
            if (player.equals(sender)) continue;

            SendableTextMessage sendableTextMessage = SendableTextMessage.builder().textBuilder()
                    .plain(Strings.PERSON_TALKING)
                    .bold(sender.getEffectiveName())
                    .plain(": " + msg).buildText().build();

            player.send(sendableTextMessage);
        }
    }
}
