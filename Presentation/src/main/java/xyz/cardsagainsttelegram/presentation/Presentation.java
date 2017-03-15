package xyz.cardsagainsttelegram.presentation;

import pro.zackpollard.telegrambot.api.TelegramBot;
import xyz.cardsagainsttelegram.presentation.engine.commands.StartCommand;
import xyz.cardsagainsttelegram.presentation.engine.handlers.PresentationHandler;
import xyz.cardsagainsttelegram.presentation.engine.handlers.TelegramHandler;

public class Presentation {
    private TelegramHandler telegramHandler;

    public Presentation(String... args) {
        telegramHandler = new TelegramHandler(this, args[0]);
        PresentationHandler.readStories();
        registerCommands();

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String... args) {
        new Presentation(args);


    }

    public TelegramBot getBot() {
        return telegramHandler.getBot();
    }

    private void registerCommands() {
        new StartCommand(this);
    }
}
