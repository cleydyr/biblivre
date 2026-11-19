package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class FileIOUtilsTest {

    @Test
    void zipFolder() {
        String content =
                """
                        Uma canção tem cheiro e pode transportar
                        Uma fração de um tempo qualquer
                        Que a gente viveu num outro lugar
                        É diamante para lapidar
                        Na pedra bruta segue o veio da beleza
                        Quando faz soar cristalina revelação
                        """;
        try {
            File dest = File.createTempFile("test", "destination.zip");

            Path src = Files.createTempDirectory("test");

            Path textFile = src.resolve("test.txt");

            try (OutputStream outputStream = Files.newOutputStream(textFile)) {
                outputStream.write(content.getBytes(StandardCharsets.UTF_8));
            }

            FileIOUtils.zipFolder(src.toFile(), dest);

            File unzipped = FileIOUtils.unzip(dest);

            try (Stream<Path> list = Files.list(unzipped.toPath())) {
                assertEquals(1, list.count());
            }

            try (Stream<Path> list = Files.list(unzipped.toPath())) {
                assertTrue(
                        list.allMatch(
                                path -> {
                                    try {
                                        return content.equals(Files.readString(path));
                                    } catch (IOException e) {
                                        fail(e);
                                    }

                                    return false;
                                }));
            }
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void contentDisposition_imagesAndTextOpenInline() {
        assertEquals(
                "inline;filename=\"photo.png\"",
                FileIOUtils.contentDisposition("image/png", "photo.png", null));
        assertEquals(
                "inline;filename=\"notes.txt\"",
                FileIOUtils.contentDisposition("text/plain", "notes.txt", null));
    }

    @Test
    void contentDisposition_otherTypesFollowTheAcceptHeader() {
        assertEquals(
                "inline;filename=\"book.pdf\"",
                FileIOUtils.contentDisposition(
                        "application/pdf", "book.pdf", "text/html,application/pdf"));
        assertEquals(
                "inline;filename=\"book.pdf\"",
                FileIOUtils.contentDisposition("application/pdf", "book.pdf", "*/*"));
        assertEquals(
                "attachment;filename=\"book.pdf\"",
                FileIOUtils.contentDisposition("application/pdf", "book.pdf", "text/html"));
        assertEquals(
                "attachment;filename=\"book.pdf\"",
                FileIOUtils.contentDisposition("application/pdf", "book.pdf", null));
    }

    @Test
    void responseContentType_addsCharsetToTextOnly() {
        assertEquals("text/plain;charset=UTF-8", FileIOUtils.responseContentType("text/plain"));
        assertEquals("image/png", FileIOUtils.responseContentType("image/png"));
    }
}
