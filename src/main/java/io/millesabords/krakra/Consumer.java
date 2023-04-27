package io.millesabords.krakra;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.logging.Logger;

import static io.millesabords.krakra.handler.TopicApiHandler.API_CONSUMER_TOPIC;
import static io.millesabords.krakra.handler.TopicApiHandler.API_MESSAGE_TOPIC;

public class Consumer {

    private static final Logger logger = Logger.getLogger(Consumer.class.getName());

    private String id;

    private String brokerUrl;

    private String topic;

    private boolean active = true;

    private MessageListener messageListener;

    private Consumer() {
        this.id = UUID.randomUUID().toString();
    }

    public static Consumer create() {
        return new Consumer();
    }

    public Consumer id(String id) {
        this.id = id;
        return this;
    }

    public Consumer broker(String brokerUrl) {
        this.brokerUrl = brokerUrl;
        return this;
    }

    public Consumer topic(String topic) {
        this.topic = topic;
        return this;
    }

    public Consumer listener(MessageListener messageListener) {
        this.messageListener = messageListener;
        return this;
    }

    public void subscribe() {
        HttpClient httpClient = HttpClient.newHttpClient();

        // First, subscribe to the topic
        try {
            logger.info("Subscribe to broker " + brokerUrl);
            HttpResponse<String> subResp = httpClient.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create(brokerUrl + API_CONSUMER_TOPIC + "?id=" + id + "&topic=" + topic))
                            .POST(HttpRequest.BodyPublishers.noBody())
                            .build(),
                    HttpResponse.BodyHandlers.ofString());

            if (subResp.statusCode() != 201) {
                logger.severe("Cannot subscribe to the broker: status code is " + subResp.statusCode());
                throw new RuntimeException("Cannot subscribe");
            }

        } catch (IOException | InterruptedException e) {
            logger.severe("Error when subscribing to the broker: " + e.getMessage());
            throw new RuntimeException(e);
        }

        // Then, pull messages until the consumer is stopped
        logger.info("Start pulling messages");
        while (active) {
            try {
                HttpResponse<byte[]> response = httpClient.send(
                        HttpRequest.newBuilder()
                                .uri(URI.create(brokerUrl + API_MESSAGE_TOPIC + "?consumer=" + id + "&topic=" + topic)) //TODO: add parameter to get n messages
                                .build(),
                        HttpResponse.BodyHandlers.ofByteArray());

                if (response.statusCode() == 200 && messageListener != null) {
                    Message message = Message.create()
                        .id(response.headers().firstValue("X-MSG-ID").get())
                        .topic(topic)
                        .contentType(response.headers().firstValue("Content-Type").get())
                        .body(response.body());
                    messageListener.onMessage(message);
                }

                Thread.sleep(500); //TODO: to be configurable

            } catch (IOException e) {
                logger.severe("Error when querying the broker: " + e.getMessage());
            } catch (InterruptedException e) {
                // Nothing to do
            }
        }
    }

    public void unsubscribe() {
        active = false;
    }
}
