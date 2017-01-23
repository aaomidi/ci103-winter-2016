package xyz.cardsagainsttelegram.files.deckfile;

import lombok.Data;
import xyz.cardsagainsttelegram.bean.Pack;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class DeckFile {
    // The list of Black cards, simply TEXT and PICK
    private final ArrayList<BlackDeckInfo> blackCards;
    // The list of strings as white cards
    private final ArrayList<String> whiteCards;
    // Deck name, to Deck info
    private final HashMap<String, DeckInfo> map;

    public Pack convertToPack() {
        // This will convert this json file into a useful Object
        return null;
    }
}
