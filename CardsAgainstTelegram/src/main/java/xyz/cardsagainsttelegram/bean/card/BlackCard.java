package xyz.cardsagainsttelegram.bean.card;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.cardsagainsttelegram.bean.CAHInputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@RequiredArgsConstructor
public class BlackCard implements Card {
    private static transient Font font = new Font("Comic Sans MS", Font.BOLD, 14);
    @Getter
    private final CardType type = CardType.BLACK;
    @Getter
    private final String text;
    @Getter
    private final int empty;
    private transient CAHInputStream buffer;

    public String getTextAlternative() {
        return text;
    }

    public CAHInputStream drawImage() {
        if (buffer != null) {
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

        bufferedImagetoIS(img);
        return buffer;
    }

    private void bufferedImagetoIS(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            InputStream i = new ByteArrayInputStream(baos.toByteArray());
            BufferedInputStream buffered = new BufferedInputStream(i);
            buffer = new CAHInputStream(buffered, baos.size() + 2);
            buffer.mark(baos.size() + 1);

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
}
