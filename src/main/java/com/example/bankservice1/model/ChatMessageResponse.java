package com.example.bankservice1.model;

import java.util.Date;

public class ChatMessageResponse{
    private long messageIndex;
    private String content;
    private Date sentTime;
    private long senderIndex;
    private String senderName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(long messageIndex) {
        this.messageIndex = messageIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public long getSenderIndex() {
        return senderIndex;
    }

    public void setSenderIndex(long senderIndex) {
        this.senderIndex = senderIndex;
    }

    public ChatMessageResponse() {}

    public ChatMessageResponse(long messageIndex, String content, Date sentTime, long senderIndex) {
        this.messageIndex = messageIndex;
        this.content = content;
        this.sentTime = sentTime;
        this.senderIndex = senderIndex;
        this.senderName = senderName;
    }
}
