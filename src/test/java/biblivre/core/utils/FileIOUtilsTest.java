package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileIOUtilsTest {

    @TempDir Path tempDir;

    @Test
    void zipFolder_keepsNestedFolderPaths() throws IOException {
        Path src = Files.createDirectory(tempDir.resolve("src"));
        Files.writeString(src.resolve("backup.meta"), "{}");
        Files.writeString(src.resolve("single.schema.b5b"), "schema");
        Files.createDirectories(src.resolve("single"));
        Files.writeString(src.resolve("single").resolve("1"), "media");
        Files.createDirectories(src.resolve("a").resolve("b"));
        Path nested = src.resolve("a").resolve("b").resolve("c.txt");
        Files.writeString(nested, "nested");

        File dest = tempDir.resolve("nested.zip").toFile();
        FileIOUtils.zipFolder(src.toFile(), dest);

        assertEquals(
                Set.of("backup.meta", "single.schema.b5b", "single/1", "a/b/c.txt"),
                entryNames(dest));

        Path unzipped = FileIOUtils.unzip(dest).toPath();

        assertEquals("nested", Files.readString(unzipped.resolve("a/b/c.txt")));
        assertEquals("media", Files.readString(unzipped.resolve("single/1")));
        assertEquals("{}", Files.readString(unzipped.resolve("backup.meta")));
        assertTrue(
                Math.abs(
                                Files.getLastModifiedTime(nested).toMillis()
                                        - Files.getLastModifiedTime(unzipped.resolve("a/b/c.txt"))
                                                .toMillis())
                        <= 2000);
    }

    @Test
    void zipFolder_keepsEmptyFolders() throws IOException {
        Path src = Files.createDirectory(tempDir.resolve("src"));
        Files.writeString(src.resolve("file.txt"), "content");
        Files.createDirectory(src.resolve("empty"));
        Files.createDirectories(src.resolve("parent").resolve("emptyChild"));

        File dest = tempDir.resolve("empty.zip").toFile();
        FileIOUtils.zipFolder(src.toFile(), dest);

        assertEquals(Set.of("file.txt", "empty/", "parent/emptyChild/"), entryNames(dest));

        Path unzipped = FileIOUtils.unzip(dest).toPath();

        assertTrue(Files.isDirectory(unzipped.resolve("empty")));
        assertTrue(Files.isDirectory(unzipped.resolve("parent/emptyChild")));
        assertEquals("content", Files.readString(unzipped.resolve("file.txt")));
    }

    @Test
    void unzip_rejectsEntriesOutsideTheTargetFolder() throws IOException {
        String evilName = "evil-" + UUID.randomUUID() + ".txt";
        File zip = tempDir.resolve("evil.zip").toFile();

        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zip.toPath()))) {
            out.putNextEntry(new ZipEntry("good.txt"));
            out.write("good".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
            out.putNextEntry(new ZipEntry("../" + evilName));
            out.write("evil".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
        }

        // unzip extracts into a fresh folder under the system temp dir, so "../" lands there
        Path systemTemp = Path.of(System.getProperty("java.io.tmpdir"));

        assertThrows(IOException.class, () -> FileIOUtils.unzip(zip));
        assertTrue(Files.notExists(systemTemp.resolve(evilName)));
    }

    @Test
    void unzip_readsArchivesWrittenByJavaUtilZip() throws IOException {
        File zip = tempDir.resolve("legacy.b5bz").toFile();

        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(zip.toPath()))) {
            out.putNextEntry(new ZipEntry("backup.meta"));
            out.write("{}".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
            out.putNextEntry(new ZipEntry("single.media.b5b"));
            out.write("media".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
            out.putNextEntry(new ZipEntry("single/42"));
            out.write("file".getBytes(StandardCharsets.UTF_8));
            out.closeEntry();
            out.putNextEntry(new ZipEntry("empty/"));
            out.closeEntry();
        }

        Path unzipped = FileIOUtils.unzip(zip).toPath();

        assertEquals("{}", Files.readString(unzipped.resolve("backup.meta")));
        assertEquals("media", Files.readString(unzipped.resolve("single.media.b5b")));
        assertEquals("file", Files.readString(unzipped.resolve("single/42")));
        assertTrue(Files.isDirectory(unzipped.resolve("empty")));
    }

    @Test
    void writablePathHelpers() throws IOException {
        Path file = Files.writeString(tempDir.resolve("file.txt"), "x");
        String missing = tempDir.resolve("missing").toString();

        assertTrue(FileIOUtils.isWritablePath(tempDir.toString()));
        assertEquals(tempDir.toFile(), FileIOUtils.getWritablePath(tempDir.toString()));
        assertFalse(FileIOUtils.isWritablePath(file.toString()));
        assertFalse(FileIOUtils.isWritablePath(missing));
        assertFalse(FileIOUtils.isWritablePath(" "));
        assertNull(FileIOUtils.getWritablePath(null));

        assertTrue(FileIOUtils.doesNotExists(missing));
        assertFalse(FileIOUtils.doesNotExists(file.toString()));
    }

    private static Set<String> entryNames(File zip) throws IOException {
        try (ZipFile zipFile = new ZipFile(zip)) {
            return Collections.list(zipFile.entries()).stream()
                    .map(ZipEntry::getName)
                    .collect(Collectors.toSet());
        }
    }

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
