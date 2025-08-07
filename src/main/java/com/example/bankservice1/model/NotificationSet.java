package com.example.bankservice1.model;

public class NotificationSet {
    private final String type;
    private final String content;
    private final String timestamp;

    public NotificationSet(String type, String content, String timestamp) {
        this.type = type;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
