package com.indikore.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class WebServerTest {

    private static WebServer server;
    private static final int TEST_PORT = 18080;
    private static final HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    static void startServer() throws IOException {
        server = new WebServer(TEST_PORT);
        server.start();
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    @DisplayName("Test /api/health endpoint returns status UP")
    void testHealthEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/api/health"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"status\":\"UP\""));
        assertTrue(response.body().contains("\"service\":\"IndiKore Calculator & Text Toolkit\""));
    }

    @Test
    @DisplayName("Test /api/calculate addition returns correct sum")
    void testCalculateAdd() throws Exception {
        String payload = "{\"operation\":\"add\",\"a\":12,\"b\":8}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/api/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"result\":20.0"));
    }

    @Test
    @DisplayName("Test /api/calculate divide by zero returns error 400")
    void testCalculateDivideByZero() throws Exception {
        String payload = "{\"operation\":\"divide\",\"a\":10,\"b\":0}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/api/calculate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Cannot divide by zero."));
    }

    @Test
    @DisplayName("Test /api/text analyze returns palindrome detection and word count")
    void testTextAnalyze() throws Exception {
        String payload = "{\"operation\":\"analyze\",\"text\":\"madam\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/api/text"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"isPalindrome\":true"));
        assertTrue(response.body().contains("\"wordCount\":1"));
        assertTrue(response.body().contains("\"reversed\":\"madam\""));
    }

    @Test
    @DisplayName("Test static index.html is served successfully")
    void testStaticIndexHtml() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("IndiKore Suite"));
        assertTrue(response.headers().firstValue("Content-Type").orElse("").contains("text/html"));
    }
}
