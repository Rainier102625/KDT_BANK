package com.example.bankservice1.model;

import java.util.List;

public class ChatMemberAdd {

    private long chatIndex;
    private List<Long> member;


    public long getChatIndex() {
        return chatIndex;
    }

    public void setChatIndex(long chatIndex) {
        this.chatIndex = chatIndex;
    }

    public List<Long> getMember() {
        return member;
    }

    public void setMember(List<Long> member) {
        this.member = member;
    }

    public ChatMemberAdd(long chatIndex, List<Long> member) {
        this.chatIndex = chatIndex;
        this.member = member;
    }

    public ChatMemberAdd() {}
}
