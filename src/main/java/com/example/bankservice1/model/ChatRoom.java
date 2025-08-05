package com.example.bankservice1.model;

public class ChatRoom {

    private int chatIndex;
    private String chatName;

    public int getChatIndex() {
        return chatIndex;
    }

    public void setChatIndex(int chatIndex) {
        this.chatIndex = chatIndex;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public ChatRoom() {}

    public ChatRoom(int chatIndex, String chatName) {
        this.chatIndex = chatIndex;
        this.chatName = chatName;
    }

    public String toString(){
        return chatName;
    }
}
