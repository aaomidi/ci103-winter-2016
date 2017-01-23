package xyz.cardsagainsttelegram;

import xyz.cardsagainsttelegram.files.CardReader;
import xyz.cardsagainsttelegram.handlers.TelegramHandler;

public class CardsAgainstTelegram {
    private CardReader cardReader;

    public static void main(String... args) {
        assert args.length > 0;
        new CardsAgainstTelegram(args);
    }

    public CardsAgainstTelegram(String... args) {
        cardReader = new CardReader();
        new TelegramHandler(args[0]);
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
