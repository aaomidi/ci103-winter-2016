package xyz.cardsagainsttelegram.bean.card;

import lombok.Data;
import lombok.Getter;

@Data
public class WhiteCard implements Card {
    @Getter
    private final CardType type = CardType.WHITE;
    @Getter
    private final String text;
}
