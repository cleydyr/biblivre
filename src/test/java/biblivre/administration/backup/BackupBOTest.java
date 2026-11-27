package biblivre.administration.backup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.file.BiblivreFile;
import biblivre.core.utils.Constants;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.digitalmedia.DigitalMediaDTO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BackupBOTest {

    @TempDir Path tempDir;

    @Test
    void backup_whenCreateBackupFails_propagatesTheException() {
        BackupBO backupBO =
                new BackupBO() {
                    @Override
                    public void createBackup(BackupDTO dto) throws BackupException {
                        throw new BackupException("boom");
                    }
                };

        BackupException exception =
                assertThrows(BackupException.class, () -> backupBO.backup(backupDto()));

        assertEquals("boom", exception.getMessage());
    }

    @Test
    void createBackup_whenPgDumpIsMissing_fails() {
        BackupBO backupBO =
                new BackupBO() {
                    @Override
                    File pgDumpExecutable() {
                        return null;
                    }
                };

        BackupException exception =
                assertThrows(BackupException.class, () -> backupBO.createBackup(backupDto()));

        assertEquals("pg_dump not found", exception.getMessage());
    }

    @Test
    void dumpDatabase_whenProcessExitsWithError_fails() {
        BackupBO backupBO = new BackupBO();

        BackupException exception =
                assertThrows(
                        BackupException.class,
                        () -> backupBO.dumpDatabase(new ProcessBuilder("/usr/bin/false")));

        assertEquals("pg_dump exited with status 1", exception.getMessage());
    }

    @Test
    void dumpDatabase_whenProcessCannotStart_fails() {
        BackupBO backupBO = new BackupBO();

        BackupException exception =
                assertThrows(
                        BackupException.class,
                        () ->
                                backupBO.dumpDatabase(
                                        new ProcessBuilder(
                                                tempDir.resolve("missing-pg-dump").toString())));

        assertEquals("can't start pg_dump", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void dumpDatabase_whenProcessSucceeds_completes() throws Exception {
        new BackupBO().dumpDatabase(new ProcessBuilder("/usr/bin/true"));
    }

    @Test
    void exportDigitalMedia_whenCopyFails_stopsAndFails() throws Exception {
        DigitalMediaDTO first = media(1, "a.pdf");
        DigitalMediaDTO second = media(2, "b.pdf");
        BiblivreFile firstFile = mock(BiblivreFile.class);
        BiblivreFile secondFile = mock(BiblivreFile.class);
        doThrow(new IOException("disk full")).when(firstFile).copy(any());

        DigitalMediaBO digitalMediaBO = mock(DigitalMediaBO.class);
        when(digitalMediaBO.list()).thenReturn(List.of(first, second));
        when(digitalMediaBO.load(1, "a.pdf")).thenReturn(firstFile);
        when(digitalMediaBO.load(2, "b.pdf")).thenReturn(secondFile);

        BackupBO backupBO = new BackupBO();
        backupBO.setDigitalMediaBO(digitalMediaBO);

        BackupException exception =
                assertThrows(
                        BackupException.class,
                        () -> backupBO.exportDigitalMedia("single", tempDir.toFile()));

        assertEquals("can't export digital media 1 from schema single", exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
        verify(digitalMediaBO, never()).load(2, "b.pdf");
    }

    @Test
    void exportDigitalMedia_whenFileIsMissing_fails() {
        DigitalMediaDTO media = media(4, "gone.pdf");
        DigitalMediaBO digitalMediaBO = mock(DigitalMediaBO.class);
        when(digitalMediaBO.list()).thenReturn(List.of(media));
        when(digitalMediaBO.load(4, "gone.pdf")).thenReturn(null);

        BackupBO backupBO = new BackupBO();
        backupBO.setDigitalMediaBO(digitalMediaBO);

        BackupException exception =
                assertThrows(
                        BackupException.class,
                        () -> backupBO.exportDigitalMedia("single", tempDir.toFile()));

        assertTrue(exception.getMessage().contains("gone.pdf"));
    }

    @Test
    void move_whenRenameFails_failsBeforeSaving() {
        ConfigurationBO configurationBO = mock(ConfigurationBO.class);
        when(configurationBO.getString(Constants.CONFIG_BACKUP_PATH))
                .thenReturn(tempDir.toString());

        BackupDTO dto = backupDto();
        dto.setBackup(tempDir.resolve("missing.b5bz").toFile());

        BackupBO backupBO = new BackupBO();
        backupBO.setConfigurationBO(configurationBO);

        BackupException exception = assertThrows(BackupException.class, () -> backupBO.move(dto));

        assertTrue(exception.getMessage().startsWith("can't move backup to "));
    }

    private static BackupDTO backupDto() {
        return new BackupDTO(
                Map.of("single", Pair.of("Library", "Subtitle")),
                BackupType.FULL,
                BackupScope.SINGLE_SCHEMA);
    }

    private static DigitalMediaDTO media(int id, String name) {
        DigitalMediaDTO media = new DigitalMediaDTO();
        media.setId(id);
        media.setName(name);
        return media;
    }
}
