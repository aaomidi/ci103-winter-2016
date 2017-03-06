package xyz.cardsagainsttelegram.bean.card;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Pack implements Cloneable {
    private final String packName;
    private final List<BlackCard> blacks;
    private final List<WhiteCard> whites;

    public Pack(String packName) {
        this(packName, new LinkedList<>(), new LinkedList<>());
    }

    private Pack(String packName, List<BlackCard> blacks, List<WhiteCard> whites) {
        this.packName = packName;
        this.blacks = blacks;
        this.whites = whites;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        List<BlackCard> bs = new LinkedList<>();
        List<WhiteCard> ws = new LinkedList<>();
        for (BlackCard b : blacks) {
            bs.add((BlackCard) b.clone());
        }

        for (WhiteCard w : whites) {
            ws.add((WhiteCard) w.clone());
        }

        return new Pack(packName, bs, ws);
    }
}
