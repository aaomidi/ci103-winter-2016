package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.menu.InlineMenu;
import pro.zackpollard.telegrambot.api.menu.InlineMenuBuilder;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;

public class StartCommand extends Command {
    public StartCommand(CardsAgainstTelegram instance) {
        super(instance, "start", "Starts the bot", false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        if (chat.getType() != ChatType.PRIVATE) {
            event.getChat().sendMessage("You need to send a private message to me.");
            return false;
        }

        InlineMenuBuilder builder = InlineMenu.builder(instance.getTelegramHandler().getBot());
        builder
                .forWhom(chat)
                .message(SendableTextMessage.plain("Cards Against Telegram Control Panel"))
                .newRow()
                .toggleButton("\uD83D\uDCDD Create a Lobby")
                .toggleCallback((button, newValue) -> {
                    chat.sendMessage("Hello");
                    return null;
                }).build().build().buildMenu().start();

        return true;
    }
}
