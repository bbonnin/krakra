package io.millesabords.krakra;

import com.sun.net.httpserver.HttpServer;
import io.millesabords.krakra.handler.ApiRequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static io.millesabords.krakra.handler.AdminApiHandler.API_ADMIN_PONG;
import static io.millesabords.krakra.handler.AdminApiHandler.PONG_HANDLER;
import static io.millesabords.krakra.handler.BrokerApiHandler.API_STOP_BROKER;
import static io.millesabords.krakra.handler.BrokerApiHandler.STOP_BROKER_HANDLER;
import static io.millesabords.krakra.handler.TopicApiHandler.API_CONSUMER_TOPIC;
import static io.millesabords.krakra.handler.TopicApiHandler.API_MESSAGE_TOPIC;
import static io.millesabords.krakra.handler.TopicApiHandler.DELETE_CONSUMER_HANDLER;
import static io.millesabords.krakra.handler.TopicApiHandler.NEW_CONSUMER_HANDLER;
import static io.millesabords.krakra.handler.TopicApiHandler.POST_MESSAGE_HANDLER;
import static io.millesabords.krakra.handler.TopicApiHandler.READ_MESSAGE_HANDLER;

public final class Broker {

    private static final Logger logger = Logger.getLogger(Broker.class.getName());

    private static Broker THIS;

    private HttpServer server;

    private Map<String, Topic> topics = new ConcurrentHashMap<>();

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

            ApiRequestHandler.createFor(server)
                    .get(API_ADMIN_PONG, PONG_HANDLER)
                    .post(API_STOP_BROKER, STOP_BROKER_HANDLER)
                    .post(API_CONSUMER_TOPIC, NEW_CONSUMER_HANDLER)
                    .delete(API_CONSUMER_TOPIC, DELETE_CONSUMER_HANDLER)
                    .post(API_MESSAGE_TOPIC, POST_MESSAGE_HANDLER)
                    .get(API_MESSAGE_TOPIC, READ_MESSAGE_HANDLER);

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

    public Topic getOrCreateTopic(String name) {
        topics.putIfAbsent(name, new Topic(name));
        return topics.get(name);
    }

    public void addConsumer(String id, String topicName) {
        Topic topic = getOrCreateTopic(topicName);
        topic.addConsumer(id);
    }

    public void removeConsumer(String id, String topicName) {
        topics.computeIfPresent(topicName, (name, topic) -> {
            topic.removeConsumer(id);
            return topic;
        });
    }

    public void newMessage(String id, String topicName, String contentType, byte[] body) {
        Message msg = Message.create()
                .id(id)
                .contentType(contentType)
                .topic(topicName)
                .body(body);

        Topic topic = getOrCreateTopic(topicName);
        topic.addMessage(msg);
    }

    public Message readMessage(String consumer, String topicName) {
        Topic topic = topics.get(topicName);
        Message msg = topic.getNextMessage(consumer);
        return msg;
    }
}
