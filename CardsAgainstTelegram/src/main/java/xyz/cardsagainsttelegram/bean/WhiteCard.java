package xyz.cardsagainsttelegram.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WhiteCard implements Card {
    @Getter
    private final CardType type = CardType.WHITE;
    @Getter
    private final String text;
}
