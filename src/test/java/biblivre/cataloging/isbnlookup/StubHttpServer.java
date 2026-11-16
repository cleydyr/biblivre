package biblivre.cataloging.isbnlookup;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Local HTTP server answering every request with a configurable JSON response. */
final class StubHttpServer implements AutoCloseable {
    private final HttpServer server;
    private final AtomicBoolean closed = new AtomicBoolean();
    private final AtomicInteger statusCode = new AtomicInteger(200);
    private final AtomicReference<String> responseBody = new AtomicReference<>("{}");
    private final AtomicReference<URI> lastRequestUri = new AtomicReference<>();

    StubHttpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(
                "/",
                exchange -> {
                    lastRequestUri.set(exchange.getRequestURI());
                    byte[] body = responseBody.get().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().add("Content-Type", "application/json");
                    exchange.sendResponseHeaders(statusCode.get(), body.length);
                    try (OutputStream outputStream = exchange.getResponseBody()) {
                        outputStream.write(body);
                    }
                });
        server.start();
    }

    String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    void respond(int status, String body) {
        statusCode.set(status);
        responseBody.set(body);
    }

    URI lastRequestUri() {
        return lastRequestUri.get();
    }

    IsbnLookupProperties properties() {
        IsbnLookupProperties properties = new IsbnLookupProperties();
        properties.setBaseUrl(baseUrl());
        return properties;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            server.stop(0);
        }
    }
}
