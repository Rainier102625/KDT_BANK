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
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import com.example.bankservice1.model.*;


import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        contentArea.setAlignment(Pos.CENTER);
        logoutBtn.setOnAction((event) -> Logout());

        menu.setVisible(false);
        employeeSearch.setVisible(false);
        if(UserSession.getInstance().getAdmin()) {
            menu.setVisible(true);
            employeeSearch.setVisible(true);
        }

    }

    public void setupAfterLogin() {
        System.out.println("MainViewController: 로그인 후 설정을 시작합니다.");
        loadInitialUnreadCount();
        subscribeToGlobalNotifications();
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

    private void subscribeToGlobalNotifications() {
        StompSession session = WebSocketManager.getInstance().getSession();
        if (session == null || !session.isConnected()) {
            System.err.println("알림을 구독할 수 없습니다. 웹소켓이 연결되지 않았습니다.");
            return;
        }
        session.subscribe("/topic/notify", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return NotificationPayload.class;
            }
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(">>>>>>>>>> [SUCCESS] MESSAGE RECEIVED ON /topic/notify! <<<<<<<<<<");
                Platform.runLater(() -> {
                    System.out.println("🔔 [MainView] 새로운 실시간 알림 수신!");
                    unreadCount.set(unreadCount.get() + 1);
                });
            }
        });
        System.out.println("📢 [MainView] '/topic/notify' 알림 채널 구독 완료.");
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

                                System.out.println(URC);

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