package xyz.cardsagainsttelegram.handlers;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

public class TelegramHandler implements Listener {
    private final TelegramBot bot;

    public TelegramHandler(String apiKey) {
        bot = TelegramBot.login(apiKey);
        bot.startUpdates(false);
        bot.getEventsManager().register(this);
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        Message message = event.getMessage();
        chat.sendMessage(String.format("Hi %s! I'm alive!", message.getSender().getFullName()));
    }
}
