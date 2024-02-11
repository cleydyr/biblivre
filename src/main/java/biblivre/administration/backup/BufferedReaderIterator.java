package biblivre.administration.backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;

final class BufferedReaderIterator implements Iterator<Character> {
    private final BufferedReader bufferedReader;
    int read;

    BufferedReaderIterator(BufferedReader bufferedReader) throws IOException {
        this.bufferedReader = bufferedReader;

        read = bufferedReader.read();
    }

    @Override
    public Character next() {
        char c = (char) read;

        try {
            read = bufferedReader.read();
        } catch (IOException ioException) {
            RestoreBO.logger.error("error while reading script", ioException);

            read = -1;
        }

        return c;
    }

    @Override
    public boolean hasNext() {
        return read != -1;
    }
}
