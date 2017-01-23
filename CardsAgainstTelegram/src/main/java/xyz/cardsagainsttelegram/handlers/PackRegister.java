package xyz.cardsagainsttelegram.handlers;

import xyz.cardsagainsttelegram.bean.Pack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PackRegister {
    private static final HashMap<String, Pack> packs = new HashMap<>();

    public static void registerPacks(List<Pack> pl) {
        for (Pack p : pl) {
            packs.put(p.getPackName().toLowerCase(), p);
        }
    }

    public static Collection<Pack> getPacks() {
        return packs.values();
    }

    public static Pack getPack(String s) {
        return packs.get(s.toLowerCase());
    }
}
