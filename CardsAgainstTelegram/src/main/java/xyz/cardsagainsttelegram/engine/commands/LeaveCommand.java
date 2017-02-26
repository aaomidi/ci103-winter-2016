package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;

public class LeaveCommand extends Command {
    public LeaveCommand(CardsAgainstTelegram instance) {
        super(instance, "leave", "Leaves the player's current lobby.", false, true);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        if (player.hasLobby())
            player.leave();
        else
            event.getChat().sendMessage("You are not in a lobby.");
        return true;
    }
}
