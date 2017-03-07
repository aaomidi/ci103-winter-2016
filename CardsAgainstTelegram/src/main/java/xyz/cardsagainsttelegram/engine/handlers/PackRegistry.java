package xyz.cardsagainsttelegram.engine.handlers;

import xyz.cardsagainsttelegram.bean.card.Pack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class PackRegistry {
    private static final HashMap<String, Pack> packs = new HashMap<>();

    public static void registerPacks(List<Pack> pl) {
        for (Pack p : pl) {
            packs.put(p.getPackName().toLowerCase(), p);
        }
    }

    public static Collection<Pack> getPacks() {
        return packs.values();
    }

    public static List<String> getPacksString() {
        List<String> list = new ArrayList<>();
        for (Pack pack : getPacks()) {
            list.add(pack.getPackName());
        }
        return list;
    }

    public static Pack getPack(String s) {
        try {
            return (Pack) packs.get(s.toLowerCase()).clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
