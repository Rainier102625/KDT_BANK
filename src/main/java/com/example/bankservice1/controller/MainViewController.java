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
    @FXML private Label name;
    @FXML private Label menu;
    @FXML private Button employeeSearch;
    @FXML private  Button logoutBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // initialize() 메소드가 실행되자마자 공지사항 화면을 로드하는 메소드를 호출합니다.
        showNoticeView();
        String userName = UserSession.getInstance().getUserName();
        name.setText(userName);
        contentArea.setAlignment(Pos.CENTER);
        logoutBtn.setOnAction((event) -> Logout());

        menu.setVisible(false);
        employeeSearch.setVisible(false);
        if(UserSession.getInstance().getAdmin()) {
            menu.setVisible(true);
            employeeSearch.setVisible(true);
        }
    }

    @FXML
    private void showNoticeView() {
        try {
            Region noticePage = (Region) FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/NoticeView.fxml"));

            noticePage.prefWidthProperty().bind(contentArea.widthProperty());
            noticePage.prefHeightProperty().bind(contentArea.heightProperty());
            contentArea.getChildren().clear();
            contentArea.getChildren().add(noticePage);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }
    @FXML
    private void showAccountApprovalView() {
        try {
            Region accountPage = (Region) FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/Accountapproval.fxml"));
            accountPage.prefWidthProperty().bind(contentArea.widthProperty());
            accountPage.prefHeightProperty().bind(contentArea.heightProperty());
            contentArea.getChildren().clear();
            contentArea.getChildren().add(accountPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }
    @FXML
    private void showChatView() {
        try {
            Region chatPage = (Region) FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/ChatView.fxml"));
            chatPage.prefWidthProperty().bind(contentArea.widthProperty());
            chatPage.prefHeightProperty().bind(contentArea.heightProperty());
            contentArea.getChildren().clear();
            contentArea.getChildren().add(chatPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void showCustomerView() {
        try {
            Region CustomerPage = (Region) FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/CustomerSearch.fxml"));
            CustomerPage.prefWidthProperty().bind(contentArea.widthProperty());
            CustomerPage.prefHeightProperty().bind(contentArea.heightProperty());

            contentArea.getChildren().clear();
            contentArea.getChildren().add(CustomerPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void showEmployeeCheckView() {
        try {
            Region EmployeePage = (Region) FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/EmployeeCheck.fxml"));

            EmployeePage.prefWidthProperty().bind(contentArea.widthProperty());
            EmployeePage.prefHeightProperty().bind(contentArea.heightProperty());
            contentArea.getChildren().clear();
            contentArea.getChildren().add(EmployeePage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }
    @FXML
    private void showMypage() {
        try {
            Region MyPage =(Region) FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/mypage.fxml"));
            MyPage.prefWidthProperty().bind(contentArea.widthProperty());
            MyPage.prefHeightProperty().bind(contentArea.heightProperty());

            contentArea.getChildren().clear();
            contentArea.getChildren().add(MyPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }
    @FXML
    private void showProductManagement() {
        try {
            Region management = (Region) FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/ProductManagement.fxml"));
            management.prefWidthProperty().bind(contentArea.widthProperty());
            management.prefHeightProperty().bind(contentArea.heightProperty());
            contentArea.getChildren().clear();
            contentArea.getChildren().add(management);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    public void Logout(){
        tokenManager.getInstance().clearSession();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("로그인");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            Stage currentStage = (Stage)logoutBtn.getScene().getWindow(); //현재 창
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
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