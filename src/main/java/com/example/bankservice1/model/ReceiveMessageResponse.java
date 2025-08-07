package com.example.bankservice1.model;

import java.time.LocalDateTime;

public class ReceiveMessageResponse {
    private Long messageIndex;
    private String content;
    private LocalDateTime sentTime;
    private Long senderIndex;
    private String senderName;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Long getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(Long messageIndex) {
        this.messageIndex = messageIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }

    public Long getSenderIndex() {
        return senderIndex;
    }

    public void setSenderIndex(Long senderIndex) {
        this.senderIndex = senderIndex;
    }

    public ReceiveMessageResponse(){}

    public ReceiveMessageResponse(long messageIndex, String content, LocalDateTime sentTime, Long senderIndex, String senderName) {
        this.messageIndex = messageIndex;
        this.content = content;
        this.sentTime = sentTime;
        this.senderIndex = senderIndex;
        this.senderName = senderName;
    }


}
