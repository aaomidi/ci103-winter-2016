package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.menu.InlineMenu;
import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;
import xyz.cardsagainsttelegram.utils.Strings;


public class Player {
    @Getter
    private final CardsAgainstTelegram instance;
    @Getter
    private final String id;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String username; // Could be null
    @Getter
    @Setter
    private boolean admin;

    @Getter
    @Setter
    private int wins;
    @Getter
    @Setter
    private transient Lobby lobby;
    private transient Chat chat;
    @Getter
    private transient InlineMenu inlineMenu;

    public Player(CardsAgainstTelegram instance, User user) {
        this.instance = instance;
        this.id = String.valueOf(user.getId());
        this.name = user.getFullName();
        this.username = user.getUsername();
        this.wins = 0;
    }

    /**
     * Returns true if the player has a lobby.
     *
     * @return true if player has a lobby
     */
    public boolean hasLobby() {
        return lobby != null;
    }

    public void send(String msg, Object... args) {
        send(ParseMode.NONE, msg, args);
    }

    public void send(ParseMode parseMode, String msg, Object... args) {
        String fm = null;
        if (args.length == 0) {
            fm = msg;
        } else {
            fm = String.format(msg, args);
        }
        getChat().sendMessage(SendableTextMessage.builder().parseMode(parseMode).message(fm).build());
    }

    public void send(SendableMessage message) {
        getChat().sendMessage(message);
    }

    public Chat getChat() {
        if (chat == null) {
            chat = getInstance().getBot().getChat(id);
        }
        return chat;
    }

    public void sendInlineMenu(InlineMenu menu) {
        setInlineMenu(menu);
        menu.start();
    }

    private void setInlineMenu(InlineMenu menu) {
        if (this.inlineMenu != null) {
            //inlineMenu.unregister();
        }
        this.inlineMenu = menu;
    }

    public boolean leave() {
        LobbyConnectionResult result = LobbyRegistry.leaveLobby(this, lobby);
        this.send(Strings.getString(result));
        return result == LobbyConnectionResult.SUCCESS;
    }

    public boolean join(String lobby) {
        LobbyConnectionResult result = LobbyRegistry.joinLobby(this, lobby);
        this.send(Strings.getString(result));
        return result == LobbyConnectionResult.SUCCESS;
    }

    public String getEffectiveName() {
        if (getUsername() == null) {
            return getName();
        }
        return getUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }
        return ((Player) o).getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
