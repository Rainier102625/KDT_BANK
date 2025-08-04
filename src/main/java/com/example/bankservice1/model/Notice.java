package com.example.bankservice1.model;

import java.time.LocalDateTime;

public class Notice {

    // 필드는 이전과 동일
    // 1. 프로퍼티 필드 선언
    private int noticeIndex;
    private String noticeTitle;
    private String noticeContent;
    private String createdAt;

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setNoticeIndex(int noticeIndex) {
        this.noticeIndex = noticeIndex;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public Notice (){}

    public Notice(int noticeIndex, String noticeTitle, String createdAt) {
        Notice notice = new Notice(noticeIndex, null,noticeTitle, createdAt);
    }

    public Notice(int noticeIndex, String noticeTitle, String noticeContent, String createdAt) {
        this.noticeIndex = noticeIndex;
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.createdAt=createdAt;
    }

    // Getters
    public int getNoticeIndex() {return noticeIndex;}
    public String getNoticeTitle() { return noticeTitle; }
    public String getNoticeContent() { return noticeContent; }
    public String getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return getNoticeIndex() + getNoticeTitle() + getCreatedAt();
    }

}