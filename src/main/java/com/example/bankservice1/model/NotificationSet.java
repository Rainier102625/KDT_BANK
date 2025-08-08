package com.example.bankservice1.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationSet {
    private long id;
    private long userId;
    private String type;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;

    public NotificationSet() {}

    public NotificationSet(long id, long userId, String type, String message, LocalDateTime createdAt, boolean read) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getUserId() {return userId;}

    public long getId() {return id;}

    public boolean isRead() {return read;}

    @Override
    public String toString() {
        return type + " " + message + " " + createdAt;
    }

}
