package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public final class HttpHandlerHelper {

    private HttpHandlerHelper() {
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
        exchange.sendResponseHeaders(204, 0);
    }
}
