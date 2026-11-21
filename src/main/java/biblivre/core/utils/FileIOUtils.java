/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.core.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class FileIOUtils {

    public static File createTempDir() throws IOException {
        return Files.createTempDirectory("biblivre_").toFile();
    }

    public static boolean doesNotExists(String path) {
        if (path == null) {
            return true;
        }

        try {
            return Files.notExists(Path.of(path));
        } catch (InvalidPathException e) {
            return true;
        }
    }

    /**
     * Zips the contents of a folder. Entry names are relative to {@code src} (the folder itself is
     * not part of them), nested folders keep their full relative path, and empty folders are stored
     * as {@code name/} entries.
     *
     * @param src the folder to zip.
     * @param dest the zip file to create.
     * @throws IOException if the folder can't be read or the zip file can't be written.
     */
    public static void zipFolder(File src, File dest) throws IOException {
        Path root = src.toPath();

        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("The file %s is not a folder.".formatted(src));
        }

        try (Stream<Path> paths = Files.walk(root);
                ZipArchiveOutputStream zip = new ZipArchiveOutputStream(dest.toPath())) {
            for (Path path : (Iterable<Path>) paths.sorted()::iterator) {
                if (path.equals(root)) {
                    continue;
                }

                String name = root.relativize(path).toString().replace(File.separatorChar, '/');

                if (Files.isDirectory(path)) {
                    if (isEmptyDirectory(path)) {
                        zip.putArchiveEntry(zip.createArchiveEntry(path, name + "/"));
                        zip.closeArchiveEntry();
                    }
                } else {
                    zip.putArchiveEntry(zip.createArchiveEntry(path, name));
                    Files.copy(path, zip);
                    zip.closeArchiveEntry();
                }
            }
        }
    }

    private static boolean isEmptyDirectory(Path directory) throws IOException {
        try (Stream<Path> children = Files.list(directory)) {
            return children.findAny().isEmpty();
        }
    }

    /**
     * Extracts a zip file into a new temporary folder. Each extracted file gets the last modified
     * time stored in its entry. Entries that would resolve outside the temporary folder (Zip Slip)
     * are rejected.
     *
     * @param zip the zip file to extract.
     * @return the temporary folder holding the extracted contents.
     * @throws IOException if the zip file can't be read, holds an entry that resolves outside the
     *     target folder, or the contents can't be written.
     */
    public static File unzip(File zip) throws IOException {
        File tmpDir = FileIOUtils.createTempDir();
        Path target = tmpDir.toPath().toAbsolutePath().normalize();

        try (ZipFile zipFile = ZipFile.builder().setFile(zip).get()) {
            for (ZipArchiveEntry entry : Collections.list(zipFile.getEntries())) {
                // resolveIn normalizes the entry path and throws if it leaves the target folder
                Path destination = entry.resolveIn(target);

                if (entry.isDirectory()) {
                    Files.createDirectories(destination);
                    continue;
                }

                Files.createDirectories(destination.getParent());

                try (InputStream is = zipFile.getInputStream(entry)) {
                    Files.copy(is, destination, StandardCopyOption.REPLACE_EXISTING);
                }

                setLastModifiedTime(destination, entry);
            }
        } catch (IOException | RuntimeException e) {
            FileUtils.deleteQuietly(tmpDir);

            throw e;
        }

        return tmpDir;
    }

    private static void setLastModifiedTime(Path destination, ZipArchiveEntry entry) {
        long time = entry.getTime();

        if (time == -1) {
            return;
        }

        try {
            Files.setLastModifiedTime(destination, FileTime.fromMillis(time));
        } catch (IOException e) {
            log.warn("Setting last modified date failed for entry {}", entry.getName(), e);
        }
    }

    public static File getWritablePath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }

        Path directory;

        try {
            directory = Path.of(path);
        } catch (InvalidPathException e) {
            return null;
        }

        if (!Files.isDirectory(directory) || !Files.isWritable(directory)) {
            return null;
        }

        return directory.toFile();
    }

    public static boolean isWritablePath(String path) {
        return FileIOUtils.getWritablePath(path) != null;
    }
}
