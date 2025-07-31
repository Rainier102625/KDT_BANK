package com.example.bankservice1.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;


public class SignupController {
    @FXML private TextField nameField;
    @FXML private TextField idField;
    @FXML private TextField pwField;
    @FXML private TextField pnumField;
    @FXML private TextField birthField;
    @FXML private TextField departField;
    @FXML private TextField rankField;
    private boolean sendSignupData(String id, String pw, String name, String pnum, String birth, String depart, String rank) {
        try {
            URL url = new URL("http://100.100.101.56:8080/api/auth/register");
            HttpURLConnection conn =(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = String.format(
                    "{\"id\":\"%s\",\"pw\":\"%s\",\"name\":\"%s\",\"pnum\":\"%s\",\"birth\":\"%s\"depart\":\"%s\",\"rank\":\"%s\"}",
                    id, pw, name, pnum, birth, depart, rank
            );
            try (OutputStream os = conn.getOutputStream()){
                os.write(json.getBytes("UTF-8"));
            }
            int responseCode = conn.getResponseCode();
            return responseCode == 200 | responseCode == 201;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleSignupButton() {
        String name = nameField.getText();
        String id = idField.getText();
        String pw = pwField.getText();
        String pnum = pnumField.getText();
        String birth = birthField.getText();
        String depart = departField.getText();
        String rank = rankField.getText();

        if(name.trim().isEmpty()) {
            showAlert("이름을 입력하세요");
            return;
        }
        if(id.trim().isEmpty()) {
            showAlert("아이디를 입력하세요");
            return;
        }
        if(pw.trim().isEmpty()) {
            showAlert("비밀번호를 입력하세요");
            return;
        }
        if(pnum.trim().isEmpty()) {
            showAlert("전화번호를 입력하세요");
            return;
        }
        if(birth.trim().isEmpty()) {
            showAlert("주민등록번호를 입력하세요");
            return;
        }
        boolean success = sendSignupData(id, pw, name, pnum, birth, depart, rank);
        if(success) {
            showAlert("회원가입 성공");
            moveLogin();
        } else {
            showAlert("회원가입 실패");
        }

    }
    @FXML
    private void handelsignin(){
        moveLogin();
    }
    private void moveLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/Login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) nameField.getScene().getWindow(); //현재 화면 가져오기(namefield가 있는 화면)
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("로그인 화면");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
