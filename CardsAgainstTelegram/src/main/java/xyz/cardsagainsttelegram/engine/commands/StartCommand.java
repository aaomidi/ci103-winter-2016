package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.ChatType;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
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
            event.getChat().sendMessage("You need to send a private message to me.");
            return false;
        }

        // This is cancer atm. Not using it.
        /*if (event.getArgs().length == 0) {
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
        }*/

        SendableMessage message = SendableTextMessage.builder().textBuilder()
                .bold("WELCOME TO THE MONKEY HOUSE " + player.getEffectiveName()).newLine()
                .plain("mmmmm fresh meat :)").newLine()
                .plain("Welcome to a bot made for terrible and horrible people by terrible and horrible people.").newLine()
                .plain("This bot is for you if you're one of the following: ").newLine()
                .plain("\t- You're a \"nice guy\" who's angry at the world").newLine()
                .plain("\t- You have small hands ").italics("(plz don't deport me)").newLine()
                .plain("\t- You bully children on club penguin").newLine()
                .plain("\t- You believe in \"alternative facts\"").newLine()
                .plain("\t- You want to have fun with dark humor").newLine()
                .plain("\t- You use the following face in very, very weird situations: " + "( ͡° ͜ʖ ͡°)").newLine()
                .newLine()
                .plain("Actually I don't care why you're here. So yeah, wanna start playing cards against telegram? I'm not here to help you.").newLine()
                .plain("I've heard people have some luck with the guy who manages /help. I don't trust him though. kthx").newLine()
                .plain("Here. have a picture of my cock to get yourself started")
                .buildText().build();

        player.send(message);

        message = SendablePhotoMessage.builder()
                .photo(new InputFile(this.getClass().getResourceAsStream("/cock.jpg"), "cock.jpg"))
                .build();

        player.send(message);
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
