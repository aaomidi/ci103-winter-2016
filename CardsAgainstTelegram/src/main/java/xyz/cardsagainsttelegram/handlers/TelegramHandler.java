package xyz.cardsagainsttelegram.handlers;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.bean.BlackCard;
import xyz.cardsagainsttelegram.bean.Pack;
import xyz.cardsagainsttelegram.bean.WhiteCard;

import java.io.File;

public class TelegramHandler implements Listener {
    private final TelegramBot bot;

    public TelegramHandler(String apiKey) {
        bot = TelegramBot.login(apiKey);
        bot.startUpdates(true);
        bot.getEventsManager().register(this);
    }

    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        Message message = event.getMessage();
        chat.sendMessage(String.format("Hi %s!\n\tYour username is %s\n\tYou said %s %s!", message.getSender().getFullName(), message.getSender().getUsername(), event.getCommand(), event.getArgsString()));
        if (event.getCommand().equalsIgnoreCase("packs")) {
            StringBuilder sb = new StringBuilder("Registered packs:\n");
            for (Pack p : PackRegister.getPacks()) {
                sb.append(" - ").append(p.getPackName()).append("\n");
            }
            chat.sendMessage(sb.toString());
        }

        if (event.getCommand().equalsIgnoreCase("getWhite")) {
            Pack p = PackRegister.getPack(event.getArgsString());
            if (p == null) return;
            StringBuilder sb = new StringBuilder("White cards for: ").append(p.getPackName()).append("\n");
            for (WhiteCard card : p.getWhites()) {
                sb.append(" - ").append(card.getText()).append("\n");
            }
            chat.sendMessage(sb.toString());
        }

        if (event.getCommand().equalsIgnoreCase("getBlack")) {
            Pack p = PackRegister.getPack(event.getArgsString());
            if (p == null) return;
            StringBuilder sb = new StringBuilder("Black cards for: ").append(p.getPackName()).append("\n");
            int x = 0;
            for (BlackCard card : p.getBlacks()) {
                if (x++ == 0) {
                    File file = card.drawImage();
                    chat.sendMessage(SendablePhotoMessage.builder().photo(new InputFile(file)).build());
                }
                sb.append(" - ").append(card.getText()).append("\n");
            }
            chat.sendMessage(sb.toString());
        }
    }
}
