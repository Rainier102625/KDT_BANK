package com.example.bankservice1.controller;
import com.example.bankservice1.model.*;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class NoticeViewController implements Initializable {

    @FXML
    private Pagination pagination;

    private final ObservableList<Notice> allData = FXCollections.observableArrayList();
    private final int itemsPerPage = 8; // 한 페이지에 보여줄 항목 수

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 전체 데이터 준비 (25개의 샘플 데이터)
        for (int i = 1; i <= 25; i++) {
            allData.add(new Notice(String.format("공지사항 %02d - 중요한 내용입니다.", i)));
        }

        // 페이지 수 계산 및 설정
        int pageCount = (int) Math.ceil((double) allData.size() / itemsPerPage);
        if (pageCount == 0) pageCount = 1; // 데이터가 없어도 최소 1페이지는 표시
        pagination.setPageCount(pageCount);

        // PageFactory 설정: 페이지 번호를 받아 해당 페이지에 보여줄 Node(ListView)를 생성
        pagination.setPageFactory(this::createPage);
    }

    /**
     * 특정 페이지에 해당하는 ListView를 생성하여 반환합니다.
     * @param pageIndex 페이지 번호 (0부터 시작)
     * @return 해당 페이지의 내용이 담긴 ListView
     */
    private ListView<Notice> createPage(int pageIndex) {
        int fromIndex = pageIndex * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, allData.size());

        // 현재 페이지에 해당하는 데이터만 잘라내어 리스트 생성
        ObservableList<Notice> pageData = FXCollections.observableArrayList(allData.subList(fromIndex, toIndex));
        ListView<Notice> pageListView = new ListView<>(pageData);

        // 각 페이지의 ListView에 커스텀 셀 팩토리 적용
        pageListView.setCellFactory(param -> new ListCell<Notice>() {
            private final Label titleLabel = new Label();
            private final Button optionsButton = new Button("⋮");
            private final Pane spacer = new Pane();
            private final HBox contentBox = new HBox(titleLabel, spacer, optionsButton);

            {
                // 버튼에 CSS 클래스 적용
                optionsButton.getStyleClass().add("options-button");

                HBox.setHgrow(spacer, Priority.ALWAYS);
                contentBox.setAlignment(Pos.CENTER_LEFT);
                // HBox 자체에 스타일 클래스 적용
                contentBox.getStyleClass().add("custom-list-cell-content");
            }

            @Override
            protected void updateItem(Notice item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText(item.getTitle());
                    setGraphic(contentBox);
                }
            }
        });

        return pageListView;
    }
}