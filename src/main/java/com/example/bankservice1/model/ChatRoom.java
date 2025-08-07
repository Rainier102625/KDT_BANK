package com.example.bankservice1.model;

public class ChatRoom {

    private long chatIndex;
    private String chatName;

    public long getChatIndex() {
        return chatIndex;
    }

    public void setChatIndex(long chatIndex) {
        this.chatIndex = chatIndex;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public ChatRoom() {}

    public ChatRoom(long chatIndex, String chatName) {
        this.chatIndex = chatIndex;
        this.chatName = chatName;
    }

    public String toString(){
        return chatName;
    }
}
