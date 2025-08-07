package com.example.bankservice1.controller;
import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.messaging.simp.stomp.StompSession;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

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

    @FXML private Label unreadCountBadge;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final LongProperty unreadCount = new SimpleLongProperty(0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        unreadCount.addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > 0) {
                unreadCountBadge.setText(String.valueOf(newVal)); // 라벨 텍스트 변경
                unreadCountBadge.setVisible(true);                 // 라벨 보이기
            } else {
                unreadCountBadge.setVisible(false);                // 0개면 라벨 숨기기
            }
        });

        // initialize() 메소드가 실행되자마자 공지사항 화면을 로드하는 메소드를 호출합니다.

        showNoticeView();

        String userName = UserSession.getInstance().getUserName();
        name.setText(userName);

        menu.setVisible(false);
        employeeSearch.setVisible(false);
        if(UserSession.getInstance().getAdmin()) {
            menu.setVisible(true);
            employeeSearch.setVisible(true);
        }

        loadInitialUnreadCount();
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
    @FXML
    private void showAccountApprovalView() {
        try {
            Parent noticePage = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/Accountapproval.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(noticePage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }
    @FXML
    private void showChatView() {
        try {
            Parent chatPage = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/ChatView.fxml"));
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
            Parent chatPage = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/CustomerSearch.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(chatPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void showEmployeeCheckView() {
        try {
            Parent chatPage = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/EmployeeCheck.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(chatPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void showMypage() {
        try {
            Parent chatPage = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/mypage.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(chatPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void showProductManagement() {
        try {
            Parent chatPage = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/ProductManagement.fxml"));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(chatPage);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

    @FXML
    private void Logout(){

    }

    @FXML
    private void handleNotificationClick(){

    }

    private void loadInitialUnreadCount() {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/notifications/unread-count?userId=" + UserSession.getInstance().getUserIndex()))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            // 로그인 성공 처리 (예: 메인 화면으로 전환)
                            // 1. 응답 본문(JSON 문자열) 가져오기
                            try {
                                String responseBody = response.body();

                                Gson gson = new Gson();

                                UnreadCount URC = gson.fromJson(responseBody, UnreadCount.class);

                                long initialCount = URC.getPRODUCT() + URC.getCHAT() + URC.getNOTICE();

                                unreadCount.set(initialCount);

                                System.out.println("📞 최초 안 읽은 알림 개수(" + initialCount + "개)를 로드했습니다.");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (response.statusCode() == 400) {
                            System.out.println("불러오기 실패");
                            showAlert(Alert.AlertType.ERROR, "실패", "400");
                        } else {
                            // 로그인 실패 처리
                            System.out.println("잘못된 접근");
                            showAlert(Alert.AlertType.ERROR, "실패", "잘못된 요청");
                        }
                    });
                });
    }




    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}