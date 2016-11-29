package xyz.cardsagainsttelegram.files.deckfile;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
public class BaseFile {
    private final String name;
    private final ArrayList<Integer> black;
    private final ArrayList<Integer> white;
}
