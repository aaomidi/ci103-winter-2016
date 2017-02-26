package xyz.cardsagainsttelegram.engine.handlers;

import lombok.Getter;
import pro.zackpollard.telegrambot.api.TelegramBot;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.TextMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;

public class TelegramHandler implements Listener {
    @Getter
    private static String BOT_USERNAME;
    private final CardsAgainstTelegram instance;
    @Getter
    private final TelegramBot bot;

    public TelegramHandler(String apiKey, CardsAgainstTelegram instance) {
        bot = TelegramBot.login(apiKey);
        this.instance = instance;
        bot.startUpdates(true);
        bot.getEventsManager().register(this);

        BOT_USERNAME = bot.getBotUsername();
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
        if (command.isPrivate() && chat.getType() != ChatType.PRIVATE) {
            event.getChat().sendMessage("You need to send a private message to me.");
            return;
        }

        Player player = PlayerRegistry.getPlayer(instance, event.getMessage().getSender());
        System.out.println(player.getEffectiveName() + " " + event.getCommand() + " " + event.getArgsString());

        if (command.isAdmin()) {
            if (player.isAdmin()) {
                command.execute(player, event);
            } else {
                // Ignore
            }
            return;
        }

        // Finally execute the command.
        command.execute(player, event);
    }

    public void onTextMessageReceived(TextMessageReceivedEvent event) {
        String message = event.getContent().getContent();
        Chat chat = event.getChat();

        if (chat.getType() != ChatType.PRIVATE) return;

        User user = event.getMessage().getSender();

        Player player = PlayerRegistry.getPlayer(instance, user);

        if (player.getLobby() != null) {
            player.getLobby().relayMessage(player, message);
        }
    }
}
