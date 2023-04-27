package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ApiRequestHandler implements HttpHandler {

    private static final Logger logger = Logger.getLogger(ApiRequestHandler.class.getName());

    private static final String GET_METHOD = "GET";

    private static final String POST_METHOD = "POST";

    private static final String DELETE_METHOD = "DELETE";

    // First key = context, Second key = HTTP method
    private final Map<String, Map<String, HttpHandler>> handlers = new HashMap<>();

    private HttpServer server;

    public static ApiRequestHandler createFor(HttpServer server) {
        ApiRequestHandler requestHandler = new ApiRequestHandler();
        requestHandler.server = server;
        return requestHandler;
    }

    public ApiRequestHandler get(String context, HttpHandler handler) {
        addHandler(GET_METHOD, context, handler);
        return this;
    }

    public ApiRequestHandler post(String context, HttpHandler handler) {
        addHandler(POST_METHOD, context, handler);
        return this;
    }

    public ApiRequestHandler delete(String context, HttpHandler handler) {
        addHandler(DELETE_METHOD, context, handler);
        return this;
    }

    private void addHandler(String method, String context, HttpHandler handler) {
        handlers.compute(context, (ctx, methodHandlers) -> {
            if (methodHandlers == null) {
                server.createContext(context, this);
                methodHandlers = new HashMap<>();
            }
            methodHandlers.put(method, handler);
            return methodHandlers;
        });
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String queryPath = exchange.getHttpContext().getPath();
        Map<String, HttpHandler> pathHandlers = handlers.get(queryPath);
        String method = exchange.getRequestMethod();

        if (pathHandlers.containsKey(method)) {
            logger.info("Process " + queryPath);
            HttpHandler handler = pathHandlers.get(method);
            handler.handle(exchange);
        } else {
            logger.warning(String.format("Method %s not allowed for %s", method, queryPath));
            exchange.sendResponseHeaders(405, -1);
        }
    }
}
