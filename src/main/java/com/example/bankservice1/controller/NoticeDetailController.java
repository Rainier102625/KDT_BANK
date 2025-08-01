package com.example.bankservice1.controller;

import com.example.bankservice1.model.*;
import com.example.bankservice1.model.Notice;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class NoticeDetailController implements Initializable {
    @FXML private TextField titleTextField;
    @FXML private Label dateLabel;
    @FXML private TextArea contentTextArea;

    @FXML private MenuButton noticeMenuBox;

    public MenuButton getNoticeMenuBox() {
        return noticeMenuBox;
    }

    public void setNoticeMenuBox(MenuButton noticeMenuBox) {
        this.noticeMenuBox = noticeMenuBox;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    // Notice 객체를 받아와 UI에 데이터를 설정하는 메소드
    public void setNotice(Notice notice) {
        titleTextField.setText(notice.getNoticeTitle());
        dateLabel.setText("날짜 "+notice.getCreatedAt());
        contentTextArea.setText(notice.getNoticeContent());
    }

    public void setMenuButton(MenuButton MB){
        MenuItem modifyItem = new MenuItem("수정");
        MenuItem deleteItem = new MenuItem("삭제");


        // 2. 각 메뉴 아이템에 클릭 이벤트 설정
//        modifyItem.setOnAction(event -> handleModify());
//        deleteItem.setOnAction(event -> handleDelete());
//        reportItem.setOnAction(event -> handleReport());

        // 3. MenuButton에 메뉴 아이템들 추가
        MB.getItems().addAll(modifyItem, deleteItem);
    }

    private void handleModify() {
        // YourData selectedData = myTableView.getSelectionModel().getSelectedItem();
        // if (selectedData != null) { ... }
        System.out.println("수정 메뉴 클릭됨");
        // TODO: 실제 수정 로직 구현
    }

    private void handleDelete() {
        // YourData selectedData = myTableView.getSelectionModel().getSelectedItem();
        // if (selectedData != null) { ... }
        System.out.println("삭제 메뉴 클릭됨");
        // TODO: 실제 삭제 로직 구현
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
