package com.example.bankservice1.controller;

import com.example.bankservice1.model.Notice;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class NoticeListCell extends ListCell<Notice> {
    private static final int CELL_HEIGHT = 60;

    private BorderPane pane = new BorderPane();
    private HBox leftBox = new HBox(10);
    private Label titleLabel = new Label();
    private Label dateLabel = new Label();

    public NoticeListCell() {
        // 셀 스타일
        setPrefHeight(CELL_HEIGHT);

        setPadding(new Insets(0, 25, 0, 25));

        setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        setAlignment(Pos.CENTER_LEFT);

        leftBox.setAlignment(Pos.CENTER_LEFT);
        pane.setLeft(leftBox);
        pane.setRight(dateLabel);

    }

    @Override
    protected void updateItem(Notice notice, boolean empty) {
        super.updateItem(notice, empty);

        if (empty || notice == null) {
            setGraphic(null);
            setStyle("");
        } else {
            // 제목과 날짜 설정
            if(!leftBox.getChildren().contains(titleLabel)) {
                leftBox.getChildren().add(titleLabel);
            }
            titleLabel.setText(notice.getNoticeTitle());
            dateLabel.setText(notice.getNoticeContent());

            setGraphic(pane);

            setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        }
    }
}
