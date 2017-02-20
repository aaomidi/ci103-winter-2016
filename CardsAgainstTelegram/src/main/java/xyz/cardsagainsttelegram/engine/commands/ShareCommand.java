package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;
import xyz.cardsagainsttelegram.utils.Strings;

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
                .plain("link to join ").plain(player.getLobby().getName()).plain(".")
                .newLine()
                .newLine()
                .italics("If that link does not work, please message ").plain(instance.getBot().getBotUsername()).italics(" with the following message:")
                .newLine()
                .newLine()
                .plain("/join ").plain(player.getLobby().getKey())
                .buildText().build();

        player.send(message);
        return true;
    }
}
