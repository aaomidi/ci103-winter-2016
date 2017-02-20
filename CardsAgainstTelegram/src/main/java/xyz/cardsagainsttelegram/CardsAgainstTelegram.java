package xyz.cardsagainsttelegram;

import lombok.Getter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import xyz.cardsagainsttelegram.engine.commands.*;
import xyz.cardsagainsttelegram.engine.files.CardReader;
import xyz.cardsagainsttelegram.engine.handlers.TelegramHandler;

public class CardsAgainstTelegram {
    @Getter
    private CardReader cardReader;
    @Getter
    private TelegramHandler telegramHandler;

    private CardsAgainstTelegram(String... args) {
        run(args);
    }

    public static void main(String... args) {
        assert args.length > 0;
        new CardsAgainstTelegram(args);
    }

    private void run(String... args) {
        cardReader = new CardReader();
        telegramHandler = new TelegramHandler(args[0], this);

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
     * Add any new commands here.
     */
    private void registerCommands() {
        new CreateLobbyCommand(this);
        new HelpCommand(this);
        new NameCommand(this);
        new ShareCommand(this);
        new StartCommand(this);
    }

    public TelegramBot getBot() {
        return telegramHandler.getBot();
    }
}
