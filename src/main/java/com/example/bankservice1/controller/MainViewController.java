package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;


import javafx.scene.image.ImageView;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    @FXML private Button account;

    @FXML private Label unreadCountBadge;

    @FXML private StackPane rootStackPane;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final LongProperty unreadCount = new SimpleLongProperty(0);

    @FXML private Stage notificationStage;

    @FXML private ImageView bellIcon;

    private StompSession.Subscription globalNotificationSubscription;

    @FXML private ListView<NotificationSet> notificationListView;
    private final ObservableList<NotificationSet> notificationList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        unreadCount.addListener((obs, oldVal, newVal) -> {
            unreadCountBadge.setText(String.valueOf(newVal)); // 라벨 텍스트 변경
            unreadCountBadge.setVisible(true);                 // 라벨 보이기

        });

        // initialize() 메소드가 실행되자마자 공지사항 화면을 로드하는 메소드를 호출합니다.
        showNoticeView();

        String userName = UserSession.getInstance().getUserName();
        name.setText(userName);
        contentArea.setAlignment(Pos.CENTER);
        logoutBtn.setOnAction((event) -> Logout());

        menu.setVisible(false);
        employeeSearch.setVisible(false);
        account.setVisible(false);
        if(UserSession.getInstance().getAdmin()) {
            menu.setVisible(true);
            employeeSearch.setVisible(true);
            account.setVisible(true);
        }
    }

    private void setupNotificationCellFactory() {
        notificationListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(NotificationSet item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(5);
                    Label contentLabel = new Label();
                    Label timestampLabel = new Label();
                    contentLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
                    timestampLabel.setStyle("-fx-text-fill: #888888;");
                    vbox.getChildren().addAll(contentLabel, timestampLabel);
                    contentLabel.setText(item.getMessage());

                    String originalDateTime = String.valueOf(item.getCreatedAt());
                    if (originalDateTime != null && originalDateTime.length() >= 16) {
                        String simplifiedDateTime = originalDateTime.replace('T', ' ').substring(0, 16);
                        timestampLabel.setText(simplifiedDateTime);
                    }
                    setGraphic(vbox);
                }
            }
        });
    }


    /**
     * 🔔 종 아이콘 컨테이너 클릭 이벤트 핸들러
     */
    @FXML
    public void handleBellButtonClick() {

        if (notificationStage != null && notificationStage.isShowing()) {
            notificationStage.toFront(); // 이미 열려있으면 맨 앞으로 가져옵니다.
            return;
        }
        try {
            // 1. FXML을 로드하는 것은 동일합니다.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/NotificationPanel.fxml"));
            Parent notificationRoot = loader.load();

            // 2. 새 창(Stage)을 만듭니다.
            notificationStage = new Stage();
            notificationStage.setTitle("알림 목록");
            notificationStage.setScene(new Scene(notificationRoot));

            // 3. (선택사항) 창 스타일 및 주인 창 설정
            // notificationStage.initModality(Modality.WINDOW_MODAL); // 이 창을 닫아야 다른 창을 쓸 수 있음
            // notificationStage.initOwner(rootStackPane.getScene().getWindow()); // 메인 창을 주인으로 설정

            // 4. 새 창을 보여줍니다.
            notificationStage.show();

            notificationStage.setOnCloseRequest((event) -> {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiconstants.BASE_URL + "/notifications/mark-read"))
                        .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            Platform.runLater(() -> {
                                if (response.statusCode() == 200) {
                                    System.out.println("지우기 성공: " + response.statusCode());
                                    unreadCount.set(0);

                                } else {
                                    System.out.println("지우기 실패: " + response.statusCode());
                                }
                            });
                        });
            });


        } catch (IOException e) {
            System.err.println("알림창을 여는 중 오류 발생!");
            e.printStackTrace();
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
        if (globalNotificationSubscription != null) {
            globalNotificationSubscription.unsubscribe();
            System.out.println("📢 [MainView] 기존 글로벌 알림 구독을 해지합니다.");
        }
        StompSession session = WebSocketManager.getInstance().getSession();
        if (session == null || !session.isConnected()) {
            System.err.println("알림을 구독할 수 없습니다. 웹소켓이 연결되지 않았습니다.");
            return;
        }
        session.subscribe("/topic/notify/"+UserSession.getInstance().getUserIndex(), new StompFrameHandler() {
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
                    Platform.runLater(()->{
                        BellImageAnime(bellIcon);
                    });
                });
            }
        });
        System.out.println("📢 [MainView] '/topic/notify' 알림 채널 구독 완료.");
    }

    @FXML

    public void Logout(){
        tokenManager.getInstance().clearSession();
        UserSession.getInstance().clearLogin();
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

                                System.out.println("최초 안 읽은 알림 개수(" + initialCount + "개)를 로드했습니다.");

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


    private void BellImageAnime(ImageView imageView){
        double oneWayDuration = 0.5; // 편도 0.5초
        double cycleDuration = oneWayDuration * 2; // 왕복 1.0초

        // 2. 총 실행 시간을 정합니다.
        double totalDuration = 4; // 총 10초

        // 3. 총 실행 시간에 필요한 반복 횟수를 계산합니다.
        int cycleCount = (int) (totalDuration / cycleDuration);

        // 타임라인 생성
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(imageView.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(oneWayDuration), new KeyValue(imageView.opacityProperty(), 0.0))
        );

        // 4. 무한 반복 대신 계산된 횟수를 설정합니다.
        timeline.setCycleCount(cycleCount);
        timeline.setAutoReverse(true);

        timeline.setOnFinished(event -> {
            // 이미지의 투명도를 원래 상태(1.0, 완전 불투명)로 복구합니다.
            imageView.setOpacity(1.0);
        });

        timeline.play();
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}