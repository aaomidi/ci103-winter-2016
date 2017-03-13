package xyz.cardsagainsttelegram.bean.game;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pro.zackpollard.telegrambot.api.chat.CallbackQuery;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
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
import java.util.concurrent.locks.ReentrantLock;

@ToString
public class Player {
    @Getter
    private final CardsAgainstTelegram instance;
    @Getter
    private final String id;
    private final transient ReentrantLock lock = new ReentrantLock();
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
    private int globalWins;
    @Getter
    @Setter
    private transient Lobby lobby;
    private transient Chat chat;
    @Getter
    private transient InlineMenu inlineMenu;
    private PlayerState playerState;
    @Getter
    @Setter
    private List<WhiteCard> deck = new LinkedList<>(); // The player's deck of cards
    @Getter
    @Setter
    private LinkedList<WhiteCard> selectedCards = new LinkedList<>();
    private Message cooldownMessage;
    private Message pickedCardChoicesMessage;
    @Getter
    private int gameWins;

    public Player(CardsAgainstTelegram instance, User user) {
        this.instance = instance;
        this.id = String.valueOf(user.getId());
        this.name = user.getFullName();
        this.username = user.getUsername();
        this.globalWins = 0;
        this.playerState = PlayerState.NONE;
    }

    public PlayerState getPlayerState() {
        try {
            lock.lock();
            return playerState;
        } catch (Exception ex) {
            ex.printStackTrace();
            return PlayerState.NONE;
        } finally {
            lock.unlock();
        }
    }

    public void setPlayerState(PlayerState playerState) {
        try {
            lock.lock();
            this.playerState = playerState;
        } catch (Exception ex) {

        } finally {
            lock.unlock();
        }
    }

    /**
     * Called to add a win to the player
     */
    public void addWin(int amount) {
        gameWins += amount;
    }

    public void resetCooldownMessage() {
        this.cooldownMessage = null;
    }

    /**
     * Returns true if the player has a lobby.
     *
     * @return true if player has a lobby
     */
    public boolean hasLobby() {
        return lobby != null;
    }

    public Message send(String msg, Object... args) {
        return send(ParseMode.NONE, msg, args);
    }

    public void cooldownMessage(ParseMode parseMode, String msg, Object... args) {
        if (cooldownMessage == null) {
            cooldownMessage = send(parseMode, msg, args);
        } else {
            cooldownMessage = instance.getBot().editMessageText(cooldownMessage, String.format(msg, args), parseMode, false, null);
        }
    }

    public void pickedCardChoices(ParseMode parseMode, String msg, Object... args) {
        if (pickedCardChoicesMessage == null) {
            pickedCardChoicesMessage = send(parseMode, msg, args);
        } else {
            pickedCardChoicesMessage = instance.getBot().editMessageText(pickedCardChoicesMessage, String.format(msg, args), parseMode, false, null);
        }
    }

    public void resetPickedCardChoices() {
        pickedCardChoicesMessage = null;
        selectedCards.clear();
    }

    public void reset() {
        setPlayerState(PlayerState.NONE);
        resetPickedCardChoices();
        resetCooldownMessage();
        lobby = null;
        gameWins = 0;

    }

    public Message send(ParseMode parseMode, String msg, Object... args) {
        String fm = null;
        if (args.length == 0) {
            fm = msg;
        } else {
            fm = String.format(msg, args);
        }
        return getChat().sendMessage(SendableTextMessage.builder().parseMode(parseMode).message(fm).build());
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
            reset();
        }
        return result == LobbyResult.SUCCESS;
    }

    public boolean join(String lobby) {
        LobbyResult result = LobbyRegistry.joinLobby(this, lobby);
        this.send(Strings.getString(result));
        if (result == LobbyResult.SUCCESS) {
            setPlayerState(PlayerState.WAITING);
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
        textBuilder.newLine().newLine();

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
                return;
            }
            dummyButton.setText(String.format("%s %s", Strings.BLUE_CIRCLE, Strings.NUMBERS[i]));
            selectedCards.add(card);
        }
    }

    private void submit(DummyButton dummyButton, CallbackQuery query) {
        int requiredCards = getLobby().getBlackCard().getEmpty();
        if (selectedCards.size() < requiredCards) {
            send("You have not picked enough cards. You need %d.", requiredCards);
            return;
        }
        for (WhiteCard whiteCard : selectedCards) {
            deck.remove(whiteCard);
        }
        setPlayerState(PlayerState.PICKED);
    }

    public boolean isAPlayer() {
        PlayerState ps = getPlayerState();
        return ps == PlayerState.WAITING || ps == PlayerState.PICKED || ps == PlayerState.PICKING || ps == PlayerState.CZAR;
    }
}
