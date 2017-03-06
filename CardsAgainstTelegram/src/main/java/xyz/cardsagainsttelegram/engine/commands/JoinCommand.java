package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;

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
        // Why care if its a longer arg? Let's just attempt to join them.
        if (event.getArgs().length == 0) {
            event.getChat().sendMessage("You need to enter the ID for the lobby you want to join!");
            return true;
        }

        String key = event.getArgsString();
        player.join(key);
        return true;
    }
}