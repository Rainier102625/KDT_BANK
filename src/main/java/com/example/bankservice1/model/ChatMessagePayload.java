package com.example.bankservice1.model;

public class ChatMessagePayload {
    private long chatIndex;
    private long userIndex;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getChatIndex() {
        return chatIndex;
    }

    public void setChatIndex(long chatIndex) {
        this.chatIndex = chatIndex;
    }

    public long getUserIndex() {
        return userIndex;
    }

    public void setUserIndex(long userIndex) {
        this.userIndex = userIndex;
    }

    public ChatMessagePayload() {}

    public ChatMessagePayload(long chatIndex, long userIndex, String content) {
        this.chatIndex = chatIndex;
        this.userIndex = userIndex;
        this.content = content;
    }


}
