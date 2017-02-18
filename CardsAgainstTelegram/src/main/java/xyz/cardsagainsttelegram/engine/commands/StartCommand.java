package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.menu.InlineMenu;
import pro.zackpollard.telegrambot.api.menu.InlineMenuBuilder;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;
import xyz.cardsagainsttelegram.engine.handlers.LobbyRegistry;
import xyz.cardsagainsttelegram.utils.Strings;

public class StartCommand extends Command {
    public StartCommand(CardsAgainstTelegram instance) {
        super(instance, "start", "Starts the bot", false);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        if (chat.getType() != ChatType.PRIVATE) {
            //event.getChat().sendMessage("You need to send a private message to me.");
            //return false;
        }
        if (event.getArgs().length == 0) {
            InlineMenuBuilder builder = startCreator(player);

            player.sendInlineMenu(builder.buildMenu());
        } else {
            if (player.join(event.getArgs()[0])) {
                player.sendInlineMenu(startCreator(player).buildMenu());
            }

            String key = event.getArgs()[0];
            if (player.join(key)) {
                player.sendInlineMenu(startCreator(player).buildMenu());
            }
        }

        return true;
    }

    public InlineMenuBuilder startCreator(Player player) {
        InlineMenuBuilder builder = InlineMenu.builder(instance.getTelegramHandler().getBot());
        builder
                .forWhom(player.getChat())
                .message(SendableTextMessage.plain("Cards Against Telegram Game Panel"));

        if (!player.canCreateLobby()) { // Player is part of lobby
            builder
                    .newRow()
                    .menuButton(Strings.LOBBY_SETTINGS + " Lobby Settings")
                    .nextMenu(LobbyRegistry.lobbySubMenu(player, builder))
                    .newRow()
                    .toggleButton(Strings.LEAVE_LOBBY + " Leave lobby")
                    .toggleCallback((b, v) -> {
                        player.send("Left lobby.");
                        return null;
                    })
                    .build().build();
        } else {
            builder
                    .newRow()
                    .toggleButton(Strings.CREATE_LOBBY + " Create Lobby")
                    //.nextMenu(LobbyRegistry.lobbySubMenu(player, builder))
                    .toggleCallback((b, v) -> {
                        player.send("Creating your lobby!");
                        LobbyRegistry.createLobby(player);
                        player.sendInlineMenu(startCreator(player).buildMenu());
                        return null;
                    })
                    .build()
                    .newRow()
                    .inputButton(Strings.JOIN_LOBBY + " Join Lobby")
                    .buttonCallback(b -> "Send me the ID of the lobby")
                    .textCallback((button, key) -> {
                        // handle joining logic.
                        if (player.join(key)) {
                            player.sendInlineMenu(startCreator(player).buildMenu());
                        }
                    })
                    .build().build();
        }
        return builder;
    }
}
