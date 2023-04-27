package io.millesabords.krakra;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {

    private String id;

    private String topic;

    private String contentType;

    private LocalDateTime postTime;

    private byte[] body;

    private Message() {}

    public static Message create() {
        Message msg = new Message();
        msg.postTime = LocalDateTime.now();
        return msg;
    }

    public String id() {
        return id;
    }

    public Message id(String id) {
        this.id = id;
        return this;
    }

    public String topic() {
        return topic;
    }

    public Message topic(String topic) {
        this.topic = topic;
        return this;
    }

    public String contentType() {
        return contentType;
    }

    public Message contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public LocalDateTime postTime() {
        return postTime;
    }

    public byte[] body() {
        return body;
    }

    public Message body(byte[] body) {
        this.body = body;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id) && Objects.equals(topic, message.topic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, topic);
    }
}
