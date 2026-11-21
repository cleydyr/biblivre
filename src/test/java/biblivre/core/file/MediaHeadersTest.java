package biblivre.core.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MediaHeadersTest {

    @Test
    void contentDisposition_imagesAndTextOpenInline() {
        assertEquals(
                "inline; filename=\"photo.png\"",
                MediaHeaders.contentDisposition("image/png", "photo.png", null));
        assertEquals(
                "inline; filename=\"notes.txt\"",
                MediaHeaders.contentDisposition("text/plain", "notes.txt", null));
    }

    @Test
    void contentDisposition_otherTypesFollowTheAcceptHeader() {
        assertEquals(
                "inline; filename=\"book.pdf\"",
                MediaHeaders.contentDisposition(
                        "application/pdf", "book.pdf", "text/html,application/pdf"));
        assertEquals(
                "inline; filename=\"book.pdf\"",
                MediaHeaders.contentDisposition("application/pdf", "book.pdf", "*/*"));
        assertEquals(
                "inline; filename=\"book.pdf\"",
                MediaHeaders.contentDisposition("application/pdf", "book.pdf", "application/*"));
        assertEquals(
                "attachment; filename=\"book.pdf\"",
                MediaHeaders.contentDisposition("application/pdf", "book.pdf", "text/html"));
        assertEquals(
                "attachment; filename=\"book.pdf\"",
                MediaHeaders.contentDisposition("application/pdf", "book.pdf", null));
    }

    @Test
    void contentDisposition_xDownloadAlwaysDownloads() {
        assertEquals(
                "attachment; filename=\"export.mrc\"",
                MediaHeaders.contentDisposition("x-download", "export.mrc", "*/*"));
    }

    @Test
    void contentDisposition_escapesQuotesInTheFileName() {
        assertEquals(
                "attachment; filename=\"my \\\"best\\\" book.pdf\"",
                MediaHeaders.contentDisposition("application/pdf", "my \"best\" book.pdf", null));
    }

    @Test
    void contentDisposition_encodesNonAsciiFileNames() {
        assertEquals(
                "attachment; filename=\"Relatorio anual.pdf\";"
                        + " filename*=UTF-8''Relat%C3%B3rio%20anual.pdf",
                MediaHeaders.contentDisposition("application/pdf", "Relatório anual.pdf", null));
    }

    @Test
    void contentDisposition_withoutFileName() {
        assertEquals("inline", MediaHeaders.contentDisposition("image/png", null, null));
    }

    @Test
    void responseContentType_addsCharsetToTextOnly() {
        assertEquals("text/plain;charset=UTF-8", MediaHeaders.responseContentType("text/plain"));
        assertEquals("image/png", MediaHeaders.responseContentType("image/png"));
    }
}
