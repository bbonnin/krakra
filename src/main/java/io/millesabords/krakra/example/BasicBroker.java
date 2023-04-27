package io.millesabords.krakra.example;

import io.millesabords.krakra.Broker;

public class BasicBroker {

    public static void main(String[] args) {
        Broker broker = Broker.get();
        broker.start();

        Runtime.getRuntime().addShutdownHook(new Thread(broker::stop));
    }
}
