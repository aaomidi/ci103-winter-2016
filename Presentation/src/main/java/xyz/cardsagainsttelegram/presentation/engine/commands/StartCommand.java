package xyz.cardsagainsttelegram.presentation.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.presentation.Presentation;
import xyz.cardsagainsttelegram.presentation.bean.Command;

public class StartCommand extends Command {
    public StartCommand(Presentation instance, String name, String description) {
        super(instance, name, description);
    }

    @Override
    public boolean execute(User user, CommandMessageReceivedEvent event) {
        return false;
    }
}
