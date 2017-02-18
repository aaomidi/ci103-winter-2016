package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;

/**
 * Created by amir on 2/18/17.
 */
public class ShareCommand extends Command {
    public ShareCommand(CardsAgainstTelegram instance) {
        super(instance, "share", "Provides a link you can use to share the lobby with others.", false, true);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        if (!player.hasLobby()) {

        }
        return true;
    }
}
