package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;

import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.lang.reflect.Type;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.*;

import com.google.gson.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;

public class NoticeViewController implements Initializable {



//    String requestBody = objectMapper.writeValueAsString(loginRequest);
//    HttpRequest request = HttpRequest.newBuilder()
//            .uri(URI.create(apiconstants.BASE_URL + "/api/auth/login"))// 로그인 API 주소
//            .header("Content-Type", "application/json")
//            .header("Authorization", "Bearer "+ jwtToken)
//            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//            .build();

    @FXML
    private ListView<Notice> noticeListView;
    @FXML
    private HBox paginationBox;

    @FXML
    private Button createNoticeBtn;

    private List<Notice> allNotices = new ArrayList<>();

    private static final int ITEMS_PER_PAGE = 10;

    // NoticeListCell과 동일한 셀 높이 값을 상수로 정의
    private static final int FIXED_CELL_HEIGHT = 40;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObservableList<Notice> displayedNotices = FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        noticeListView.setItems(displayedNotices);
        noticeListView.setCellFactory(param -> new NoticeListCell());


        createNoticeBtn.setVisible(false);
        if(UserSession.getInstance().getAdmin()) {
            createNoticeBtn.setVisible(true);
        }

        noticeListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        // 선택된 항목(newSelection)으로 팝업을 띄웁니다.
                        showNoticePopup(newSelection);
                    }
                }
        );
        createNoticeBtn.setOnAction(e -> showCreateNoticePopup());

        loadAllNoticesFromServer();
    }
    // 버튼 생성
    private void setupPagination() {
        //페이지 버튼을 다시 만들기 전에 기존 버튼 제거
        paginationBox.getChildren().clear();
        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) allNotices.size() / ITEMS_PER_PAGE);

        // 페이지 버튼 생성
        for (int i = 1; i <= totalPages; i++) {
            final int pageNumber = i;
            Button pageButton = new Button(String.valueOf(pageNumber));
            pageButton.setOnAction(event -> showPage(pageNumber)); // 버튼 클릭 시 해당 페이지 보여주기
            paginationBox.getChildren().add(pageButton);
        }
    }
    //특정 페이지에 해당하는 데이터만 ListView에 표시합니다.
    private void showPage(int pageNumber) {
        // 페이지 번호에 맞는 데이터 범위를 계산
        int fromIndex = (pageNumber - 1) * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allNotices.size());

        // 전체 리스트에서 현재 페이지에 해당하는 부분만 잘라내기
        List<Notice> pageData = allNotices.subList(fromIndex, toIndex);

        // ListView의 아이템들을 현재 페이지 데이터로 교체
        displayedNotices.setAll(pageData);
    }
    // 공지 데이터 불러오기
    public void loadAllNoticesFromServer() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/notices"))
                    .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            String responseBody = response.body();
                            Gson gson = new Gson();
                            Type noticeListType = new TypeToken<ArrayList<Notice>>() {}.getType();
                            allNotices = gson.fromJson(responseBody, noticeListType);

                            Collections.sort(allNotices, Collections.reverseOrder());

                            // 3. 데이터를 성공적으로 받아온 후, UI를 업데이트하는 부분만 Platform.runLater로 감쌉니다.
                            Platform.runLater(() -> {
                                setupPagination();
                                showPage(1); // 첫 페이지 보여주기
                            });

                        } else {
                            Platform.runLater(() -> {
                                System.out.println("공지사항 불러오기 실패: " + response.statusCode());
                                showAlert(Alert.AlertType.ERROR, "오류", "공지사항을 불러오는 데 실패했습니다.");
                            });
                        }
                    })
                    .exceptionally(e -> { // 네트워크 오류 등 예외 처리
                        e.printStackTrace();
                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "오류", "서버 연결 중 오류가 발생했습니다."));
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "요청 생성 중 오류가 발생했습니다.");
        }
    }
    //팝업창 로드
    private void showNoticePopup(Notice notice) {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/notices/" + notice.getNoticeIndex()))// 로그인 API 주소
                    .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                    .GET()
                    .build();

            // 비동기로 서버에 요청 전송
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                // 로그인 성공 처리 (예: 메인 화면으로 전환)
                                // 1. 응답 본문(JSON 문자열) 가져오기
                                try {
                                    String responseBody = response.body();

                                    Gson gson = new Gson();

                                    // JSON 배열을 Notice 객체 배열로 한 번에 파싱
                                    Notice noticeDetail = gson.fromJson(responseBody, Notice.class);

                                    // 1. 팝업용 FXML 로드
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/NoticeDetailView.fxml"));
                                    Parent root = loader.load();


                                    // 2. 팝업창 컨트롤러를 가져와서 데이터 전달
                                    NoticeDetailController controller = loader.getController();
                                    controller.setNotice(noticeDetail);
                                    controller.setMenuButton(noticeDetail);

                                    controller.setNoticeViewController(this);
                                    // 3. 새로운 창(Stage) 생성
                                    Stage popupStage = new Stage();
                                    popupStage.setTitle("공지사항 상세 정보");
                                    popupStage.setScene(new Scene(root));

                                    // 4. 팝업창 보여주기
                                    popupStage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (response.statusCode() == 400) {
                                System.out.println("공지사항 불러오기 실패");
                                showAlert(Alert.AlertType.ERROR, "실패", "사용자가 아닙니다.");
                            } else {
                                // 로그인 실패 처리
                                System.out.println("잘못된 접근");
                                showAlert(Alert.AlertType.ERROR, "실패", "잘못된 요청");
                            }
                        });
                    });

    }
    @FXML
    private void showCreateNoticePopup() {
        createNoticeBtn.setVisible(false);
        createNoticeBtn.setManaged(false);
        if(UserSession.getInstance().getAdmin()) {
            createNoticeBtn.setVisible(true);
            createNoticeBtn.setManaged(true);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/NoticeCreateView.fxml"));
                Parent root = loader.load();

                NoticeDetailController noticeDetailController = loader.getController();

                noticeDetailController.setNoticeViewController(this);

                // 3. 새로운 창(Stage) 생성
                Stage popupStage = new Stage();
                popupStage.setTitle("공지사항 상세 정보");
                popupStage.setScene(new Scene(root));

                popupStage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
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