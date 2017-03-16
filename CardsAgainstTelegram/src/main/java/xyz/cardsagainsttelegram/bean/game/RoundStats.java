package xyz.cardsagainsttelegram.bean.game;

import lombok.Data;
import xyz.cardsagainsttelegram.bean.card.BlackCard;
import xyz.cardsagainsttelegram.bean.card.WhiteCard;

import java.util.List;

@Data
public class RoundStats {
    private Player winner;
    private BlackCard blackCard;
    private List<WhiteCard> whiteCardList;
}
