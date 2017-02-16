package xyz.cardsagainsttelegram.bean.deckfile;

import lombok.Data;

import java.util.List;

@Data
public class DeckInfo {
    private final String name;
    private final List<Integer> black;
    private final List<Integer> white;
}
