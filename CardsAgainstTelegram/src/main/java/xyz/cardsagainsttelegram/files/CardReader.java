package xyz.cardsagainsttelegram.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.cardsagainsttelegram.files.deckfile.DeckFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class CardReader {
    private final Gson gson;

    public CardReader() {
        gson = new GsonBuilder().create();

        this.readFiles();
    }

    private void readFiles() {
        try {
            InputStream is1 = this.getClass().getResourceAsStream("/official.json");
            InputStream is2 = this.getClass().getResourceAsStream("/official.json");

            readToDeckFile(is2).register(readToMap(is1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map readToMap(InputStream file) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        Map map = gson.fromJson(reader, Map.class);
        return map;
    }

    private DeckFile readToDeckFile(InputStream file) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        DeckFile dFile = gson.fromJson(reader, DeckFile.class);

        return dFile;
    }

}
