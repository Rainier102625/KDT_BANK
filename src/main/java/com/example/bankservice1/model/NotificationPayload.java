package com.example.bankservice1.model;

import java.time.LocalDateTime;

public class NotificationPayload {
    private long id;
    private String type;
    private String message;
    private long referenceId;
    private LocalDateTime createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(long referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public NotificationPayload() {
    }

    public NotificationPayload(long id, String type, String message, long referenceId, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationPayload{" + "type=" + type + ", message=" + message + ", referenceId=" + referenceId + ", createdAt=" + createdAt + '}';
    }
}
