package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;

public class CreateLobbyCommand extends Command {
    public CreateLobbyCommand(CardsAgainstTelegram instance) {
        super(instance, "createlobby", "Creates a cards against telegram lobby.", false, true);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        boolean res = LobbyRegistry.createLobby(player);
        if (!res) {
            player.send("You can not create a lobby. You already are part of a lobby.\nYou can use /leave to leave your lobby.");
            return true;
        }
        event.getChat().sendMessage("Lobby creation successful! Do /share to share a link to your lobby with others.");
        return false;
    }
}
