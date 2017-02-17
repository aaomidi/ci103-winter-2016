package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;

public class CreateLobbyCommand extends Command {
    public CreateLobbyCommand(CardsAgainstTelegram instance) {
        super(instance, "createlobby", "Creates a cards against a telegram", false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        if (chat.getType() != ChatType.PRIVATE) {
            event.getChat().sendMessage("You need to send a private message to me.");
            return false;
        }

        boolean res = LobbyRegistry.createLobby(player);
        if (!res) {
            event.getChat().sendMessage("You can not create a lobby. You already are part of a lobby.\nYou can use /leavelobby to leave your lobby.");
            return true;
        }
        event.getChat().sendMessage("Lobby creation successful!");
        return false;
    }
}
