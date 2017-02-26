package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;

public class JoinCommand extends Command {
    public JoinCommand(CardsAgainstTelegram instance) {
        super(instance, "join", "Joins an existing lobby using its key.", false, false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        if (player.hasLobby()) {
            event.getChat().sendMessage("You can not create a lobby. You already are part of a lobby.\nYou can use /leave to leave your lobby.");
            return true;
        }
        if (event.getArgs().length > 1) {
            event.getChat().sendMessage("Incorrect format. \nPlease try \"join KEY\"");
            return true;
        }

        String key = event.getArgsString();
        LobbyRegistry.joinLobby(player, key);

        return true;
    }
}