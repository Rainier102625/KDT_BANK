package com.example.bankservice1.model;

import java.util.List;

public class ChatMemberAdd {

    private long ChatRoomIndex;
    private List<Integer> member;


    public long getChatRoomIndex() {
        return ChatRoomIndex;
    }

    public void setChatRoomIndex(long chatRoomIndex) {
        ChatRoomIndex = chatRoomIndex;
    }

    public List<Integer> getMember() {
        return member;
    }

    public void setMember(List<Integer> member) {
        this.member = member;
    }

    public ChatMemberAdd(long ChatRoomIndex, List<Integer> member) {
        this.ChatRoomIndex = ChatRoomIndex;
        this.member = member;
    }

    public ChatMemberAdd() {}
}
