package xyz.cardsagainsttelegram.presentation;

import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import xyz.cardsagainsttelegram.presentation.engine.commands.StartCommand;
import xyz.cardsagainsttelegram.presentation.engine.handlers.PresentationHandler;
import xyz.cardsagainsttelegram.presentation.engine.handlers.TelegramHandler;
import xyz.cardsagainsttelegram.presentation.util.Updater;

public class Presentation {
    private TelegramHandler telegramHandler;

    /**
     * Entry point to Presentation class
     *
     * @param args startup args
     */
    public Presentation(String... args) {
        telegramHandler = new TelegramHandler(this, args[0]);
        new Thread(new Updater(this)).start();

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

    /**
     * Main entry point
     * @param args startup args
     */
    public static void main(String... args) {
        new Presentation(args);
    }

    /**
     * Telegram Bot instance
     * @return null if not initiated
     */
    public TelegramBot getBot() {
        return telegramHandler.getBot();
    }

    /**
     * Command registeration system
     */
    private void registerCommands() {
        new StartCommand(this);
    }

    /**
     * Messages the team with message.
     * @param msg
     */
    public void tellTelegram(String msg) {
        Chat chat = getBot().getChat("-1001081498579");
        chat.sendMessage(msg);
    }
}
