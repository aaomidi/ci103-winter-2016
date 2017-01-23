package xyz.cardsagainsttelegram.bean;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class BlackCard implements Card {
    @Getter
    private final CardType type = CardType.BLACK;
    @Getter
    private final String text;
    @Getter
    private final int empty;
}
