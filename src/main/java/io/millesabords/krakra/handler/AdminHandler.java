package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.millesabords.krakra.Broker;

import java.io.IOException;

import static io.millesabords.krakra.handler.HttpHandlerHelper.sendEmptyResponse;
import static io.millesabords.krakra.handler.HttpHandlerHelper.sendResponse;

public final class AdminHandler {

    private static final String PONG = "pong";

    public static final HttpHandler PONG_HANDLER = exchange -> sendResponse(exchange, PONG.getBytes());

    public static final HttpHandler STOP_HANDLER = AdminHandler::stopBroker;

    private static void stopBroker(HttpExchange exchange) throws IOException {
        Broker.get().stop();
        sendEmptyResponse(exchange);
    }
}
