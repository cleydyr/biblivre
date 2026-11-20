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

import jakarta.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class FileIOUtils {

    public static File createTempDir() throws IOException {
        final File sysTempDir = FileUtils.getTempDirectory();
        final int maxAttempts = 9;
        int attemptCount = 0;

        File newTempDir;

        do {
            attemptCount++;
            if (attemptCount > maxAttempts) {
                throw new IOException(
                        "The highly improbable has occurred! Failed to create a unique temporary directory after "
                                + maxAttempts
                                + " attempts.");
            }
            String dirName = UUID.randomUUID().toString();
            newTempDir = new File(sysTempDir, dirName);
        } while (newTempDir.exists());

        if (newTempDir.mkdirs()) {
            return newTempDir;
        } else {
            throw new IOException(
                    "Failed to create temp dir named " + newTempDir.getAbsolutePath());
        }
    }

    public static boolean doesNotExists(String path) {
        try {
            File file = new File(path);
            return !file.exists();
        } catch (Exception e) {
            return true;
        }
    }

    public static void zipFolder(File src, File dest) throws IOException {
        try (OutputStream fileWriter = Files.newOutputStream(dest.toPath());
                ZipOutputStream zip = new ZipOutputStream(fileWriter)) {
            FileIOUtils.addFolderToZip("", src, zip);
        }
    }

    private static void addFileToZip(String path, File src, ZipOutputStream zip, boolean flag)
            throws IOException {
        if (flag) {
            zip.putNextEntry(new ZipEntry(src.getName() + "/"));
        } else {
            if (src.isDirectory()) {
                FileIOUtils.addFolderToZip(src.getName(), src, zip);
            } else {
                byte[] buf = new byte[1024];
                int len;

                try (InputStream in = Files.newInputStream(src.toPath())) {
                    if (path.isEmpty()) {
                        zip.putNextEntry(new ZipEntry(src.getName()));
                    } else {
                        zip.putNextEntry(new ZipEntry(path + "/" + src.getName()));
                    }

                    while ((len = in.read(buf)) > 0) {
                        zip.write(buf, 0, len);
                    }
                }
            }
        }
    }

    private static void addFolderToZip(
            @Nonnull String path, @Nonnull File src, @Nonnull ZipOutputStream zip)
            throws IOException {
        // check the empty folder

        String[] list = src.list();

        if (!src.isDirectory() || list == null) {
            throw new IllegalArgumentException("The file %s is not a folder.".formatted(src));
        }

        if (list.length == 0) {
            FileIOUtils.addFileToZip(path, src, zip, true);
        } else {
            // list the files in the folder
            for (String fileName : list) {
                FileIOUtils.addFileToZip(path, new File(src, fileName), zip, false);
            }
        }
    }

    public static File unzip(File zip) throws IOException {
        File tmpDir = FileIOUtils.createTempDir();
        try (ZipFile zipFile = new ZipFile(zip)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                String entryName = entry.getName();

                File destination = new File(tmpDir, entryName);

                if (entry.isDirectory()) {
                    FileUtils.forceMkdir(destination);
                } else {

                    try (InputStream is = zipFile.getInputStream(entry);
                            FileOutputStream os = FileUtils.openOutputStream(destination)) {
                        IOUtils.copy(is, os);
                    }

                    boolean isLastModifiedChanged = destination.setLastModified(entry.getTime());

                    if (!isLastModifiedChanged) {
                        log.warn(
                                "Setting last modified date failed for entry %s"
                                        .formatted(entryName));
                    }
                }
            }
        }

        return tmpDir;
    }

    public static File getWritablePath(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }

        File file = new File(path);

        if (!file.isDirectory()) {
            return null;
        }

        if (!file.canWrite()) {
            return null;
        }

        return file;
    }

    public static boolean isWritablePath(String path) {
        return FileIOUtils.getWritablePath(path) != null;
    }
}
