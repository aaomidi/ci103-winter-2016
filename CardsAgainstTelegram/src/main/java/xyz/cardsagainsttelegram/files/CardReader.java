package xyz.cardsagainsttelegram.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.cardsagainsttelegram.files.deckfile.DeckFile;

import java.io.File;
import java.io.FileReader;

public class CardReader {
    private final Gson gson;

    public CardReader() {
        gson = new GsonBuilder().create();

        this.readFiles();
    }

    private void readFiles() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();

            String fileString = classLoader.getResource("official.json").getFile();

            File file = new File(fileString);

            readDeckFile(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void readDeckFile(File file) throws Exception {
        FileReader reader = new FileReader(file);
        DeckFile dFile = gson.fromJson(reader, DeckFile.class);
    }

}
