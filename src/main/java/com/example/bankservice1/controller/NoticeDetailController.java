package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.*;
import com.example.bankservice1.model.Notice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NoticeDetailController{

    @FXML private TextField titleTextField;

    @FXML private Label dateLabel;

    @FXML private TextArea contentTextArea;

    @FXML private MenuButton noticeMenuBox;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public MenuButton getNoticeMenuBox() {
        return noticeMenuBox;
    }

    public void setNoticeMenuBox(MenuButton noticeMenuBox) {
        this.noticeMenuBox = noticeMenuBox;
    }

    public NoticeDetailController(){}


    // Notice 객체를 받아와 UI에 데이터를 설정하는 메소드
    public void setNotice(Notice notice) {
        titleTextField.setText(notice.getNoticeTitle());
        dateLabel.setText("날짜 "+notice.getCreatedAt());
        contentTextArea.setText(notice.getNoticeContent());

        titleTextField.setEditable(false);
        contentTextArea.setEditable(false);


        if(UserSession.getInstance().getAdmin()) {
            titleTextField.setEditable(true);
            contentTextArea.setEditable(true);
        }
    }

    public void setMenuButton(Notice notice){

        noticeMenuBox.setVisible(false);
        noticeMenuBox.setManaged(false);

        if(UserSession.getInstance().getAdmin()) {
            noticeMenuBox.getItems().clear();
            noticeMenuBox.setVisible(true);
            noticeMenuBox.setManaged(true);

            MenuItem modifyItem = new MenuItem("수정");
            MenuItem deleteItem = new MenuItem("삭제");

            // 2. 각 메뉴 아이템에 클릭 이벤트 설정
            modifyItem.setOnAction(event -> handleModify(notice));
            deleteItem.setOnAction(event -> handleDelete(notice));
//        reportItem.setOnAction(event -> handleReport());

            // 3. MenuButton에 메뉴 아이템들 추가
            noticeMenuBox.getItems().addAll(modifyItem, deleteItem);
        }
    }

    private void handleModify(Notice notice) {
        if(UserSession.getInstance().getAdmin()) {
            try {
                NoticeDTO noticeDTO = new NoticeDTO(notice.getNoticeTitle(), notice.getNoticeContent());
                System.out.println(noticeDTO);
                System.out.println("수정 메뉴 클릭됨");
                //http에 담을 데이터 json으로 변환
                String requestBody = objectMapper.writeValueAsString(noticeDTO);
                System.out.println(requestBody);
                //http 요청 생성
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiconstants.BASE_URL + "/notices/" + Integer.toString(notice.getNoticeIndex())))
                        .header("Content-Type", "application/json")// 로그인 API 주소
                        .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                        .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();
                System.out.println(request.toString());
                // 비동기로 서버에 요청 전송
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            Platform.runLater(() -> {
                                if (response.statusCode() == 200) {
                                    System.out.println("수정 성공");
                                    showAlert("수정 완료");

                                } else if (response.statusCode() == 400) {
                                    System.out.println("수정 실패");
                                    showAlert(Alert.AlertType.ERROR, "실패", "사용자가 아닙니다.");
                                } else {
                                    System.out.println("잘못된 접근");
                                    showAlert(Alert.AlertType.ERROR, "실패", "잘못된 요청");
                                }
                            });
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleDelete(Notice notice){
        if(UserSession.getInstance().getAdmin()) {
            try {

                System.out.println("삭제 클릭됨");
                //http 요청 생성
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiconstants.BASE_URL + "/notices/" + Integer.toString(notice.getNoticeIndex())))//
                        .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                        .DELETE()
                        .build();

                // 비동기로 서버에 요청 전송
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            Platform.runLater(() -> {
                                if (response.statusCode() == 200) {
                                    System.out.println("삭제 성공");
                                    showAlert("삭제 완료");

                                } else if (response.statusCode() == 400) {
                                    System.out.println("수정 실패");
                                    showAlert(Alert.AlertType.ERROR, "실패", "사용자가 아닙니다.");
                                } else {
                                    System.out.println("잘못된 접근");
                                    showAlert(Alert.AlertType.ERROR, "실패", "잘못된 요청");
                                }
                            });
                        });
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "오류", "로그인 요청 중 오류가 발생했습니다.");
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

    private void showAlert(String message) {
        showAlert(Alert.AlertType.WARNING,"알림",message);
    }


}
