package xyz.cardsagainsttelegram.bean.game;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Lobby {
    private final String key;
    private final long creation;
    private String name;
    private boolean privateGame;
    private User owner;
    private long lastUse;

    private List<User> players;

    // ROUND INFO
    private User czar;

    // History of Lobby
    private List<RoundStats> roundstats;

}
