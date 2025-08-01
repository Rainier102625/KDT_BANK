package com.example.bankservice1.controller;

import com.example.bankservice1.model.*;
import com.example.bankservice1.model.Notice;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NoticeDetailController {
    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private Label contentLabel;

    // Notice 객체를 받아와 UI에 데이터를 설정하는 메소드
    public void setNotice(Notice notice) {
        titleLabel.setText(notice.getNoticeTitle());
        dateLabel.setText(notice.getCreatedAt());
        contentLabel.setText(notice.getNoticeContent()); // Notice 모델에 getContent()가 있다고 가정
    }
}
