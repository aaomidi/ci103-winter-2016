package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;

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
    }
}
