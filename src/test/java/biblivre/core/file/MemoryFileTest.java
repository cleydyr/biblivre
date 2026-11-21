package biblivre.core.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class MemoryFileTest {
    private static final byte[] CONTENT =
            "0123456789abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.US_ASCII);

    private static MemoryFile newFile() {
        return new MemoryFile(
                "content.txt", "text/plain", CONTENT.length, new ByteArrayInputStream(CONTENT));
    }

    @Test
    void copy_prefix_writesOnlyRequestedBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        newFile().copy(out, 0, 5);

        assertArrayEquals(Arrays.copyOfRange(CONTENT, 0, 5), out.toByteArray());
    }

    @Test
    void copy_withoutRange_writesWholeFile() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        newFile().copy(out);

        assertArrayEquals(CONTENT, out.toByteArray());
    }
}
