package biblivre.administration.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BufferedReaderIterator implements Iterator<Character> {
    private final BufferedReader bufferedReader;
    int read;

    private static final Logger logger = LoggerFactory.getLogger(BufferedReaderIterator.class);

    public BufferedReaderIterator(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;

        read = bufferedReader.read();
    }

    @Override
    public Character next() {
        char c = (char) read;

        try {
            read = bufferedReader.read();
        } catch (IOException ioException) {
            logger.error("error while reading script", ioException);

            read = -1;
        }

        return c;
    }

    @Override
    public boolean hasNext() {
        return read != -1;
    }
}
