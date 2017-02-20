package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.CommandRegistry;

import java.util.HashMap;

public class HelpCommand extends Command {
    public HelpCommand(CardsAgainstTelegram instance) {
        super(instance, "help", "Shows a list of commands with their description.", false, false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        HashMap<String, Command> commands = CommandRegistry.getCommands();
        StringBuilder sb = new StringBuilder();
        for (String key : commands.keySet()) {
            sb
                    //.append("/")
                    .append(commands.get(key).getName())
                    .append(" - ")
                    .append(commands.get(key).getDescription())
                    .append("\n");
        }

        event.getChat().sendMessage(sb.toString().trim());
        return true;
    }
}
