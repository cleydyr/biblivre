package biblivre.administration.backup;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BufferedReaderIteratorTest {

    private BufferedReaderIterator iterator;

    @BeforeEach
    public void setUp() throws Exception {
        BufferedReader reader = new BufferedReader(new StringReader("test"));
        iterator = new BufferedReaderIterator(reader);
    }

    @Test
    public void testNext() {
        assertEquals('t', iterator.next());
        assertEquals('e', iterator.next());
        assertEquals('s', iterator.next());
        assertEquals('t', iterator.next());
    }

    @Test
    public void testHasNext() {
        assertTrue(iterator.hasNext());
        iterator.next(); // 't'
        assertTrue(iterator.hasNext());
        iterator.next(); // 'e'
        assertTrue(iterator.hasNext());
        iterator.next(); // 's'
        assertTrue(iterator.hasNext());
        iterator.next(); // 't'
        assertFalse(iterator.hasNext());
    }
}
