package xyz.cardsagainsttelegram.bean;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CAHInputStream extends BufferedInputStream {
    public CAHInputStream(InputStream in) {
        super(in);
    }

    public CAHInputStream(InputStream in, int size) {
        super(in, size);
    }

    @Override
    public void close() throws IOException {
    }
}
