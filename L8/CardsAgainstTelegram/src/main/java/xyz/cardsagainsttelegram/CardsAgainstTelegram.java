package xyz.cardsagainsttelegram;

import xyz.cardsagainsttelegram.handlers.TelegramHandler;

public class CardsAgainstTelegram {
    public static void main(String... args) {
        assert args.length > 0;
        new CardsAgainstTelegram(args);
    }

    public CardsAgainstTelegram(String... args) {
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
