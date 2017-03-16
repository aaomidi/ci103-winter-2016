package xyz.cardsagainsttelegram.utils;

import lombok.RequiredArgsConstructor;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;

import java.io.File;


@RequiredArgsConstructor
public class Updater implements Runnable {
    private final CardsAgainstTelegram instance;


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000 * 5); // Check every 5 seconds
            } catch (Exception e) {
                e.printStackTrace();
            }

            File file = new File("update_available_cah");
            if (file.exists()) {
                System.out.println("File existed. Attempting to restart!");
                boolean res = file.delete();
                if (res) {
                    try {
                        instance.tellAdmins("Update was found! Restarting.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
            }

        }

    }
}