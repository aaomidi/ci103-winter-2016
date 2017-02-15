package xyz.cardsagainsttelegram.bean;

import lombok.Data;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Data
public class BlackCard implements Card {
    @Getter
    private final CardType type = CardType.BLACK;
    @Getter
    private final String text;
    @Getter
    private final int empty;
    private transient Font font = new Font("Impact", Font.PLAIN, 14);

    public InputStream drawImage() {

        BufferedImage img = null;
        try {
            InputStream is = this.getClass().getResourceAsStream("bCard.png");

            img = ImageIO.read(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Graphics2D g = img.createGraphics();

        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawString(g, text, 30, 50);
        g.dispose();
        return bufferedImagetoIS(img);
    }

    public InputStream bufferedImagetoIS(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return new ByteArrayInputStream(baos.toByteArray());
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
}
