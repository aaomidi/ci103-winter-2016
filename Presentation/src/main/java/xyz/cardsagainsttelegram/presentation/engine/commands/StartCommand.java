package xyz.cardsagainsttelegram.presentation.engine.commands;

import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;
import pro.zackpollard.telegrambot.api.menu.InlineMenu;
import pro.zackpollard.telegrambot.api.menu.InlineMenuBuilder;
import pro.zackpollard.telegrambot.api.menu.InlineMenuRowBuilder;
import pro.zackpollard.telegrambot.api.user.User;
import xyz.cardsagainsttelegram.presentation.Presentation;
import xyz.cardsagainsttelegram.presentation.bean.Command;
import xyz.cardsagainsttelegram.presentation.bean.PresentationConsumer;
import xyz.cardsagainsttelegram.presentation.engine.handlers.PresentationHandler;


public class StartCommand extends Command {
    public StartCommand(Presentation instance) {
        super(instance, "start", "Starts the bot");
    }

    @Override
    public boolean execute(User user, CommandMessageReceivedEvent event) {
        PresentationConsumer con1 = new PresentationConsumer(false, 0);
        PresentationConsumer con2 = new PresentationConsumer(true, 0);

        con1.setOther(con2);
        con2.setOther(con1);

        InlineMenuBuilder builder = InlineMenu.builder(instance.getBot(), event.getChat());
        builder.message(PresentationHandler.getSlide(0).getMessage());

        InlineMenuRowBuilder<InlineMenuBuilder> rowBuilder = builder.newRow();

        rowBuilder.dummyButton("⬅").callback(con1).build();

        rowBuilder.dummyButton("➡").callback(con2).build();

        InlineMenu menu = rowBuilder.build().buildMenu();

        InlineKeyboardMarkup kb = menu.toKeyboard();

        con1.setMarkup(kb);
        con2.setMarkup(kb);

        menu.start();

        return true;
    }
}
