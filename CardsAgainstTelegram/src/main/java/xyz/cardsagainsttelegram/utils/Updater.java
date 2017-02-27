package xyz.cardsagainsttelegram.utils;

import lombok.RequiredArgsConstructor;
import xyz.cardsagainsttelegram.CardsAgainstTelegram;

import java.io.File;

/**
 * Created by amir on 2/26/17.
 */
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

            File file = new File("update_available");
            if (file.exists()) {
                System.out.println("File existed. Attempting to restart!");
                boolean res = file.delete();
                if (res) {
                    instance.tellAdmins("Update was found! Restarting.");
                    System.exit(0);
                }
            }

        }

    }
}