package com.example.bankservice1.controller;
import com.example.bankservice1.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable{

    @FXML
    private BorderPane mainPane;

    @FXML
    private Button NoticeViewButton;

    @FXML
    private VBox contentArea;

    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initialize() 메소드가 실행되자마자 공지사항 화면을 로드하는 메소드를 호출합니다.
        showNoticeView();
    }

    @FXML
    private void showNoticeView() {
        try {
            Parent noticePage = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/NoticeView.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(noticePage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}