package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class HttpHelper {

    private static final Logger logger = Logger.getLogger(HttpHelper.class.getName());

    private HttpHelper() {
        // No instance
    }

    public static void sendResponse(HttpExchange exchange, byte[] body) throws IOException {
        exchange.sendResponseHeaders(200, body.length);
        OutputStream out = exchange.getResponseBody();
        out.write(body);
        out.flush();
        out.close();
    }

    public static void sendEmptyResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(204, -1);
    }

    public static void sendCreatedResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, -1);
    }

    public static void sendBadRequestResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, -1);
    }

    public static void sendNotFoundResponse(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, -1);
    }

    public static Map<String, List<String>> getParameters(URI url) {
        if (url.getQuery() == null) {
            return Map.of();
        }

        return Arrays.stream(url.getQuery().split("&"))
                .map(param -> {
                    int idx = param.indexOf("=");
                    String name = idx > 0 ? param.substring(0, idx) : param;
                    String value = idx > 0 && param.length() > idx + 1 ? param.substring(idx + 1) : null;
                    return new SimpleEntry<>(
                            URLDecoder.decode(name, StandardCharsets.UTF_8),
                            URLDecoder.decode(value, StandardCharsets.UTF_8));
                })
                .collect(Collectors.groupingBy(SimpleEntry::getKey, HashMap::new,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    public static String getHeader(HttpExchange exchange, String headerName) {
        return exchange.getRequestHeaders().getFirst(headerName);
    }

    public static byte[] getBodyAsByeArray(HttpExchange exchange, int maxSize) {
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int read;
        byte[] data = new byte[1024];

        try {
            while ((read = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
                if (buffer.size() > maxSize) {
                    logger.severe("Body of the message if too large");
                    return null;
                }
            }
        } catch (IOException ioe) {
            logger.severe("Error when reading the body: " + ioe.getMessage());
            return null;
        }

        return buffer.toByteArray();
    }
}
