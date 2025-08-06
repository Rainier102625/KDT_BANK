package com.example.bankservice1.model;

public class SendMessageRequest {
    private Long chatIndex;
    private Long userIndex;
    private String content;

    public Long getChatIndex() {
        return chatIndex;
    }

    public void setChatIndex(Long chatIndex) {
        this.chatIndex = chatIndex;
    }

    public Long getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(Long userIndex) {
        this.userIndex = userIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SendMessageRequest(){}

    public SendMessageRequest(Long chatIndex, Long userIndex, String content) {
        this.chatIndex = chatIndex;
        this.userIndex = userIndex;
        this.content = content;
    }
}
