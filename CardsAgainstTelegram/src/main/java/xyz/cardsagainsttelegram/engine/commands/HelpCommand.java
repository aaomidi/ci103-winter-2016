package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.CommandRegistry;

import java.util.TreeSet;

public class HelpCommand extends Command {
    public HelpCommand(CardsAgainstTelegram instance) {
        super(instance, "help", "Shows a list of commands with their description.", false, false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        TreeSet<Command> commands = new TreeSet<>(CommandRegistry.getCommands().values());
        StringBuilder sb = new StringBuilder();
        for (Command command : commands) {
            if (command.isPrivate()) {
                continue;
            }
            sb
                    //.append("/")
                    .append(command.getName())
                    .append(" - ")
                    .append(command.getDescription())
                    .append("\n");
        }

        event.getChat().sendMessage(sb.toString().trim());
        return true;
    }
}
