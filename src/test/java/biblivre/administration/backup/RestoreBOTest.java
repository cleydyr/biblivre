package biblivre.administration.backup;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.administration.backup.exception.RestoreException;
import biblivre.administration.setup.State;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RestoreBOTest {

    @TempDir Path tempDir;

    @Test
    void restore_whenSchemaRestoreFailsAfterPreRename_restoresOriginalSchemaNames()
            throws Exception {
        File backup = createMinimalBackupZip();

        RestoreOperation restoreOperation = new RestoreOperation();
        restoreOperation.setValid(true);
        restoreOperation.setBackup(backup);
        restoreOperation.setSchemas(Map.of("single", Pair.of("Library", "Subtitle")));
        restoreOperation.setRestoreSchemas(Map.of("single", "single"));

        RecordingRestoreService restoreService = new RecordingRestoreService();

        RestoreBO restoreBO = new RestoreBO();
        restoreBO.setBackupBO(new StubBackupBO());
        restoreBO.setRestoreService(restoreService);

        State.start();

        assertThrows(Exception.class, () -> restoreBO.restore(restoreOperation, null));

        assertTrue(
                restoreService.droppedSchemas.contains("single"),
                "Failed restore must drop the partial schema before renaming the original back");
        assertTrue(
                restoreService.schemaRenames.stream()
                        .anyMatch(RestoreBOTest::renamesTemporarySchemasBackToOriginal),
                "Failed restore left PostgreSQL schemas under temporary names (_schema_timestamp)");
    }

    private static boolean renamesTemporarySchemasBackToOriginal(Map<String, String> rename) {
        return rename.entrySet().stream()
                .anyMatch(
                        entry ->
                                entry.getKey().matches("_single_\\d+")
                                        && "single".equals(entry.getValue()));
    }

    private File createMinimalBackupZip() throws Exception {
        File backup = tempDir.resolve("restore-failure.b5bz").toFile();

        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(backup))) {
            zip.putNextEntry(new ZipEntry("single.schema.b5b"));
            zip.write("-- schema\n".getBytes());
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("single.data.b5b"));
            zip.write("-- data\n".getBytes());
            zip.closeEntry();

            zip.putNextEntry(new ZipEntry("single.media.b5b"));
            zip.write("-- media\n".getBytes());
            zip.closeEntry();
        }

        return backup;
    }

    private static final class StubBackupBO extends BackupBO {
        @Override
        public Set<String> listDatabaseSchemas() {
            return Set.of("global", "single");
        }
    }

    private static final class RecordingRestoreService extends RestoreService {
        private final List<Map<String, String>> schemaRenames = new ArrayList<>();
        private final List<String> droppedSchemas = new ArrayList<>();

        @Override
        public void processSchemaRenames(Map<String, String> preRenameSchemas) {
            schemaRenames.add(new HashMap<>(preRenameSchemas));
        }

        @Override
        public void dropSchemaIfExists(String schemaToBeDeleted) {
            droppedSchemas.add(schemaToBeDeleted);
        }

        @Override
        public void processSchemaRestores(File path, String extension, String schema)
                throws RestoreException {
            throw new RestoreException("simulated restore failure");
        }
    }
}
