package io.millesabords.krakra.standalone;

import io.millesabords.krakra.Broker;

public class Main {

    public static void main(String[] args) {
        Broker broker = Broker.get();
        broker.start();

        Runtime.getRuntime().addShutdownHook(new Thread(broker::stop));
    }
}
