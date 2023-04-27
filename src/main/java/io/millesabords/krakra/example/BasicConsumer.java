package io.millesabords.krakra.example;

import io.millesabords.krakra.Consumer;

public class BasicConsumer {

    public static void main(String[] args) {
        Consumer consumer = Consumer.create()
                .broker("http://localhost:65123")
                .topic("test")
                .listener(message -> {
                    System.out.println("NEW MESSAGE: " + new String(message.body()));
                });

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::unsubscribe));

        consumer.subscribe();
    }
}
