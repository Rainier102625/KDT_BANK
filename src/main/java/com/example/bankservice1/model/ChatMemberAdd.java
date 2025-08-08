package com.example.bankservice1.model;

import java.util.List;

public class ChatMemberAdd {

    private int ChatRoomIndex;
    private List<Integer> member;


    public int getChatRoomIndex() {
        return ChatRoomIndex;
    }

    public void setChatRoomIndex(int chatRoomIndex) {
        ChatRoomIndex = chatRoomIndex;
    }

    public List<Integer> getMember() {
        return member;
    }

    public void setMember(List<Integer> member) {
        this.member = member;
    }

    public ChatMemberAdd(int ChatRoomIndex, List<Integer> member) {
        this.ChatRoomIndex = ChatRoomIndex;
        this.member = member;
    }

    public ChatMemberAdd() {}
}
