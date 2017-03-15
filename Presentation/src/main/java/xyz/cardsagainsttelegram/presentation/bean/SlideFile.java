package xyz.cardsagainsttelegram.presentation.bean;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SlideFile {
    @Getter
    private final List<Slide> slides;
}
