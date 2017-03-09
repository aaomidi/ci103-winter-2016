package xyz.cardsagainsttelegram.bean.game;

import lombok.Data;
import xyz.cardsagainsttelegram.bean.card.WhiteCard;

import java.util.LinkedList;

@Data
public class Pick {
    private final Player player;
    private final LinkedList<WhiteCard> picks;
}
