package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.CommandRegistry;


public class BotFatherCommand extends Command {
    public BotFatherCommand(CardsAgainstTelegram instance) {
        super(instance, "botfather", "Returns the list of commands to add to botfather.", true, true);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        SendableTextMessage.SendableTextBuilder builder = SendableTextMessage.builder().textBuilder();
        for (Command command : CommandRegistry.getCommands().values()) {
            if (command.isAdmin()) continue;
            builder.preformatted(String.format("%s - %s", command.getName(), command.getDescription()));
        }

        player.send(builder.buildText().build());

        return true;
    }
}
