package biblivre.circulation.addresslookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.circulation.addresslookup.AddressLookupException.Reason;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class AddressLookupServiceTest {

    private HttpServer server;
    private final AtomicInteger statusCode = new AtomicInteger(200);
    private final AtomicReference<String> responseBody = new AtomicReference<>("{}");

    @BeforeEach
    void startStubServer() throws IOException {
        statusCode.set(200);
        responseBody.set("{}");

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/",
                exchange -> {
                    byte[] body = responseBody.get().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(statusCode.get(), body.length);
                    try (OutputStream outputStream = exchange.getResponseBody()) {
                        outputStream.write(body);
                    }
                });
        server.start();
    }

    @AfterEach
    void stopStubServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void normalizeCep_stripsNonDigits() {
        assertEquals("01310100", AddressLookupService.normalizeCep("01310-100"));
        assertEquals("01310100", AddressLookupService.normalizeCep(" 01310 100 "));
        assertEquals("", AddressLookupService.normalizeCep(null));
    }

    @Test
    void isValidBrazilianCep_requiresEightDigits() {
        assertTrue(AddressLookupService.isValidBrazilianCep("01310100"));
        assertFalse(AddressLookupService.isValidBrazilianCep("1310100"));
        assertFalse(AddressLookupService.isValidBrazilianCep("0131010a"));
        assertFalse(AddressLookupService.isValidBrazilianCep(""));
    }

    @Test
    void lookup_whenDisabled_throwsDisabled() {
        AddressLookupProperties properties = new AddressLookupProperties();
        properties.setEnabled(false);

        AddressLookupService service = new AddressLookupService(properties, RestClient.builder());

        AddressLookupException exception =
                assertThrows(AddressLookupException.class, () -> service.lookup("01310100"));
        assertEquals(Reason.DISABLED, exception.getReason());
    }

    @Test
    void lookup_whenInvalidCep_throwsInvalid() {
        AddressLookupProperties properties = enabledProperties();

        AddressLookupService service = new AddressLookupService(properties, RestClient.builder());

        AddressLookupException exception =
                assertThrows(AddressLookupException.class, () -> service.lookup("123"));
        assertEquals(Reason.INVALID_CEP, exception.getReason());
    }

    @Test
    void lookup_mapsSuccessfulViaCepResponse() {
        responseBody.set(
                """
                {
                  "logradouro": "Avenida Paulista",
                  "bairro": "Bela Vista",
                  "localidade": "São Paulo",
                  "uf": "SP"
                }
                """);

        AddressLookupResult result = createService().lookup("01310-100");

        assertEquals("Avenida Paulista", result.street());
        assertEquals("Bela Vista", result.neighborhood());
        assertEquals("São Paulo", result.city());
        assertEquals("SP", result.state());
        assertFalse(result.incomplete());
    }

    @Test
    void lookup_whenErroTrue_throwsNotFound() {
        responseBody.set(
                """
                {"erro": true}
                """);

        AddressLookupException exception =
                assertThrows(
                        AddressLookupException.class, () -> createService().lookup("00000000"));
        assertEquals(Reason.NOT_FOUND, exception.getReason());
    }

    @Test
    void lookup_whenStreetEmpty_marksIncomplete() {
        responseBody.set(
                """
                {
                  "logradouro": "",
                  "bairro": "Centro",
                  "localidade": "Campinas",
                  "uf": "SP"
                }
                """);

        AddressLookupResult result = createService().lookup("13010000");

        assertEquals("", result.street());
        assertEquals("Centro", result.neighborhood());
        assertEquals("Campinas", result.city());
        assertEquals("SP", result.state());
        assertTrue(result.incomplete());
    }

    @Test
    void lookup_whenAllAddressFieldsBlank_throwsNotFound() {
        responseBody.set(
                """
                {
                  "logradouro": "  ",
                  "bairro": "",
                  "localidade": null,
                  "uf": ""
                }
                """);

        AddressLookupException exception =
                assertThrows(
                        AddressLookupException.class, () -> createService().lookup("99999999"));
        assertEquals(Reason.NOT_FOUND, exception.getReason());
    }

    @Test
    void lookup_whenMalformedJson_throwsUpstreamError() {
        responseBody.set("{not-json");

        AddressLookupException exception =
                assertThrows(
                        AddressLookupException.class, () -> createService().lookup("01310100"));
        assertEquals(Reason.UPSTREAM_ERROR, exception.getReason());
    }

    @Test
    void lookup_whenEmptyResponseBody_throwsUpstreamError() {
        responseBody.set("");

        AddressLookupException exception =
                assertThrows(
                        AddressLookupException.class, () -> createService().lookup("01310100"));
        assertEquals(Reason.UPSTREAM_ERROR, exception.getReason());
    }

    @Test
    void lookup_whenUpstreamHttpError_throwsUpstreamError() {
        statusCode.set(500);
        responseBody.set("{\"message\":\"boom\"}");

        AddressLookupException exception =
                assertThrows(
                        AddressLookupException.class, () -> createService().lookup("01310100"));
        assertEquals(Reason.UPSTREAM_ERROR, exception.getReason());
    }

    private AddressLookupService createService() {
        return new AddressLookupService(enabledProperties(), RestClient.builder());
    }

    private AddressLookupProperties enabledProperties() {
        AddressLookupProperties properties = new AddressLookupProperties();
        properties.setEnabled(true);
        properties.setBaseUrl("http://127.0.0.1:" + server.getAddress().getPort());
        return properties;
    }
}
