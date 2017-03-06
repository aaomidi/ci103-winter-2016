package xyz.cardsagainsttelegram.bean.card;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.cardsagainsttelegram.bean.CAHInputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class BlackCard implements Card {
    // Used as a global cache
    private static transient HashMap<BlackCard, CAHInputStream> cachedInputStream = new HashMap<>();

    private static transient Font font = new Font("Comic Sans MS", Font.BOLD, 14);
    @Getter
    private final CardType type = CardType.BLACK;
    @Getter
    private final String text;
    @Getter
    private final int empty;

    // This is used when we're constructing the completed black card.
    private transient boolean hasChanged;
    @Getter
    private transient List<String> choices;

    public void setChoices(List<String> choices) {
        hasChanged = true;
        this.choices = choices;
    }

    public String getTextAlternative() {
        return text;
    }

    public CAHInputStream drawImage() {
        CAHInputStream buffer = cachedInputStream.get(this);

        if (buffer != null && !hasChanged) {
            try {
                buffer.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer;
        }

        System.out.println(font.getFontName());

        BufferedImage img = null;
        try {
            InputStream is = this.getClass().getResourceAsStream("/bCard.png");

            img = ImageIO.read(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Graphics2D g = img.createGraphics();

        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawString(g, text, 30, 50);
        g.dispose();

        buffer = bufferedImageToIS(img);

        cachedInputStream.put(this, buffer);
        return buffer;
    }

    private CAHInputStream bufferedImageToIS(BufferedImage image) {
        CAHInputStream buffer;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            InputStream i = new ByteArrayInputStream(baos.toByteArray());
            BufferedInputStream buffered = new BufferedInputStream(i);
            buffer = new CAHInputStream(buffered, baos.size() + 2);
            buffer.mark(baos.size() + 1);
            return buffer;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private void drawString(Graphics g, String text, int x, int y) {
        int maxX = 215;

        int printingX = x;
        int printingY = y;
        FontMetrics m = g.getFontMetrics();
        for (String word : text.split(" ")) {
            int len = m.stringWidth(word);
            if (printingX + len > maxX) {
                printingX = x; // Restore back to starting
                printingY += m.getHeight() + 2;
            }
            g.drawString(word, printingX, printingY);

            printingX += len;
            printingX += m.charWidth(' ');
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new BlackCard(text, empty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlackCard blackCard = (BlackCard) o;

        if (empty != blackCard.empty) return false;
        if (hasChanged != blackCard.hasChanged) return false;
        return text.equals(blackCard.text);
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + empty;
        result = 31 * result + (hasChanged ? 1 : 0);
        return result;
    }
}
