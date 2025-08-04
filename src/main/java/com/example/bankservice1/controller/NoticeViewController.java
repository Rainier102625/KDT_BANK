package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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

    private List<Notice> allNotices = new ArrayList<>();

    private static final int ITEMS_PER_PAGE = 10;

    // NoticeListCell과 동일한 셀 높이 값을 상수로 정의
    private static final int FIXED_CELL_HEIGHT = 40;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/notices"))// 로그인 API 주소
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
                                String responseBody = response.body();

                                Gson gson = new Gson();

                                // JSON 배열을 Notice 객체 배열로 한 번에 파싱
                                Type noticeListType = new TypeToken<ArrayList<Notice>>() {
                                }.getType();

                                noticeListView.setCellFactory(param -> new NoticeListCell());
                                // 2. 정의한 타입으로 JSON을 파싱하여 List<Notice>를 직접 얻음
                                allNotices = gson.fromJson(responseBody, noticeListType);



                                setupPagination();

                                showPage(1);
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
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "로그인 요청 중 오류가 발생했습니다.");
        }

        noticeListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        // 선택된 항목(newSelection)으로 팝업을 띄웁니다.
                        showNoticePopup(newSelection);
                    }
                }
        );
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * 페이지네이션 버튼들을 동적으로 생성하고 각 버튼에 이벤트 핸들러를 설정합니다.
     */
    private void setupPagination() {
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

    /**
     * 특정 페이지에 해당하는 데이터만 ListView에 표시합니다.
     *
     * @param pageNumber 표시할 페이지 번호
     */
    private void showPage(int pageNumber) {
        // 페이지 번호에 맞는 데이터 범위를 계산
        int fromIndex = (pageNumber - 1) * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allNotices.size());

        // 전체 리스트에서 현재 페이지에 해당하는 부분만 잘라내기
        List<Notice> pageData = allNotices.subList(fromIndex, toIndex);

        // ListView의 아이템들을 현재 페이지 데이터로 교체
        noticeListView.setItems(FXCollections.observableArrayList(pageData));
    }

    /**
     * FXML을 로드하여 새로운 팝업 창(Stage)을 띄우는 메소드
     *
     * @param notice 팝업에 표시할 공지사항 데이터
     */
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

}