package xyz.cardsagainsttelegram.bean.game;

import lombok.Data;
import pro.zackpollard.telegrambot.api.user.User;

@Data
public class Player {
    private final String id;
    private String name;
    private String username; // Could be null
    private boolean admin;

    private int wins;
    private transient Lobby lobby;

    public Player(User user) {
        this.id = String.valueOf(user.getId());
        this.name = user.getFullName();
        this.username = user.getUsername();
        this.wins = 0;
    }

    /**
     * Returns true if the player can create a lobby.
     *
     * @return true if player can create a lobby.
     */
    public boolean canCreateLobby() {
        return lobby == null;
    }
}
