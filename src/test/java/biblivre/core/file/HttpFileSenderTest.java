package biblivre.core.file;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class HttpFileSenderTest {

    private static final String CONTENT = "0123456789";

    private static final long LAST_MODIFIED = 1_700_000_000_000L;

    @TempDir Path tempDir;

    private File textFile;

    @BeforeEach
    void setUp() throws IOException {
        textFile = write("notes.txt", CONTENT.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void get_sendsTheWholeFileWithCachingHeaders() throws IOException {
        MockHttpServletResponse response = send(get(), textFile, "text/plain");

        assertEquals(200, response.getStatus());
        assertEquals(CONTENT, response.getContentAsString());
        assertEquals(10, response.getContentLength());
        assertEquals("text/plain;charset=UTF-8", response.getContentType());
        assertEquals("bytes", response.getHeader("Accept-Ranges"));
        assertEquals("inline; filename=\"notes.txt\"", response.getHeader("Content-Disposition"));
        assertEquals(LAST_MODIFIED, response.getDateHeader("Last-Modified"));
        assertTrue(response.getDateHeader("Expires") > System.currentTimeMillis());
        assertNull(response.getHeader("Content-Range"));

        String etag = response.getHeader("ETag");
        assertNotNull(etag);
        assertTrue(etag.matches("\"[0-9a-f]{32}\""), etag);
    }

    @Test
    void get_dropsTheCharsetAFilterSetForABinaryFile() throws IOException {
        File image = write("photo.png", new byte[] {1, 2, 3});
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setCharacterEncoding("UTF-8");

        HttpFileSender.send(new DiskFile(image, "image/png"), get(), response, false);

        assertEquals("image/png", response.getContentType());
        assertArrayEquals(new byte[] {1, 2, 3}, response.getContentAsByteArray());
    }

    @Test
    void head_sendsHeadersWithoutBody() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("HEAD", "/download");
        request.addHeader("Range", "bytes=0-1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpFileSender.send(new DiskFile(textFile, "text/plain"), request, response, true);

        assertEquals(200, response.getStatus());
        assertEquals(0, response.getContentAsByteArray().length);
        assertEquals(10, response.getContentLength());
        assertEquals("text/plain;charset=UTF-8", response.getContentType());
        assertNotNull(response.getHeader("ETag"));
        assertNull(response.getHeader("Content-Range"));
    }

    @Test
    void missingFile_isNotFound() throws IOException {
        MockHttpServletResponse response =
                send(get(), tempDir.resolve("missing.txt").toFile(), "text/plain");

        assertEquals(404, response.getStatus());
    }

    @Test
    void nullFile_sendsNothing() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        HttpFileSender.send(null, get(), response, false);

        assertEquals(200, response.getStatus());
        assertEquals(0, response.getContentAsByteArray().length);
    }

    @Test
    void singleRange_isPartialContent() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=2-5");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(206, response.getStatus());
        assertEquals("bytes 2-5/10", response.getHeader("Content-Range"));
        assertEquals(4, response.getContentLength());
        assertEquals("2345", response.getContentAsString());
    }

    @Test
    void openEndedRange_runsToTheEnd() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=7-");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(206, response.getStatus());
        assertEquals("bytes 7-9/10", response.getHeader("Content-Range"));
        assertEquals("789", response.getContentAsString());
    }

    @Test
    void suffixRangeLargerThanTheFile_sendsTheWholeFileFromZero() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=-500");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(206, response.getStatus());
        assertEquals("bytes 0-9/10", response.getHeader("Content-Range"));
        assertEquals(CONTENT, response.getContentAsString());
    }

    @Test
    void multipleRanges_areAWellFormedMultipartBody() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=0-1,4-5");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(206, response.getStatus());

        String contentType = response.getContentType();
        assertTrue(contentType.startsWith("multipart/byteranges; boundary="), contentType);

        String boundary = contentType.replaceFirst("^.*boundary=([^;]+).*$", "$1");
        String expected =
                "\r\n--"
                        + boundary
                        + "\r\nContent-Type: text/plain;charset=UTF-8"
                        + "\r\nContent-Range: bytes 0-1/10"
                        + "\r\n\r\n01"
                        + "\r\n--"
                        + boundary
                        + "\r\nContent-Type: text/plain;charset=UTF-8"
                        + "\r\nContent-Range: bytes 4-5/10"
                        + "\r\n\r\n45"
                        + "\r\n--"
                        + boundary
                        + "--";

        assertEquals(expected, response.getContentAsString());
    }

    @Test
    void multipleRanges_outOfOrderReopenTheFile() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=8-9,0-0");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(206, response.getStatus());

        String body = response.getContentAsString();
        assertTrue(body.contains("Content-Range: bytes 8-9/10\r\n\r\n89\r\n"), body);
        assertTrue(body.contains("Content-Range: bytes 0-0/10\r\n\r\n0\r\n"), body);
    }

    @Test
    void unsatisfiableRange_is416() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=20-30");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(416, response.getStatus());
        assertEquals("bytes */10", response.getHeader("Content-Range"));
    }

    @Test
    void malformedRange_is416() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=5-2");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(416, response.getStatus());
        assertEquals("bytes */10", response.getHeader("Content-Range"));
    }

    @Test
    void tooManyRanges_is416() throws IOException {
        StringBuilder ranges = new StringBuilder("bytes=0-0");
        for (int i = 0; i < 100; i++) {
            ranges.append(",0-0");
        }

        MockHttpServletRequest request = get();
        request.addHeader("Range", ranges.toString());

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(416, response.getStatus());
    }

    @Test
    void ifNoneMatch_withTheCurrentETag_isNotModified() throws IOException {
        String etag = currentETag();

        MockHttpServletRequest request = get();
        request.addHeader("If-None-Match", etag);

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(304, response.getStatus());
        assertEquals(etag, response.getHeader("ETag"));
        assertEquals(0, response.getContentAsByteArray().length);
    }

    @Test
    void ifNoneMatch_withAnotherETag_sendsTheFile() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("If-None-Match", "\"other\"");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(200, response.getStatus());
        assertEquals(CONTENT, response.getContentAsString());
    }

    @Test
    void ifModifiedSince_notModified() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("If-Modified-Since", new Date(LAST_MODIFIED));

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(304, response.getStatus());
        assertEquals(0, response.getContentAsByteArray().length);
    }

    @Test
    void ifModifiedSince_modified() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("If-Modified-Since", new Date(LAST_MODIFIED - 60_000));

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(200, response.getStatus());
        assertEquals(CONTENT, response.getContentAsString());
    }

    @Test
    void ifMatch_mismatch_isPreconditionFailed() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("If-Match", "\"other\"");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(412, response.getStatus());
        assertEquals(0, response.getContentAsByteArray().length);
    }

    @Test
    void ifMatch_currentETagOrWildcard_sendsTheFile() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("If-Match", "\"other\", " + currentETag());

        assertEquals(200, send(request, textFile, "text/plain").getStatus());

        MockHttpServletRequest wildcard = get();
        wildcard.addHeader("If-Match", "*");

        assertEquals(200, send(wildcard, textFile, "text/plain").getStatus());
    }

    @Test
    void ifUnmodifiedSince_beforeTheLastChange_isPreconditionFailed() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("If-Unmodified-Since", new Date(LAST_MODIFIED - 60_000));

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(412, response.getStatus());
    }

    @Test
    void ifRange_currentETag_sendsTheRange() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=0-1");
        request.addHeader("If-Range", currentETag());

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(206, response.getStatus());
        assertEquals("01", response.getContentAsString());
    }

    @Test
    void ifRange_staleETag_sendsTheWholeFile() throws IOException {
        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=0-1");
        request.addHeader("If-Range", "\"stale\"");

        MockHttpServletResponse response = send(request, textFile, "text/plain");

        assertEquals(200, response.getStatus());
        assertEquals(CONTENT, response.getContentAsString());
    }

    @Test
    void ifRange_dates() throws IOException {
        MockHttpServletRequest current = get();
        current.addHeader("Range", "bytes=0-1");
        current.addHeader("If-Range", new Date(LAST_MODIFIED));

        assertEquals(206, send(current, textFile, "text/plain").getStatus());

        MockHttpServletRequest stale = get();
        stale.addHeader("Range", "bytes=0-1");
        stale.addHeader("If-Range", new Date(LAST_MODIFIED - 60_000));

        assertEquals(200, send(stale, textFile, "text/plain").getStatus());
    }

    @Test
    void disposition_otherTypesDownloadUnlessAccepted() throws IOException {
        File pdf = write("book.pdf", new byte[] {'%', 'P', 'D', 'F'});

        MockHttpServletRequest html = get();
        html.addHeader("Accept", "text/html");

        assertEquals(
                "attachment; filename=\"book.pdf\"",
                send(html, pdf, "application/pdf").getHeader("Content-Disposition"));

        MockHttpServletRequest any = get();
        any.addHeader("Accept", "*/*");

        assertEquals(
                "inline; filename=\"book.pdf\"",
                send(any, pdf, "application/pdf").getHeader("Content-Disposition"));
    }

    @Test
    void disposition_encodesNonAsciiFileNames() throws IOException {
        File report = write("Relatório.pdf", new byte[] {1});

        MockHttpServletResponse response = send(get(), report, "application/pdf");

        assertEquals(
                "attachment; filename=\"Relatorio.pdf\"; filename*=UTF-8''Relat%C3%B3rio.pdf",
                response.getHeader("Content-Disposition"));
    }

    @Test
    void xDownload_isSentAsOctetStreamAttachment() throws IOException {
        File marc = write("export.mrc", new byte[] {1, 2});

        MockHttpServletRequest request = get();
        request.addHeader("Accept", "*/*");

        MockHttpServletResponse response = send(request, marc, "x-download");

        assertEquals("application/octet-stream", response.getContentType());
        assertEquals(
                "attachment; filename=\"export.mrc\"", response.getHeader("Content-Disposition"));
    }

    @Test
    void streamOnlyFile_ignoresRangesAndIsClosed() throws IOException {
        TrackingInputStream input = new TrackingInputStream(CONTENT.getBytes());
        MemoryFile file = new MemoryFile("notes.txt", "text/plain", CONTENT.length(), input);

        MockHttpServletRequest request = get();
        request.addHeader("Range", "bytes=2-5");

        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpFileSender.send(file, request, response, false);

        assertEquals(200, response.getStatus());
        assertEquals("none", response.getHeader("Accept-Ranges"));
        assertEquals(CONTENT, response.getContentAsString());
        assertTrue(input.closed);
    }

    private String currentETag() throws IOException {
        return send(get(), textFile, "text/plain").getHeader("ETag");
    }

    private File write(String name, byte[] content) throws IOException {
        Path path = tempDir.resolve(name);
        Files.write(path, content);
        Files.setLastModifiedTime(path, FileTime.fromMillis(LAST_MODIFIED));
        return path.toFile();
    }

    private static MockHttpServletRequest get() {
        return new MockHttpServletRequest("GET", "/download");
    }

    private static MockHttpServletResponse send(
            MockHttpServletRequest request, File file, String contentType) throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpFileSender.send(new DiskFile(file, contentType), request, response, false);
        return response;
    }

    private static final class TrackingInputStream extends ByteArrayInputStream {
        boolean closed;

        TrackingInputStream(byte[] content) {
            super(content);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
