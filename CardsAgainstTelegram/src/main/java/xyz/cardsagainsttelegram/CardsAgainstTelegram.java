package xyz.cardsagainsttelegram;

import lombok.Getter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import xyz.cardsagainsttelegram.engine.commands.*;
import xyz.cardsagainsttelegram.engine.files.CardReader;
import xyz.cardsagainsttelegram.engine.handlers.TelegramHandler;
import xyz.cardsagainsttelegram.utils.Updater;

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
        new Thread(new Updater(this)).start();

        telegramHandler = new TelegramHandler(args[0], this);

        cardReader = new CardReader();

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
     * Alphabetical order
     */
    private void registerCommands() {
        new BotFatherCommand(this);
        new CreateLobbyCommand(this);
        new HelpCommand(this);
        new InfoCommand(this);
        new JoinCommand(this);
        new LeaveCommand(this);
        new NameCommand(this);
        new ShareCommand(this);
        new StartCommand(this);
    }

    public TelegramBot getBot() {
        return telegramHandler.getBot();
    }

    public void tellAdmins(String msg) {
        /*for (Player player : PlayerRegistry.getPlayers()) {
            if (!player.isAdmin()) continue;
            player.send(msg);
        }*/

        getBot().getChat("-1001081498579").sendMessage("Bot update found. Restarting...");
    }
}
