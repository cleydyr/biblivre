package biblivre.administration.backup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.HandlerContext;
import biblivre.core.HandlerContextThreadLocal;
import biblivre.core.enums.ActionResult;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BackupHandlerTest {

    private Handler handler;
    private BackupBO backupBO;
    private HandlerContext handlerContext;

    @BeforeEach
    void setUp() {
        backupBO = mock(BackupBO.class);
        handler = new Handler();
        handler.setBackupBO(backupBO);

        handlerContext = new HandlerContext();
        HandlerContextThreadLocal.setHandlerContext(handlerContext);
    }

    @AfterEach
    void tearDown() {
        HandlerContextThreadLocal.remove();
    }

    @Test
    void backup_whenExportFails_reportsTheErrorAndKeepsTheWarning() throws Exception {
        BackupDTO dto = backupDto();
        when(backupBO.get(7)).thenReturn(dto);
        doThrow(new BackupException("pg_dump exited with status 1")).when(backupBO).backup(dto);

        ExtendedRequest request = mock(ExtendedRequest.class);
        when(request.getInteger("id")).thenReturn(7);

        handler.backup(request, mock(ExtendedResponse.class));

        assertEquals(ActionResult.ERROR, handler.getMessage().getLevel());
        assertEquals(
                "administration.maintenance.backup.error.couldnt_create_backup",
                handler.getMessage().getText());
        verify(request, never()).setScopedSessionAttribute("system_warning_backup", false);
    }

    @Test
    void backup_whenExportSucceeds_clearsTheWarning() throws Exception {
        BackupDTO dto = backupDto();
        when(backupBO.get(7)).thenReturn(dto);

        ExtendedRequest request = mock(ExtendedRequest.class);
        when(request.getInteger("id")).thenReturn(7);

        handler.backup(request, mock(ExtendedResponse.class));

        verify(request).setScopedSessionAttribute("system_warning_backup", false);
    }

    private static BackupDTO backupDto() {
        BackupDTO dto =
                new BackupDTO(
                        Map.of("single", Pair.of("Library", "Subtitle")),
                        BackupType.FULL,
                        BackupScope.SINGLE_SCHEMA);
        dto.setId(7);
        return dto;
    }
}
