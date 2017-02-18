package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;

public class HelpCommand extends Command {
    public HelpCommand(CardsAgainstTelegram instance) {
        super(instance, "help", "Shows a list of commands with their description", false, false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {

        event.getChat().sendMessage("hi");
        return true;
    }
}
