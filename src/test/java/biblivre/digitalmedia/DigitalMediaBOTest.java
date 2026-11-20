package biblivre.digitalmedia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.file.BiblivreFile;
import biblivre.digitalmedia.DirectMediaStore.PresignedUpload;
import biblivre.digitalmedia.DirectMediaStore.StoredObject;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DigitalMediaBOTest {
    private static final int USER = 7;

    private DigitalMediaDAO dao;
    private PendingUploadDAO pendingDAO;
    private DirectMediaStore store;
    private DigitalMediaBO bo;

    @BeforeEach
    void setUp() {
        SchemaThreadLocal.setSchema("single");

        dao = mock(DigitalMediaDAO.class);
        pendingDAO = mock(PendingUploadDAO.class);
        store = mock(DirectMediaStore.class);

        bo = new DigitalMediaBO();
        bo.setDigitalMediaDAO(dao);
        bo.setPendingUploadDAO(pendingDAO);
    }

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.remove();
    }

    @Test
    void resolveDownload_streamsWhenStoreCannotSign() {
        String encodedId = DigitalMediaEncodingUtil.getEncodedId(3, "book.pdf");
        BiblivreFile file = mock(BiblivreFile.class);

        when(dao.find(3, "book.pdf")).thenReturn(Optional.of(media("book.pdf", "application/pdf")));
        when(dao.load(3, "book.pdf")).thenReturn(file);

        MediaDownload download = bo.resolveDownload(encodedId, "*/*").orElseThrow();

        assertEquals(new MediaDownload.Streamed(file), download);
        assertFalse(bo.supportsDirectUpload());
    }

    @Test
    void resolveDownload_redirectsWithTheDispositionTomcatWouldSend() {
        bo.setDirectMediaStore(store);
        String encodedId = DigitalMediaEncodingUtil.getEncodedId(3, "book.pdf");
        URI signed = URI.create("https://bucket.example/single/abc?X-Amz-Signature=x");

        when(dao.find(3, "book.pdf")).thenReturn(Optional.of(media("book.pdf", "application/pdf")));
        when(store.presignDownload(any(), anyString(), anyString())).thenReturn(signed);

        MediaDownload download = bo.resolveDownload(encodedId, "text/html").orElseThrow();

        assertEquals(new MediaDownload.Redirected(signed), download);
        verify(store)
                .presignDownload(
                        StorageLocation.key("single/abc"),
                        "application/pdf",
                        "attachment;filename=\"book.pdf\"");
        verify(dao, never()).load(3, "book.pdf");
    }

    @Test
    void resolveDownload_isEmptyForUnknownMedia() {
        bo.setDirectMediaStore(store);

        when(dao.find(anyInt(), anyString())).thenReturn(Optional.empty());

        assertTrue(
                bo.resolveDownload(DigitalMediaEncodingUtil.getEncodedId(9, "x.pdf"), null)
                        .isEmpty());
    }

    @Test
    void prepareUpload_recordsPendingUploadUnderServerChosenKey() {
        bo.setDirectMediaStore(store);
        URI url = URI.create("https://bucket.example/single/key?X-Amz-Signature=x");

        when(store.newStorageKey("single")).thenReturn("single/key");
        when(store.presignUpload("single/key", "application/pdf", 10))
                .thenReturn(new PresignedUpload(url, Instant.EPOCH));

        DigitalMediaBO.PreparedUpload prepared =
                bo.prepareUpload("a:b.pdf", "application/pdf", 10, USER);

        ArgumentCaptor<PendingUpload> pending = ArgumentCaptor.forClass(PendingUpload.class);
        verify(pendingDAO).insert(pending.capture(), eq(DigitalMediaBO.PENDING_UPLOAD_TTL));

        assertEquals("single/key", pending.getValue().storageKey());
        assertEquals("a_b.pdf", pending.getValue().name());
        assertEquals(USER, pending.getValue().createdBy());
        assertEquals(pending.getValue().id(), prepared.uploadId());
        assertEquals(url, prepared.url());
    }

    @Test
    void prepareUpload_defaultsBlankTypeAndRejectsBadInput() {
        bo.setDirectMediaStore(store);

        when(store.newStorageKey("single")).thenReturn("single/key");
        when(store.presignUpload(anyString(), anyString(), anyLong()))
                .thenReturn(new PresignedUpload(URI.create("https://x"), Instant.EPOCH));

        assertEquals(
                "application/octet-stream", bo.prepareUpload("a.bin", "", 1, USER).contentType());

        assertThrows(
                DirectUploadException.class,
                () -> bo.prepareUpload("a.pdf", "not a type", 1, USER));
        assertThrows(
                DirectUploadException.class,
                () -> bo.prepareUpload("a.pdf", "application/pdf", 0, USER));
        assertThrows(
                DirectUploadException.class,
                () ->
                        bo.prepareUpload(
                                "a.pdf",
                                "application/pdf",
                                DirectMediaStore.MAX_UPLOAD_SIZE + 1,
                                USER));
        assertThrows(
                DirectUploadException.class,
                () -> bo.prepareUpload(" ", "application/pdf", 1, USER));
    }

    @Test
    void prepareUpload_isRefusedWhenStoreCannotSign() {
        assertThrows(
                DirectUploadException.class,
                () -> bo.prepareUpload("a.pdf", "application/pdf", 1, USER));
    }

    @Test
    void completeUpload_createsMediaOnlyAfterTheObjectMatches() {
        bo.setDirectMediaStore(store);
        PendingUpload upload = pending();

        when(pendingDAO.findActive(upload.id())).thenReturn(Optional.of(upload));
        when(store.head("single/key"))
                .thenReturn(Optional.of(new StoredObject(10, "application/pdf")));
        when(pendingDAO.complete(upload)).thenReturn(Optional.of(41));

        String id = bo.completeUpload(upload.id(), USER);

        assertEquals(DigitalMediaEncodingUtil.getEncodedId(41, "book.pdf"), id);
    }

    @Test
    void completeUpload_withoutObjectCreatesNothing() {
        bo.setDirectMediaStore(store);
        PendingUpload upload = pending();

        when(pendingDAO.findActive(upload.id())).thenReturn(Optional.of(upload));
        when(store.head("single/key")).thenReturn(Optional.empty());

        assertThrows(DirectUploadException.class, () -> bo.completeUpload(upload.id(), USER));

        verify(pendingDAO, never()).complete(any());
    }

    @Test
    void completeUpload_withWrongSizeDiscardsObjectAndPendingUpload() {
        bo.setDirectMediaStore(store);
        PendingUpload upload = pending();

        when(pendingDAO.findActive(upload.id())).thenReturn(Optional.of(upload));
        when(store.head("single/key"))
                .thenReturn(Optional.of(new StoredObject(11, "application/pdf")));

        assertThrows(DirectUploadException.class, () -> bo.completeUpload(upload.id(), USER));

        verify(store).deleteObject("single/key");
        verify(pendingDAO).delete(upload.id());
        verify(pendingDAO, never()).complete(any());
    }

    @Test
    void completeUpload_ofExpiredOrForeignUploadCreatesNothing() {
        bo.setDirectMediaStore(store);
        PendingUpload upload = pending();

        when(pendingDAO.findActive(upload.id())).thenReturn(Optional.empty());

        assertThrows(DirectUploadException.class, () -> bo.completeUpload(upload.id(), USER));

        when(pendingDAO.findActive(upload.id())).thenReturn(Optional.of(upload));

        assertThrows(DirectUploadException.class, () -> bo.completeUpload(upload.id(), USER + 1));

        verify(store, never()).head(anyString());
        verify(pendingDAO, never()).complete(any());
    }

    @Test
    void sweepExpiredUploads_dropsObjectThenRow() {
        bo.setDirectMediaStore(store);
        PendingUpload upload = pending();

        when(pendingDAO.listExpired()).thenReturn(List.of(upload));

        assertEquals(1, bo.sweepExpiredUploads());

        verify(store).deleteObject("single/key");
        verify(pendingDAO).delete(upload.id());
    }

    private static PendingUpload pending() {
        return new PendingUpload(
                UUID.randomUUID(), "single/key", "book.pdf", "application/pdf", 10, USER);
    }

    private static StoredMedia media(String name, String contentType) {
        return new StoredMedia(
                3, name, StorageLocation.key("single/abc"), contentType, 10, Instant.EPOCH);
    }
}
