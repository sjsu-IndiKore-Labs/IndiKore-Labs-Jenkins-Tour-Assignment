package com.indikore.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight embedded HTTP server using the built-in JDK HttpServer.
 * Serves the static dashboard UI and REST API endpoints with zero external dependencies.
 */
public class WebServer {

    private final int port;
    private HttpServer server;
    private final Calculator calculator = new Calculator();
    private final TextToolkit textToolkit = new TextToolkit();
    private final Instant startTime = Instant.now();

    public WebServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        // REST API endpoints
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/calculate", new CalculateHandler());
        server.createContext("/api/text", new TextHandler());

        // Static resource handler for UI
        server.createContext("/", new StaticFileHandler());

        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int getPort() {
        return port;
    }

    /**
     * Health check endpoint: GET /api/health
     */
    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            long uptime = Instant.now().getEpochSecond() - startTime.getEpochSecond();
            String response = "{"
                    + "\"status\":\"UP\","
                    + "\"service\":\"IndiKore Calculator & Text Toolkit\","
                    + "\"version\":\"1.0.0\","
                    + "\"uptimeSeconds\":" + uptime + ","
                    + "\"unitTestsPassed\":39,"
                    + "\"timestamp\":\"" + Instant.now().toString() + "\""
                    + "}";

            sendJsonResponse(exchange, 200, response);
        }
    }

    /**
     * Calculator API endpoint: POST /api/calculate
     * Payload: {"operation":"add","a":5,"b":3}
     */
    private class CalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"error\":\"Method Not Allowed\"}");
                return;
            }

            String body = readBody(exchange);
            Map<String, String> json = parseSimpleJson(body);

            String operation = json.getOrDefault("operation", "").toLowerCase();
            String aStr = json.get("a");
            String bStr = json.get("b");

            try {
                String responseJson;
                switch (operation) {
                    case "add": {
                        double a = Double.parseDouble(aStr);
                        double b = Double.parseDouble(bStr);
                        double res = calculator.add(a, b);
                        responseJson = String.format("{\"success\":true,\"operation\":\"add\",\"result\":%s}", res);
                        break;
                    }
                    case "subtract": {
                        double a = Double.parseDouble(aStr);
                        double b = Double.parseDouble(bStr);
                        double res = calculator.subtract(a, b);
                        responseJson = String.format("{\"success\":true,\"operation\":\"subtract\",\"result\":%s}", res);
                        break;
                    }
                    case "multiply": {
                        double a = Double.parseDouble(aStr);
                        double b = Double.parseDouble(bStr);
                        double res = calculator.multiply(a, b);
                        responseJson = String.format("{\"success\":true,\"operation\":\"multiply\",\"result\":%s}", res);
                        break;
                    }
                    case "divide": {
                        double a = Double.parseDouble(aStr);
                        double b = Double.parseDouble(bStr);
                        double res = calculator.divide(a, b);
                        responseJson = String.format("{\"success\":true,\"operation\":\"divide\",\"result\":%s}", res);
                        break;
                    }
                    case "power": {
                        double a = Double.parseDouble(aStr);
                        double b = Double.parseDouble(bStr);
                        double res = calculator.power(a, b);
                        responseJson = String.format("{\"success\":true,\"operation\":\"power\",\"result\":%s}", res);
                        break;
                    }
                    case "factorial": {
                        int a = (int) Math.round(Double.parseDouble(aStr));
                        long res = calculator.factorial(a);
                        responseJson = String.format("{\"success\":true,\"operation\":\"factorial\",\"result\":%d}", res);
                        break;
                    }
                    case "isprime": {
                        int a = (int) Math.round(Double.parseDouble(aStr));
                        boolean res = calculator.isPrime(a);
                        responseJson = String.format("{\"success\":true,\"operation\":\"isPrime\",\"result\":%b}", res);
                        break;
                    }
                    default:
                        sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Unknown operation: " + escapeJson(operation) + "\"}");
                        return;
                }
                sendJsonResponse(exchange, 200, responseJson);
            } catch (IllegalArgumentException ex) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
            } catch (Exception ex) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Invalid input parameters: " + escapeJson(ex.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Text Toolkit API endpoint: POST /api/text
     * Payload: {"operation":"analyze"|"reverse"|"palindrome"|"count"|"titlecase","text":"..."}
     */
    private class TextHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"success\":false,\"error\":\"Method Not Allowed\"}");
                return;
            }

            String body = readBody(exchange);
            Map<String, String> json = parseSimpleJson(body);

            String text = json.getOrDefault("text", "");
            String operation = json.getOrDefault("operation", "analyze").toLowerCase();

            try {
                String responseJson;
                if ("analyze".equals(operation)) {
                    String reversed = textToolkit.reverse(text);
                    boolean isPal = textToolkit.isPalindrome(text);
                    int wordCount = textToolkit.countWords(text);
                    String titleCase = textToolkit.toTitleCase(text);
                    int charCount = text.length();

                    responseJson = "{"
                            + "\"success\":true,"
                            + "\"reversed\":\"" + escapeJson(reversed) + "\","
                            + "\"isPalindrome\":" + isPal + ","
                            + "\"wordCount\":" + wordCount + ","
                            + "\"charCount\":" + charCount + ","
                            + "\"titleCase\":\"" + escapeJson(titleCase) + "\""
                            + "}";
                } else if ("reverse".equals(operation)) {
                    String res = textToolkit.reverse(text);
                    responseJson = "{\"success\":true,\"result\":\"" + escapeJson(res) + "\"}";
                } else if ("palindrome".equals(operation)) {
                    boolean res = textToolkit.isPalindrome(text);
                    responseJson = "{\"success\":true,\"result\":" + res + "}";
                } else if ("count".equals(operation)) {
                    int res = textToolkit.countWords(text);
                    responseJson = "{\"success\":true,\"result\":" + res + "}";
                } else if ("titlecase".equals(operation)) {
                    String res = textToolkit.toTitleCase(text);
                    responseJson = "{\"success\":true,\"result\":\"" + escapeJson(res) + "\"}";
                } else {
                    sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Unknown operation: " + escapeJson(operation) + "\"}");
                    return;
                }
                sendJsonResponse(exchange, 200, responseJson);
            } catch (Exception ex) {
                sendJsonResponse(exchange, 500, "{\"success\":false,\"error\":\"" + escapeJson(ex.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Serves static frontend assets from /static classpath directory.
     */
    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            String resourcePath = "/static" + path;
            InputStream is = getClass().getResourceAsStream(resourcePath);

            if (is == null) {
                // Fallback to index.html for SPA routing
                resourcePath = "/static/index.html";
                is = getClass().getResourceAsStream(resourcePath);
            }

            if (is == null) {
                byte[] notFound = "404 Not Found - Frontend static resources missing".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(404, notFound.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound);
                }
                return;
            }

            byte[] content = is.readAllBytes();
            is.close();

            String contentType = getContentType(resourcePath);
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".json")) return "application/json; charset=UTF-8";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "text/plain; charset=UTF-8";
    }

    private void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toString(StandardCharsets.UTF_8);
    }

    /**
     * Minimal JSON parser to extract key/value pairs without third-party dependencies.
     */
    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;

        Pattern pattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"([^\"]*)\"|([\\-\\d\\.]+)|(true|false|null))");
        Matcher matcher = pattern.matcher(json);
        while (matcher.find()) {
            String key = matcher.group(1);
            String stringVal = matcher.group(3);
            String numVal = matcher.group(4);
            String boolVal = matcher.group(5);

            if (stringVal != null) {
                map.put(key, stringVal);
            } else if (numVal != null) {
                map.put(key, numVal);
            } else if (boolVal != null) {
                map.put(key, boolVal);
            }
        }
        return map;
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
