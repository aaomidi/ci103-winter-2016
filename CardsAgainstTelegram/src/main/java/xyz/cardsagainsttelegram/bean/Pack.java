package xyz.cardsagainsttelegram.bean;

import lombok.RequiredArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class Pack {
    private final String packName;
    private final List<BlackCard> blacks = new LinkedList<>();
    private final List<WhiteCard> whites = new LinkedList<>();
}
