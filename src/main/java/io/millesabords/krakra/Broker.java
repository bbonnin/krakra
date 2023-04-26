package io.millesabords.krakra;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import static io.millesabords.krakra.handler.AdminHandler.PONG_HANDLER;
import static io.millesabords.krakra.handler.AdminHandler.STOP_HANDLER;
import static io.millesabords.krakra.handler.BrokerHandler.BROKER_HANDLER;

public final class Broker {

    private static final Logger logger = Logger.getLogger(Broker.class.getName());

    private static Broker THIS;

    private HttpServer server;

    public static Broker get() {
        if (THIS == null) {
            THIS = new Broker();
        }

        return THIS;
    }

    private Broker() {
        // Singleton: use getInstance
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(65123), 0);

            server.createContext("/api/v1/admin/ping", PONG_HANDLER);
            server.createContext("/api/v1/admin/stop", STOP_HANDLER);
            server.createContext("/api/v1/broker", BROKER_HANDLER);

            server.setExecutor(null); // Use default executor
            server.start();

            logger.info("Broker started");
        } catch (IOException e) {
            logger.severe("Problem when starting the broker: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (server != null) {
            logger.info("Stop the broker");
            server.stop(5);
        } else {
            logger.warning("No broker to stop");
        }
    }
}
