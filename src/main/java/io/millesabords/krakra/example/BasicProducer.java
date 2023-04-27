package io.millesabords.krakra.example;

import io.millesabords.krakra.Message;
import io.millesabords.krakra.Producer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class BasicProducer {

    public static void main(String[] args) {
        Producer producer = Producer.create()
                .broker("http://localhost:65123");

        IntStream.range(0, 10).forEach(i -> {
            Message msg = Message.create()
                    .body(("Hello #" + i + " from outer space").getBytes())
                    .contentType("text/plain")
                    .topic("test")
                    .id(UUID.randomUUID().toString());

            producer.sendSync(msg);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                // Ignore it
            }
        });
    }
}
