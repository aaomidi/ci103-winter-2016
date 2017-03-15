package xyz.cardsagainsttelegram.presentation.engine.handlers;


import com.google.gson.Gson;
import xyz.cardsagainsttelegram.presentation.Presentation;
import xyz.cardsagainsttelegram.presentation.bean.Slide;
import xyz.cardsagainsttelegram.presentation.bean.SlideFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PresentationHandler {
    public static final HashMap<Integer, Slide> slides = new HashMap<>();
    private static final Gson gson = new Gson();

    public static void readStories() {
        InputStream is = Presentation.class.getResourceAsStream("/presentation.json");
        readToSlides(is);
    }

    private static void readToSlides(InputStream is) {
        SlideFile slideFile = gson.fromJson(new InputStreamReader(is), SlideFile.class);
        int i = 0;
        for (Slide slide : slideFile.getSlides()) {
            slides.put(i++, slide);
        }
    }

    public static Slide getSlide(int i) {
        return slides.get(i);
    }
}
