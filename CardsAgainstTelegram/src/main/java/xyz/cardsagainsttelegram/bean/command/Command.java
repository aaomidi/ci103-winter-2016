package xyz.cardsagainsttelegram.bean.command;


import lombok.Getter;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.CommandRegistry;

public abstract class Command implements Comparable<Command> {

    protected final CardsAgainstTelegram instance;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final boolean admin;
    @Getter
    private final boolean isPrivate; // Should this command only work in a private chat?

    public Command(CardsAgainstTelegram instance, String name, String description, boolean admin, boolean isPrivate) {
        this.instance = instance;
        this.name = name;
        this.description = description;
        this.admin = admin;
        this.isPrivate = isPrivate;

        CommandRegistry.registerCommand(this);
    }


    public abstract boolean execute(Player player, CommandMessageReceivedEvent event);

    @Override
    public int compareTo(Command command) {
        return getName().compareTo(command.getName());
    }
}
