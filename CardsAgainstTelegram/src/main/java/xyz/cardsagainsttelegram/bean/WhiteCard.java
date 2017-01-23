package xyz.cardsagainsttelegram.bean;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class WhiteCard implements Card {
    @Getter
    private final CardType type = CardType.WHITE;
    @Getter
    private final String text;
}
