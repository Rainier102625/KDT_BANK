package com.example.bankservice1.model;

public class UnreadCount {

    private long NOTICE;
    private long CHAT;
    private long PRODUCT;

    public long getNOTICE() {
        return NOTICE;
    }

    public void setNOTICE(long NOTICE) {
        this.NOTICE = NOTICE;
    }

    public long getCHAT() {
        return CHAT;
    }

    public void setCHAT(long CHAT) {
        this.CHAT = CHAT;
    }

    public long getPRODUCT() {
        return PRODUCT;
    }

    public void setPRODUCT(long PRODUCT) {
        this.PRODUCT = PRODUCT;
    }

    public UnreadCount(){}

    public UnreadCount(long NOTICE, long CHAT, long PRODUCT) {
        this.NOTICE = NOTICE;
        this.CHAT = CHAT;
        this.PRODUCT = PRODUCT;
    }
}
