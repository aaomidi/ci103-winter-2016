package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.bean.game.enums.GenericResult;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyResult;
import xyz.cardsagainsttelegram.utils.Strings;

public class NameCommand extends Command {

    public NameCommand(CardsAgainstTelegram instance) {
        super(instance, "name", "Changes the name of the lobby.", false, true);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        if (!player.hasLobby()) {
            player.send(Strings.getString(LobbyResult.PLAYER_NO_LOBBY));
            return true;
        }

        if (!player.getLobby().getOwner().equals(player)) {
            player.send(Strings.getString(LobbyResult.PLAYER_NOT_OWN_LOBBY));
            return true;
        }

        if (event.getArgs().length == 0) {
            player.send(Strings.getString(GenericResult.NOT_ENOUGH_ARGS));
            return true;
        }

        String newName = event.getArgsString();
        player.getLobby().setName(newName);
        return true;
    }
}
