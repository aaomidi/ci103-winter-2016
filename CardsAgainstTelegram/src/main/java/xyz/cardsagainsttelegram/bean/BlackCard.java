package xyz.cardsagainsttelegram.bean;

import lombok.Data;
import lombok.Getter;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Data
public class BlackCard implements Card {
    @Getter
    private final CardType type = CardType.BLACK;
    @Getter
    private final String text;
    @Getter
    private final int empty;
    private transient Font font = new Font("Ubuntu", Font.PLAIN, 14);

    public File drawImage() {
        ClassLoader classLoader = CardsAgainstTelegram.class.getClassLoader();

        BufferedImage img = null;
        try {
            String filePath = classLoader.getResource("bCard.png").getFile();

            File blackCard = new File(filePath);
            img = ImageIO.read(blackCard);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Graphics2D g = img.createGraphics();

        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        drawString(g, text, 30, 50);

        File cardOutput = new File("/home/amir/Desktop/card.png");
        try {
            ImageIO.write(img, "png", cardOutput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardOutput;
    }

    private void drawString(Graphics g, String text, int x, int y) {
        int maxX = 210;

        int printingX = x;
        int printingY = y;
        FontMetrics m = g.getFontMetrics();
        for (String word : text.split(" ")) {
            int len = m.stringWidth(word);
            if (printingX + len > maxX) {
                printingX = x; // Restore back to starting
                printingY += m.getHeight();
            }
            g.drawString(word, printingX, printingY);

            printingX += len;
            printingX += m.charWidth(' ');
        }
    }
}
