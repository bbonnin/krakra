package io.millesabords.krakra.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.millesabords.krakra.Broker;
import io.millesabords.krakra.Message;
import io.millesabords.krakra.Topic;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static io.millesabords.krakra.ApiConstants.API_PATH;
import static io.millesabords.krakra.handler.HttpHelper.getBodyAsByeArray;
import static io.millesabords.krakra.handler.HttpHelper.getHeader;
import static io.millesabords.krakra.handler.HttpHelper.getParameters;
import static io.millesabords.krakra.handler.HttpHelper.sendBadRequestResponse;
import static io.millesabords.krakra.handler.HttpHelper.sendCreatedResponse;
import static io.millesabords.krakra.handler.HttpHelper.sendNotFoundResponse;
import static io.millesabords.krakra.handler.HttpHelper.sendResponse;

/**
 * Provides methods to handle topic API requests.
 */
public final class TopicApiHandler {

    private static final Logger logger = Logger.getLogger(TopicApiHandler.class.getName());

    public static final String API_CONSUMER_TOPIC = API_PATH + "/topic/consumer";

    public static final HttpHandler NEW_CONSUMER_HANDLER = TopicApiHandler::addConsumer;

    public static final HttpHandler DELETE_CONSUMER_HANDLER = TopicApiHandler::deleteConsumer;

    public static final String API_MESSAGE_TOPIC = API_PATH + "/topic/message";

    public static final HttpHandler POST_MESSAGE_HANDLER = TopicApiHandler::postMessage;

    public static final HttpHandler READ_MESSAGE_HANDLER = TopicApiHandler::readMessage;

    private static void postMessage(HttpExchange exchange) throws IOException {
        Map<String, List<String>> params = getParameters(exchange.getRequestURI());
        String id = null, topicName = null;

        if (params.containsKey("id")) {
            id = params.get("id").get(0);
        }
        if (params.containsKey("topic")) {
            topicName = params.get("topic").get(0);
        }

        byte[] body = getBodyAsByeArray(exchange, Topic.MAX_MESSAGE_SIZE);

        if (id == null || topicName == null || body == null) {
            logger.warning("Cannot process the message without id or topic name, or with a too large body");
            sendBadRequestResponse(exchange);
        } else {
            logger.fine(String.format("New message for topic %s", topicName));
            Broker.get().newMessage(id, topicName, getHeader(exchange, "Content-Type"), body);
            sendCreatedResponse(exchange);
        }
    }

    private static void readMessage(HttpExchange exchange) throws IOException {
        Map<String, List<String>> params = getParameters(exchange.getRequestURI());
        String consumer = null, topicName = null;

        if (params.containsKey("consumer")) {
            consumer = params.get("consumer").get(0);
        }
        if (params.containsKey("topic")) {
            topicName = params.get("topic").get(0);
        }

        if (consumer == null || topicName == null) {
            logger.warning("Cannot process the read without consumer id or topic name");
            sendBadRequestResponse(exchange);
        } else {
            Message msg = Broker.get().readMessage(consumer, topicName);
            if (msg != null) {
                exchange.getResponseHeaders().set("X-MSG-ID", msg.id());
                exchange.getResponseHeaders().set("Content-Type", msg.contentType());
                sendResponse(exchange, msg.body());
            } else {
                sendNotFoundResponse(exchange);
            }
        }
    }

    private static void addConsumer(HttpExchange exchange) throws IOException {
        Map<String, List<String>> params = getParameters(exchange.getRequestURI());
        String id = null, topicName = null;

        if (params.containsKey("id")) {
            id = params.get("id").get(0);
        }
        if (params.containsKey("topic")) {
            topicName = params.get("topic").get(0);
        }

        if (id == null || topicName == null) {
            logger.warning("Cannot create consumer without id or topic name");
            sendBadRequestResponse(exchange);
        } else {
            logger.info(String.format("New consumer %s for topic %s", id, topicName));
            Broker.get().addConsumer(id, topicName);
            sendCreatedResponse(exchange);
        }
    }

    private static void deleteConsumer(HttpExchange exchange) throws IOException {

    }
}
