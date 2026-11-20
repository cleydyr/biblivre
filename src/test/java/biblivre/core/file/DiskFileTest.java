package biblivre.core.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class DiskFileTest {
    private static final byte[] CONTENT =
            "0123456789abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.US_ASCII);

    @TempDir Path tempDir;

    private DiskFile diskFile;

    @BeforeEach
    void setUp() throws IOException {
        Path path = tempDir.resolve("content.txt");
        Files.write(path, CONTENT);
        diskFile = new DiskFile(path.toFile(), "text/plain");
    }

    @Test
    void copy_middleSlice_writesOnlyRequestedBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        diskFile.copy(out, 10, 5);

        assertArrayEquals(Arrays.copyOfRange(CONTENT, 10, 15), out.toByteArray());
    }

    @Test
    void copy_fromStartWithFullSize_writesWholeFile() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        diskFile.copy(out, 0, CONTENT.length);

        assertArrayEquals(CONTENT, out.toByteArray());
    }

    @Test
    void copy_withoutRange_writesWholeFile() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        diskFile.copy(out);

        assertArrayEquals(CONTENT, out.toByteArray());
    }

    @Test
    void copy_startBeyondEndOfFile_throws() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        assertThrows(IOException.class, () -> diskFile.copy(out, CONTENT.length + 1, 1));
    }
}
