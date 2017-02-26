package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Lobby;
import xyz.cardsagainsttelegram.bean.game.Player;

import java.util.List;


public class InfoCommand extends Command {
    public InfoCommand(CardsAgainstTelegram instance) {
        super(instance, "info", "Show current lobby and lobby player's information.", false, false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        StringBuilder sb = new StringBuilder();

        if (player.hasLobby()) {

            Lobby lobby = player.getLobby();

            List<Player> players = lobby.getPlayers();

            String currentLobby = lobby.getName();
            String shareLink = lobby.getShareLink();
            String owner = lobby.getOwner().getName();
            int playerCount = lobby.getPlayerCount();

            sb
                    .append("=== Lobby Information ===").append("\n")
                    .append("Name: ").append(currentLobby).append("\n")
                    .append("Owner: ").append(owner).append("\n")
                    .append("Number of Players: ").append(playerCount).append("\n")
                    .append("Shareable Link: ").append(shareLink).append("\n\n");

            /* This would get too spammy. We can add different commands for this
            for (Player currentPlayer : players) {
                int wins = currentPlayer.getWins();
                String playerName = currentPlayer.getName();
                String username = currentPlayer.getUsername();

                sb
                        .append("=== Players Information ===").append("\n")
                        .append(playerName).append("'s Username: ").append(username).append("\n")
                        .append(playerName).append("'s Wins: ").append(wins).append("\n");
            } */
            event.getChat().sendMessage(sb.toString().trim());
        } else {
            event.getChat().sendMessage("You must be in a lobby to use this command.");
        }
        return true;
    }
}