package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.io.IOException;

import com.example.bankservice1.model.*;

public class loginController {
    @FXML
    private TextField idField;

    @FXML
    private TextField pwField;

    @FXML
    private Button loginButton;

    @FXML
    private Button signUpButton;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String userId = idField.getText();
        String userPw = pwField.getText();

        LoginDTO loginRequest = new LoginDTO(userId, userPw);

        System.out.println("로그인 시도: ID = "  + userId + ", PW = " + userPw);
        
        try {
            //http에 담을 데이터 json으로 변환
            String requestBody = objectMapper.writeValueAsString(loginRequest);
            System.out.println(requestBody);
            
            //http 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/auth/login"))// 로그인 API 주소
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 비동기로 서버에 요청 전송
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                // 로그인 성공 처리 (예: 메인 화면으로 전환)
                                // 응답 본문(JSON 문자열) 가져오기
                                String responseBody = response.body();

                                // Gson 객체 생성
                                Gson gson = new Gson();

                                // JSON을 User 객체로 파싱
                                User user = gson.fromJson(responseBody, User.class);

                                System.out.println(user);

                                // 파싱된 객체에서 데이터 추출
                                String token = user.getJwtToken();
                                String userName = user.getUserName();
                                boolean admin = user.getAdmin();

                                // 각 싱글톤에 데이터 저장
                                tokenManager.getInstance().setJwtToken(token);
                                UserSession.getInstance().setUserName(userName);
                                UserSession.getInstance().setAdmin(admin);
                                System.out.println("로그인 성공: " + responseBody);
                                showAlert(Alert.AlertType.INFORMATION, "성공", "로그인에 성공했습니다.");
                                // 여기서 화면 전환 로직 호출
                                loadMainView();
                            }
                            else if (response.statusCode() == 400) {
                                System.out.println("로그인 실패");
                                showAlert(Alert.AlertType.ERROR, "실패", "아이디 또는 비밀번호가 올바르지 않습니다.");
                            }
                            else {
                                // 로그인 실패 처리
                                System.out.println("응답 실패");
                                showAlert(Alert.AlertType.ERROR, "실패", "잘못된 요청");
                            }
                        });
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "로그인 요청 중 오류가 발생했습니다.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/MainView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("XiliBank");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "오류", "메인 화면을 불러오는 데 실패했습니다.");
        }
    }

//    private void showAlert(String msg) {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("알림");
//        alert.setHeaderText(null);
//        alert.setContentText(msg);
//        alert.showAndWait();
//    }

    @FXML
    protected void handleSignUpButtonAction(ActionEvent event) {
        System.out.println("회원가입 화면으로 이동합니다.");
        // 여기에 화면 전환 로직을 구현합니다.
        try {
            // 1. 새로 로드할 FXML 파일의 경로를 지정합니다.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/Signup.fxml"));

            // 2. FXML 파일을 로드하여 새로운 화면(Parent 객체)을 생성합니다.
            Parent root = loader.load();

            // 3. 현재 창(Stage)을 가져옵니다.
            //    (이벤트가 발생한 컨트롤로부터 Scene과 Window를 거슬러 올라가 Stage를 찾습니다.)
            Stage stage = (Stage) signUpButton.getScene().getWindow();

            // 4. 새로운 화면으로 Scene을 생성합니다.
            Scene scene = new Scene(root);

            // 5. 현재 Stage에 새로운 Scene을 설정하여 화면을 전환합니다.
            stage.setScene(scene);
            stage.setTitle("회원가입"); // 창 제목을 변경할 수도 있습니다.
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}