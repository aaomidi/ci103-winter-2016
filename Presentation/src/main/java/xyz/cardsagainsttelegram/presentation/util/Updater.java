package xyz.cardsagainsttelegram.presentation.util;

import lombok.RequiredArgsConstructor;
import xyz.cardsagainsttelegram.presentation.Presentation;

import java.io.File;


@RequiredArgsConstructor
public class Updater implements Runnable {
    private final Presentation instance;


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 * 5); // Check every 5 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }

            File file = new File("update_available_pres");
            if (file.exists()) {
                System.out.println("File existed. Attempting to restart!");
                boolean res = file.delete();
                if (res) {
                    try {
                        instance.tellTelegram("Bot restarted.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    System.exit(0);
                }
            }
        }
    }
}