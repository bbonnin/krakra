package io.millesabords.krakra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Manages the messages and the consumers of these messages.
 */
public class Topic implements Runnable {

    public static final int MAX_MESSAGE_SIZE = 1024;

    private static final Logger logger = Logger.getLogger(Topic.class.getName());

    private final String name;

    private boolean active = true;

    private final List<String> consumers = Collections.synchronizedList(new ArrayList<>());

    private final Map<Message, List<String>> messages = Collections.synchronizedMap(new LinkedHashMap<>());

    public Topic(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void run() {
        while (active) {
            try {
                logger.fine("Clean the message list");
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                // Nothing to do
            }
        }
        //TODO : clean the message list when the messages are read by all the consumers or when they are "too old"
    }

    public void addMessage(Message message) {
        //messages.add(message);
        List<String> currentConsumers = Collections.synchronizedList(new ArrayList<>());
        currentConsumers.addAll(consumers);
        messages.put(message, currentConsumers);
    }

    public Message getNextMessage(String consumer) {
        logger.info("Get next message for " + consumer);
        for (Map.Entry<Message, List<String>> entry : messages.entrySet()) {
            logger.info("Message " + entry.getKey().id() + " for " + entry.getValue());
            if (entry.getValue().contains(consumer)) {
                entry.getValue().remove(consumer);
                return entry.getKey();
            }
        }
        return null;
    }

    public void addConsumer(String id) {
        consumers.add(id);

        //TODO: this new user may be added as a new consumer for the existing message
        // It should be a parameter: where does it start to consume messages ? the new ones or the earliest ?
    }

    public void removeConsumer(String id) {
        consumers.remove(id);
        messages.forEach((msgId, consumers) -> consumers.remove(id));
    }
}
