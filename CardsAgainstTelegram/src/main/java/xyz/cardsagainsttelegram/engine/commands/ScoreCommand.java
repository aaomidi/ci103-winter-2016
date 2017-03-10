package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Lobby;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.bean.game.enums.LobbyState;

public class ScoreCommand extends Command {
    public ScoreCommand(CardsAgainstTelegram instance) {
        super(instance, "score", "Displays the in game score.", false, true);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        if (!player.hasLobby()) {
            player.send("Lobby not started or found.");
            return true;
        }

        Lobby lobby = player.getLobby();
        LobbyState lobbyState = lobby.getLobbyState();

        if (lobbyState != LobbyState.PRE_ROUND && lobbyState != LobbyState.PLAYERS_PICKING && lobbyState != LobbyState.CZAR_PICKING) {
            player.send("Game not started or found.");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        for (Player players : lobby.getPlayers())
            sb.append(String.format("*%s* - %s", players.getName(), players.getGameWins())).append("\n");

        player.send(ParseMode.MARKDOWN, sb.toString());
        return true;

    }

}