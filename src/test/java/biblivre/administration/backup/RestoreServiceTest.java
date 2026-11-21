package biblivre.administration.backup;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.administration.setup.State;
import biblivre.digitalmedia.DigitalMediaDAO;
import biblivre.digitalmedia.StorageLocation;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;

class RestoreServiceTest {

    @TempDir Path tempDir;

    @Test
    void setDigitalMediaDAO_isAutowired() throws Exception {
        Method setter = RestoreService.class.getMethod("setDigitalMediaDAO", DigitalMediaDAO.class);

        assertNotNull(
                setter.getAnnotation(Autowired.class),
                "DigitalMediaDAO must be Spring-injected for media restore during backup restore");
    }

    @Test
    void processRestore_skipsPsqlRestrictMetaCommandsAndExecutesFollowingSql() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);

        RestoreService service = new RestoreService();
        service.setDataSource(dataSource);

        Path dump = tempDir.resolve("global.schema.b5b");
        Files.writeString(
                dump,
                """
                -- PostgreSQL database dump
                --

                \\restrict Dpg1CK9sasGyNv4DJoJAVOrQ7NnPCsVnFuYfKOlq9UVNnBQ9DWnYhnnxt8dbmhg

                SET statement_timeout = 0;

                --
                -- PostgreSQL database dump complete
                --

                \\unrestrict Dpg1CK9sasGyNv4DJoJAVOrQ7NnPCsVnFuYfKOlq9UVNnBQ9DWnYhnnxt8dbmhg
                """);

        State.start();

        service.processRestore(dump.toFile());

        verify(statement).execute("SET statement_timeout = 0;");
        verify(statement, never()).execute(argThat(sql -> sql.startsWith("\\")));
    }

    @Test
    void processSchemaRestores_importsMediaFilesFromSchemaFolder() throws Exception {
        DigitalMediaDAO digitalMediaDAO = mock(DigitalMediaDAO.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(digitalMediaDAO.importFile(any(File.class), eq("single")))
                .thenReturn(StorageLocation.key("single/abc"));

        RestoreService service = new RestoreService();
        service.setDataSource(dataSource);
        service.setDigitalMediaDAO(digitalMediaDAO);

        Path schemaDir = Files.createDirectory(tempDir.resolve("single"));
        Files.writeString(schemaDir.resolve("123_photo.jpg"), "fake-media-content");

        State.start();

        assertDoesNotThrow(
                () -> service.processSchemaRestores(tempDir.toFile(), "b5b", "single", "single"));

        verify(digitalMediaDAO).importFile(any(File.class), eq("single"));
        verify(statement).execute(contains("ADD COLUMN IF NOT EXISTS storage_key"));
        verify(statement)
                .execute(
                        contains(
                                "UPDATE digital_media SET blob = NULL, storage_key = 'single/abc'"
                                        + " WHERE id = '123'"));
    }

    @Test
    void processSchemaRestores_keepsLargeObjectIdAsStorageKey() throws Exception {
        DigitalMediaDAO digitalMediaDAO = mock(DigitalMediaDAO.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(digitalMediaDAO.importFile(any(File.class), eq("single")))
                .thenReturn(StorageLocation.largeObject(42L));

        RestoreService service = new RestoreService();
        service.setDataSource(dataSource);
        service.setDigitalMediaDAO(digitalMediaDAO);

        Path schemaDir = Files.createDirectory(tempDir.resolve("single"));
        Files.writeString(schemaDir.resolve("123_photo.jpg"), "fake-media-content");

        State.start();

        service.processSchemaRestores(tempDir.toFile(), "b5b", "single", "single");

        verify(statement)
                .execute(
                        contains(
                                "UPDATE digital_media SET blob = '42', storage_key = '42'"
                                        + " WHERE id = '123'"));
    }

    @Test
    void processSchemaRestores_keysMediaUnderTheTargetLibrary() throws Exception {
        DigitalMediaDAO digitalMediaDAO = mock(DigitalMediaDAO.class);
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(digitalMediaDAO.importFile(any(File.class), eq("renamed")))
                .thenReturn(StorageLocation.key("renamed/abc"));

        RestoreService service = new RestoreService();
        service.setDataSource(dataSource);
        service.setDigitalMediaDAO(digitalMediaDAO);

        Path schemaDir = Files.createDirectory(tempDir.resolve("single"));
        Files.writeString(schemaDir.resolve("123_photo.jpg"), "fake-media-content");

        State.start();

        service.processSchemaRestores(tempDir.toFile(), "b5b", "single", "renamed");

        // Data still loads under the backup's name; only the key uses the target library.
        verify(statement).execute(contains("SET search_path = \"single\""));
        verify(digitalMediaDAO).importFile(any(File.class), eq("renamed"));
        verify(statement)
                .execute(
                        contains(
                                "UPDATE digital_media SET blob = NULL, storage_key = 'renamed/abc'"
                                        + " WHERE id = '123'"));
    }

    @Test
    void dropSchemaIfExists_executesConditionalDrop() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);

        RestoreService service = new RestoreService();
        service.setDataSource(dataSource);

        service.dropSchemaIfExists("single");

        verify(statement).execute(contains("DROP SCHEMA IF EXISTS \"single\" CASCADE"));
    }
}
