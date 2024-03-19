package biblivre.administration.backup;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

class PostgresCopyDataReader extends Reader {
    private final Iterator<Character> sourceIterator;
    private boolean endOfData;

    private int lastChar;

    private int secondToLastChar;

    private int thirdToLastChar;

    public PostgresCopyDataReader(Iterator<Character> sourceIterator) {
        this.sourceIterator = sourceIterator;
        endOfData = false;
        lastChar = -1;
        secondToLastChar = -1;
        thirdToLastChar = -1;
    }

    @Override
    public int read(@Nonnull char[] cbuf, int off, int len) {
        if (hasNoNext()) {
            return -1;
        }

        for (int i = off; i < len; i++) {
            if (hasNoNext()) {
                return i - off;
            }

            char c = sourceIterator.next();

            if (secondToLastChar == '\\'
                    && lastChar == '.'
                    && c == '\n'
                    && (thirdToLastChar == '\n' || thirdToLastChar == -1)) {
                endOfData = true;
            }

            thirdToLastChar = secondToLastChar;

            secondToLastChar = lastChar;

            lastChar = c;

            cbuf[off + i] = c;
        }

        return len;
    }

    private boolean hasNoNext() {
        return endOfData || !sourceIterator.hasNext();
    }

    @Override
    public void close() throws IOException {}
}
