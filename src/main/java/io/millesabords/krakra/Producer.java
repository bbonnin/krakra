package io.millesabords.krakra;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static io.millesabords.krakra.handler.TopicApiHandler.API_MESSAGE_TOPIC;

public class Producer {

    private static final Logger logger = Logger.getLogger(Producer.class.getName());

    private String brokerUrl;

    private HttpClient httpClient = HttpClient.newHttpClient();

    private Producer() {
    }

    public static Producer create() {
        return new Producer();
    }

    public Producer broker(String brokerUrl) {
        this.brokerUrl = brokerUrl;
        return this;
    }

    public void sendSync(Message message) {
        try {
            HttpResponse<String> response = httpClient.send(
                    buidHttpRequest(message),
                    HttpResponse.BodyHandlers.ofString());

            logger.info("Response status code: " + response.statusCode());

        } catch (IOException | InterruptedException e) {
            logger.severe("Error when sending message to the broker: " +
                    (e.getMessage() == null ? e.getClass().getName() : e.getMessage()));
        }
    }

    public CompletableFuture<HttpResponse<String>> sendAsync(Message message) {
        return httpClient.sendAsync(
                buidHttpRequest(message),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest buidHttpRequest(Message message) {
        return HttpRequest.newBuilder()
                .uri(URI.create(brokerUrl + API_MESSAGE_TOPIC + "?id=" + message.id() + "&topic=" + message.topic()))
                .header("Content-Type", message.contentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(message.body()))
                .build();
    }
}
