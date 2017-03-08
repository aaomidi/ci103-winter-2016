package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.CallbackQuery;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.menu.InlineMenu;
import pro.zackpollard.telegrambot.api.menu.InlineMenuBuilder;
import pro.zackpollard.telegrambot.api.menu.InlineMenuRowBuilder;
import pro.zackpollard.telegrambot.api.menu.button.impl.DummyButton;
import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.card.WhiteCard;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;
import xyz.cardsagainsttelegram.bean.game.enums.PlayerState;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;
import xyz.cardsagainsttelegram.utils.Strings;

import java.util.LinkedList;
import java.util.List;


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
    @Getter
    @Setter
    private PlayerState playerState = PlayerState.NONE;

    @Getter
    @Setter
    private List<WhiteCard> deck = new LinkedList<>(); // The player's deck of cards

    @Getter
    @Setter
    private LinkedList<WhiteCard> selectedCards = new LinkedList<>();

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
        LobbyResult result = LobbyRegistry.leaveLobby(this, lobby);
        this.send(Strings.getString(result));
        if (result == LobbyResult.SUCCESS) {
            playerState = PlayerState.NONE;
        }
        return result == LobbyResult.SUCCESS;
    }

    public boolean join(String lobby) {
        LobbyResult result = LobbyRegistry.joinLobby(this, lobby);
        this.send(Strings.getString(result));
        if (result == LobbyResult.SUCCESS) {
            playerState = PlayerState.WAITING;
        }
        return result == LobbyResult.SUCCESS;
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

    public void showCards() {
        SendableTextMessage.SendableTextMessageBuilder messageBuilder = SendableTextMessage.builder();
        SendableTextMessage.SendableTextBuilder textBuilder = messageBuilder.textBuilder();

        int requiredCards = getLobby().getBlackCard().getEmpty();
        textBuilder.plain(String.format("Select %d card%s:", requiredCards, requiredCards == 1 ? "" : "s"));

        textBuilder.newLine();
        int i = 1;
        for (WhiteCard card : deck) {
            textBuilder.bold(String.valueOf(i)).plain(". ").plain(card.getText());
            textBuilder.newLine();
            i++;
        }


        InlineMenuBuilder builder = InlineMenu.builder(instance.getBot()).forWhom(getChat()).message(textBuilder.buildText());

        builder.userFilter(user -> String.valueOf(user.getId()).equals(getId()));

        InlineMenuRowBuilder<InlineMenuBuilder> rowBuilder = builder.newRow();
        i = 1;
        for (WhiteCard card : deck) {
            assert i < 11; // Sanity check
            if (i == 6) {
                rowBuilder = rowBuilder.build().newRow();
            }

            int finalI = i;
            rowBuilder.dummyButton(Strings.NUMBERS[i]).callback((dummyButton, callbackQuery) -> selectedCard(dummyButton, callbackQuery, card, finalI)).build();
            i++;
        }

        rowBuilder = rowBuilder.build().newRow();
        rowBuilder.dummyButton(String.format("%s Submit %s", Strings.SUBMIT_BUTTON, Strings.SUBMIT_BUTTON)).callback(this::submit).build();

        rowBuilder.build().buildMenu().start();

    }

    private void selectedCard(DummyButton dummyButton, CallbackQuery query, WhiteCard card, int i) {
        if (playerState != PlayerState.PICKING) {
            send("You have finalized your picks. Wait for the Czar to pick the winner.");
            return;
        }
        if (dummyButton.getText().contains(Strings.BLUE_CIRCLE)) {
            dummyButton.setText(Strings.NUMBERS[i]);
            selectedCards.remove(card);
        } else {
            int requiredCards = getLobby().getBlackCard().getEmpty();
            if (selectedCards.size() >= requiredCards) {
                send("You have already picked %d card%s. Use the submit button to finalize your picks.", requiredCards, requiredCards == 1 ? "" : "s");
            }
            dummyButton.setText(String.format("%s %s", Strings.BLUE_CIRCLE, Strings.NUMBERS[i]));
            selectedCards.add(card);
        }
    }

    private void submit(DummyButton dummyButton, CallbackQuery query) {
        setPlayerState(PlayerState.PICKED);
    }
}
