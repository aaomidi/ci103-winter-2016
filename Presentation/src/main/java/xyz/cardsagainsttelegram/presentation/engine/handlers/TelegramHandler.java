package xyz.cardsagainsttelegram.presentation.engine.handlers;


import lombok.Getter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.presentation.Presentation;
import xyz.cardsagainsttelegram.presentation.bean.Command;

public class TelegramHandler implements Listener {
    private final Presentation instance;
    @Getter
    private final TelegramBot bot;

    public TelegramHandler(Presentation instance, String key) {
        this.instance = instance;

        bot = TelegramBot.login(key);
        bot.startUpdates(false);
        bot.getEventsManager().register(this);
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        String message = event.getCommand();
        Command command = CommandRegistry.getCommand(message);
        if (command == null) {
            // Ignore
            return;
        }

        Chat chat = event.getChat();

        // Finally execute the command.
        command.execute(event.getMessage().getSender(), event);
    }

}
