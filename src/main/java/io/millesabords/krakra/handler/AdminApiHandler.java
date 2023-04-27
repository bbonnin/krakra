package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpHandler;

import static io.millesabords.krakra.ApiConstants.API_PATH;
import static io.millesabords.krakra.handler.HttpHelper.sendResponse;

/**
 * Provides methods to handle admin API requests.
 */
public final class AdminApiHandler {

    public static final String API_ADMIN_PONG = API_PATH + "/admin/ping";

    private static final String PONG = "pong";

    public static final HttpHandler PONG_HANDLER = exchange -> sendResponse(exchange, PONG.getBytes());
}
