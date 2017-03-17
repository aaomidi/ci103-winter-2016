package xyz.cardsagainsttelegram.bean.game;

import xyz.cardsagainsttelegram.bean.card.BlackCard;
import xyz.cardsagainsttelegram.bean.card.WhiteCard;

import java.util.List;

public class RoundStats {
    private final Player winner;
    private final BlackCard blackCard;
    private final List<WhiteCard> whiteCardList;

    public RoundStats(Player winner, BlackCard blackCard, List<WhiteCard> whiteCardList) {
        this.winner = winner;
        this.blackCard = blackCard;
        this.whiteCardList = whiteCardList;
    }
}
