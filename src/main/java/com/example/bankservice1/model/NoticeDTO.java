package com.example.bankservice1.model;

public class NoticeDTO {

    private String noticeTitle;
    private String noticeContent;

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticedtoTitle) {
        this.noticeContent = noticedtoTitle;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = this.noticeContent;
    }

    public NoticeDTO (String noticeTitle, String noticeContent){
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
    }
}
