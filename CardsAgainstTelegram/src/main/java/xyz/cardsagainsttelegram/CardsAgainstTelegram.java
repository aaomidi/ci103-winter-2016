package xyz.cardsagainsttelegram;

import xyz.cardsagainsttelegram.commands.HelpCommand;
import xyz.cardsagainsttelegram.files.CardReader;
import xyz.cardsagainsttelegram.handlers.TelegramHandler;

public class CardsAgainstTelegram {
    private CardReader cardReader;

    public CardsAgainstTelegram(String... args) {
        cardReader = new CardReader();
        new TelegramHandler(args[0]);

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
        assert args.length > 0;
        new CardsAgainstTelegram(args);
    }

    /**
     * Add any new commands here.
     */
    public void registerCommands() {
        new HelpCommand(this);
    }
}
