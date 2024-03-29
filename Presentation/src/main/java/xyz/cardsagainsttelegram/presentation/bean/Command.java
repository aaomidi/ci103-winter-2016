package xyz.cardsagainsttelegram.presentation.bean;

import lombok.Getter;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.presentation.Presentation;
import xyz.cardsagainsttelegram.presentation.engine.handlers.CommandRegistry;


public abstract class Command implements Comparable<Command> {

    protected final Presentation instance;
    @Getter
    private final String name;
    @Getter
    private final String description;

    public Command(Presentation instance, String name, String description) {
        this.instance = instance;
        this.name = name;
        this.description = description;

        CommandRegistry.registerCommand(this);
    }

    /**
     * Executes a command
     *
     * @param user
     * @param event
     * @return
     */
    public abstract boolean execute(User user, CommandMessageReceivedEvent event);

    @Override
    public int compareTo(Command command) {
        return getName().compareTo(command.getName());
    }
}
