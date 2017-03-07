package xyz.cardsagainsttelegram.bean.deckfile;

import lombok.Data;
import xyz.cardsagainsttelegram.bean.card.Pack;
import xyz.cardsagainsttelegram.bean.card.WhiteCard;
import xyz.cardsagainsttelegram.engine.handlers.PackRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class DeckFile {
    // The list of Black cards, simply TEXT and PICK
    private final ArrayList<BlackDeckInfo> blackCards;
    // The list of strings as white cards
    private final ArrayList<String> whiteCards;

    public void register(Map<Object, Object> m) {
        List<Pack> list = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : m.entrySet()) {
            String key = entry.getKey().toString();
            if (key.equalsIgnoreCase("blackCards") || key.equalsIgnoreCase("whiteCards")) continue;
            Object value = entry.getValue();
            DeckInfo df = cToDF(value);
            if (df == null) continue;

            Pack pack = new Pack(df.getName());
            for (Integer i : df.getBlack()) {
                pack.getBlacks().add(blackCards.get(i).toBlackCard());
            }

            for (Integer i : df.getWhite()) {
                pack.getWhites().add(new WhiteCard(whiteCards.get(i)));
            }
            list.add(pack);
        }

        PackRegistry.registerPacks(list);
    }

    /**
     * Converts a Map object into a Deck Info File
     *
     * @param o
     * @return
     */
    private DeckInfo cToDF(Object o) {
        if (!(o instanceof Map)) {
            return null;
        }
        Map<Object, Object> map = (Map<Object, Object>) o;
        String name = (String) map.get("name");
        List<Integer> black = null, white = null;
        try {
            black = toIL((List<Object>) map.get("black"));
            white = toIL((List<Object>) map.get("white"));
        } catch (Exception ex) {
            System.out.println(name);
        }


        return new DeckInfo(name, black, white);
    }

    /**
     * Converts an Object list into an Integer list
     *
     * @param list
     * @return
     */
    private List<Integer> toIL(List<Object> list) {
        List<Integer> l = new ArrayList<>();
        for (Object o : list) {
            Integer i = Double.valueOf(o.toString()).intValue();
            l.add(i);
        }

        return l;

    }
}
