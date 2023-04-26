package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.millesabords.krakra.Broker;

import java.io.IOException;

import static io.millesabords.krakra.handler.HttpHandlerHelper.sendEmptyResponse;
import static io.millesabords.krakra.handler.HttpHandlerHelper.sendResponse;

public final class BrokerHandler {

    public static final HttpHandler BROKER_HANDLER = exchange -> processQuery(exchange);

    private static void processQuery(HttpExchange exchange) throws IOException {

        Broker.get().stop();
        sendEmptyResponse(exchange);
    }
}
