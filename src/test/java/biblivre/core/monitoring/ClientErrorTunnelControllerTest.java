package biblivre.core.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;

class ClientErrorTunnelControllerTest {

    static final String DSN = "https://key@errors.example.org/1";

    ClientMonitoringService service;
    ClientMonitoringProperties properties;
    ClientErrorTunnelController controller;

    @BeforeEach
    void setUp() {
        properties = new ClientMonitoringProperties();
        properties.setDsn(DSN);
        properties.setMaxEnvelopeBytes(1024);
        properties.setClientEnvelopesPerMinute(2);

        service = mock(ClientMonitoringService.class);
        when(service.isEnabled()).thenReturn(true);
        when(service.getProperties()).thenReturn(properties);
        when(service.getDsn()).thenReturn(SentryDsn.parse(DSN));

        Clock clock = Clock.fixed(Instant.parse("2026-11-25T10:00:00Z"), ZoneOffset.UTC);
        controller = new ClientErrorTunnelController(service, clock);
    }

    static MockHttpServletRequest post(String body, String ip) {
        MockHttpServletRequest request =
                new MockHttpServletRequest("POST", "/api/v2/client-errors");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));
        request.setRemoteAddr(ip);
        return request;
    }

    static String eventEnvelope(String dsn) {
        return "{\"dsn\":\"" + dsn + "\"}\n{\"type\":\"event\"}\n{\"message\":\"boom\"}\n";
    }

    @Test
    void forwardsEnvelopeForConfiguredDsn() throws Exception {
        int status =
                controller.tunnel(post(eventEnvelope(DSN), "10.0.0.1")).getStatusCode().value();

        assertEquals(200, status);
        verify(service).forward(any(byte[].class), anyString());
    }

    @Test
    void rejectsEnvelopeForAnotherDsn() throws Exception {
        int status =
                controller
                        .tunnel(post(eventEnvelope("https://key@evil.example/9"), "10.0.0.1"))
                        .getStatusCode()
                        .value();

        assertEquals(403, status);
        verify(service, never()).forward(any(), any());
    }

    @Test
    void rejectsMalformedEnvelope() throws Exception {
        assertEquals(400, controller.tunnel(post("not json", "10.0.0.1")).getStatusCode().value());
    }

    @Test
    void rejectsOversizedEnvelope() throws Exception {
        String big = eventEnvelope(DSN) + "x".repeat(2048);

        assertEquals(413, controller.tunnel(post(big, "10.0.0.1")).getStatusCode().value());
        verify(service, never()).forward(any(), any());
    }

    @Test
    void rateLimitsPerClient() throws Exception {
        controller.tunnel(post(eventEnvelope(DSN), "10.0.0.1"));
        controller.tunnel(post(eventEnvelope(DSN), "10.0.0.1"));

        assertEquals(
                429,
                controller.tunnel(post(eventEnvelope(DSN), "10.0.0.1")).getStatusCode().value());
        assertEquals(
                200,
                controller.tunnel(post(eventEnvelope(DSN), "10.0.0.2")).getStatusCode().value());
    }

    @Test
    void dropsReplayItemsBeforeForwarding() throws Exception {
        String body =
                "{\"dsn\":\""
                        + DSN
                        + "\"}\n{\"type\":\"replay_event\"}\n{}\n{\"type\":\"event\"}\n{}\n";

        controller.tunnel(post(body, "10.0.0.1"));

        ArgumentCaptor<byte[]> forwarded = ArgumentCaptor.forClass(byte[].class);
        verify(service).forward(forwarded.capture(), anyString());
        assertEquals(
                "{\"dsn\":\"" + DSN + "\"}\n{\"type\":\"event\"}\n{}\n",
                new String(forwarded.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    void doesNotForwardReplayOnlyEnvelope() throws Exception {
        String body = "{\"dsn\":\"" + DSN + "\"}\n{\"type\":\"replay_event\"}\n{}\n";

        assertEquals(200, controller.tunnel(post(body, "10.0.0.1")).getStatusCode().value());
        verify(service, never()).forward(any(), any());
    }

    @Test
    void doesNotForwardSessionOnlyEnvelope() throws Exception {
        String body = "{\"dsn\":\"" + DSN + "\"}\n{\"type\":\"session\"}\n{}\n";

        assertEquals(200, controller.tunnel(post(body, "10.0.0.1")).getStatusCode().value());
        verify(service, never()).forward(any(), any());
    }

    @Test
    void returnsNotFoundWhenDisabled() throws Exception {
        when(service.isEnabled()).thenReturn(false);

        assertEquals(
                404,
                controller.tunnel(post(eventEnvelope(DSN), "10.0.0.1")).getStatusCode().value());
    }
}
