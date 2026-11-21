package biblivre.digitalmedia.postgres;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.core.file.HttpFileSender;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.largeobject.LargeObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class DatabaseFileTest {

    private static final byte[] CONTENT =
            "abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.UTF_8);

    private final AtomicLong position = new AtomicLong();

    private final AtomicLong bytesRead = new AtomicLong();

    private LargeObject largeObject;

    private Connection connection;

    private DatabaseFile file;

    @BeforeEach
    void setUp() throws Exception {
        largeObject = mock(LargeObject.class);
        connection = mock(Connection.class);

        when(largeObject.size64()).thenReturn((long) CONTENT.length);
        when(largeObject.tell64()).thenAnswer(invocation -> position.get());
        doAnswer(
                        invocation -> {
                            position.set(invocation.getArgument(0));
                            return null;
                        })
                .when(largeObject)
                .seek64(anyLong(), eq(LargeObject.SEEK_SET));
        when(largeObject.read(any(byte[].class), anyInt(), anyInt()))
                .thenAnswer(
                        invocation -> {
                            byte[] buffer = invocation.getArgument(0);
                            int offset = invocation.getArgument(1);
                            int length = invocation.getArgument(2);
                            int start = (int) position.get();
                            int count = Math.max(0, Math.min(length, CONTENT.length - start));
                            System.arraycopy(CONTENT, start, buffer, offset, count);
                            position.addAndGet(count);
                            bytesRead.addAndGet(count);
                            return count;
                        });

        file = new DatabaseFile(connection, largeObject);
        file.setName("alphabet.txt");
        file.setContentType("text/plain");
        file.setSize(CONTENT.length);
        file.setLastModified(1_700_000_000_000L);
    }

    @Test
    void range_seeksInsteadOfReadingTheLeadingBytes() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/download");
        request.addHeader("Range", "bytes=20-22");

        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpFileSender.send(file, request, response, false);

        assertEquals(206, response.getStatus());
        assertEquals("bytes 20-22/26", response.getHeader("Content-Range"));
        assertEquals("uvw", response.getContentAsString());
        assertEquals(3, bytesRead.get());
        verify(largeObject).seek64(20, LargeObject.SEEK_SET);
    }

    @Test
    void multipleRanges_shareTheLargeObject() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/download");
        request.addHeader("Range", "bytes=24-25,0-1");

        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpFileSender.send(file, request, response, false);

        assertEquals(206, response.getStatus());

        String body = response.getContentAsString();
        assertTrue(body.contains("Content-Range: bytes 24-25/26\r\n\r\nyz\r\n"), body);
        assertTrue(body.contains("Content-Range: bytes 0-1/26\r\n\r\nab\r\n"), body);
        assertEquals(4, bytesRead.get());
    }

    @Test
    void sending_closesTheLargeObjectAndTheConnection() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpFileSender.send(file, new MockHttpServletRequest("GET", "/download"), response, false);

        assertEquals(new String(CONTENT, StandardCharsets.UTF_8), response.getContentAsString());
        verify(largeObject).close();
        verify(connection).commit();
        verify(connection).close();
    }
}
