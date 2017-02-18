package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;
import xyz.cardsagainsttelegram.utils.Strings;

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
            player.send(Strings.getString(LobbyResult.PLAYER_NO_LOBBY));
            return false;
        }

        SendableMessage message = SendableTextMessage.builder().textBuilder()
                .plain("Click ")
                .link("this ", player.getLobby().getShareLink())
                .plain("link to join ").plain(player.getLobby().getName()).plain(" .")
                .buildText().build();

        player.send(message);
        return true;
    }
}
