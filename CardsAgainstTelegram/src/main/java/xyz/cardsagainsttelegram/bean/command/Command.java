package xyz.cardsagainsttelegram.bean.command;


import lombok.Getter;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.CommandRegistry;

public abstract class Command {

    protected final CardsAgainstTelegram instance;
    @Getter
    private final String name;
    @Getter
    private final String description;
    @Getter
    private final boolean admin;

    public Command(CardsAgainstTelegram instance, String name, String description, boolean admin) {
        this.instance = instance;
        this.name = name;
        this.description = description;
        this.admin = admin;

        CommandRegistry.registerCommand(this);
    }


    public abstract boolean execute(Player player, CommandMessageReceivedEvent event);

}
