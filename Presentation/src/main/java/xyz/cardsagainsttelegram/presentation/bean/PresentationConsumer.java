package xyz.cardsagainsttelegram.presentation.bean;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pro.zackpollard.telegrambot.api.chat.CallbackQuery;
import pro.zackpollard.telegrambot.api.chat.message.MessageCallbackQuery;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;
import pro.zackpollard.telegrambot.api.menu.button.impl.DummyButton;
import xyz.cardsagainsttelegram.presentation.engine.handlers.PresentationHandler;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class PresentationConsumer implements BiConsumer<DummyButton, CallbackQuery> {
    private final boolean isForward;
    @NonNull
    @Setter
    private int slide;
    @Setter
    private PresentationConsumer other;
    @Setter
    private InlineKeyboardMarkup markup;


    @Override
    public void accept(DummyButton db, CallbackQuery cq) {
        MessageCallbackQuery query = (MessageCallbackQuery) cq;
        Slide s;
        System.out.println(slide);
        if (isForward) {
            s = PresentationHandler.getSlide(slide + 1);
        } else {
            s = PresentationHandler.getSlide(slide - 1);
        }
        if (s == null) {
            query.answer("No more slides", true);
            return;
        }

        if (isForward) {
            slide++;
        } else {
            slide--;
        }

        other.setSlide(slide);

        query.getBotInstance().editMessageText(query.getMessage(),
                s.getMessageText(),
                ParseMode.MARKDOWN,
                false,
                markup
        );

    }
}
