package xyz.cardsagainsttelegram.engine.commands;

import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.InputFile;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendablePhotoMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;
import xyz.cardsagainsttelegram.bean.command.Command;
import xyz.cardsagainsttelegram.bean.game.Player;

public class StartCommand extends Command {
    public StartCommand(CardsAgainstTelegram instance) {
        super(instance, "start", "Starts the bot.", false, true);
    }

    @Override
    public boolean execute(Player player, CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();
        if (event.getArgs().length == 0) {
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
        } else {
            String key = event.getArgs()[0];
            if (player.join(key)) {
                player.send("You have joined %s! Remember to say hi a**hole!", player.getLobby().getName());
            }
        }
        return true;
    }
}
