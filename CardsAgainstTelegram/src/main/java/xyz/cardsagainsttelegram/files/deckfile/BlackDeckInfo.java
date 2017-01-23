package xyz.cardsagainsttelegram.files.deckfile;

import lombok.RequiredArgsConstructor;
import xyz.cardsagainsttelegram.bean.BlackCard;

@RequiredArgsConstructor
public class BlackDeckInfo {
    private final String text;
    private final int pick;

    public BlackCard toBlackCard() {
        return new BlackCard(text, pick);
    }
}
