package xyz.cardsagainsttelegram.bean.game;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class User {
    private final String id;
    private String name;
    private String username; // Could be null

    private int wins;
    private Lobby lobby;

}
