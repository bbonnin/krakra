package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.millesabords.krakra.Broker;

import java.io.IOException;

import static io.millesabords.krakra.ApiConstants.API_PATH;
import static io.millesabords.krakra.handler.HttpHelper.sendEmptyResponse;

/**
 * Provides methods to handle broker API requests.
 */
public final class BrokerApiHandler {

    public static final String API_STOP_BROKER = API_PATH + "/broker/stop";

    public static final HttpHandler STOP_BROKER_HANDLER = BrokerApiHandler::stopBroker;

    private static void stopBroker(HttpExchange exchange) throws IOException {
        Broker.get().stop();
        sendEmptyResponse(exchange);
    }
}
