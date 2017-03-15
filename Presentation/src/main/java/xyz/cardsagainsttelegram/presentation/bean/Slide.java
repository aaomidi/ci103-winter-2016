package xyz.cardsagainsttelegram.presentation.bean;

import lombok.RequiredArgsConstructor;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

@RequiredArgsConstructor
public class Slide {
    private final String text;

    public SendableTextMessage.SendableTextMessageBuilder getMessage() {
        return SendableTextMessage.builder().message(text).parseMode(ParseMode.MARKDOWN);
    }

    public String getMessageText() {
        return text;
    }
}
