package com.example.bankservice1.controller;

import com.example.bankservice1.model.Notice;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class NoticeListCell extends ListCell<Notice> {

    private final BorderPane pane;
    private final Label titleLabel;
    private final Label dateLabel;

    public NoticeListCell() {
        // 1. UI 컴포넌트 생성 및 구조 설정은 생성자에서 한 번만 합니다.
        titleLabel = new Label();
        dateLabel = new Label();

        // 제목을 담을 왼쪽 영역
        HBox leftBox = new HBox(titleLabel);
        leftBox.setAlignment(Pos.CENTER_LEFT);
        leftBox.setPadding(new Insets(10, 15, 10, 15)); // 안쪽 여백

        // 날짜를 담을 오른쪽 영역
        HBox rightBox = new HBox(dateLabel);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setPadding(new Insets(10, 15, 10, 15)); // 안쪽 여백

        // 전체 레이아웃을 담당할 BorderPane
        pane = new BorderPane();
        pane.setLeft(leftBox);
        pane.setRight(rightBox);

        // 셀 배경 스타일 (그림자 효과 등)
        setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        // 셀 사이의 간격을 위해 바깥 여백 설정
        setPadding(new Insets(5, 10, 5, 10));
    }

    @Override
    protected void updateItem(Notice notice, boolean empty) {
        super.updateItem(notice, empty);

        if (empty || notice == null) {
            setGraphic(null); // 빈 셀은 아무것도 표시하지 않음
        } else {
            // 제목 getter
            titleLabel.setText(notice.getNoticeTitle());
            // 시간 getter
            dateLabel.setText(notice.getCreatedAt());

            setGraphic(pane); // 완성된 그래픽을 셀에 적용
        }
    }
}
