package xyz.cardsagainsttelegram.files.deckfile;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;

@RequiredArgsConstructor
public class DeckFile {
    private final ArrayList<BlackDeckFile> blackCards;
    private final ArrayList<String> whiteCards;
    private final HashMap<String, DeckFile> map;
}
