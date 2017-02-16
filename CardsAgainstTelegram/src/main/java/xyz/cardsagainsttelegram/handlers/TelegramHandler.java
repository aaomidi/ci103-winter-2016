package xyz.cardsagainsttelegram.handlers;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.bean.command.Command;

public class TelegramHandler implements Listener {
    private final TelegramBot bot;

    public TelegramHandler(String apiKey) {
        bot = TelegramBot.login(apiKey);
        bot.startUpdates(true);
        bot.getEventsManager().register(this);
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        String message = event.getCommand();
        Command command = CommandRegister.getCommand(message);
        if (command == null) {
            // Ignore
            return;
        }

        if (command.isAdmin()) {
            // Check if user is admin
            return;
        }

        // Finally execute the command.
        command.execute(event);

    }
}
