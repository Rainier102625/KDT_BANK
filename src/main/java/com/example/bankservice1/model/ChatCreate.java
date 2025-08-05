package com.example.bankservice1.model;

import java.util.List;

public class ChatCreate {
    private String name;
    private List<Integer> member;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getMember() {
        return member;
    }

    public void setMember(List<Integer> member) {
        this.member = member;
    }

    public ChatCreate(String name,List<Integer> member){
        this.name=name;
        this.member=member;
    }

}
