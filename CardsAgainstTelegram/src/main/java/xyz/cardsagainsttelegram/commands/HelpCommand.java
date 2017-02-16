package xyz.cardsagainsttelegram.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;

public class HelpCommand extends Command {
    public HelpCommand(CardsAgainstTelegram instance) {
        super(instance, "help", "Shows a list of commands with their description", false);
    }

    @Override
    public boolean execute(CommandMessageReceivedEvent event) {
        event.getChat().sendMessage("Hai");
        return false;
    }
}
