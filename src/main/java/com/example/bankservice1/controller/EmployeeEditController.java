package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Employee;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class EmployeeEditController {
    @FXML private Label userName;
    @FXML private TextField userPhone;
    @FXML private TextField department;
    @FXML private TextField position;
    @FXML private CheckBox admin;
    @FXML private Button edit;
    @FXML private Button cancel;

    private int userIndex; // 조회할 유저의 index

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize(){
        edit.setOnAction((e) -> handleAdminedit());
        cancel.setOnAction((e) -> handleCancel());
    }
    public void setUserIndex(int userIndex) {
        this.userIndex = userIndex;
        System.out.println("userIndex = " + userIndex);
        handleEmployeeEdit();
    }
    private void handleEmployeeEdit(){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/users/"+userIndex))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();
        httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200){
                        try {
                            Employee employee = objectMapper.readValue(response.body(), Employee.class);
                            javafx.application.Platform.runLater(()->{
                                userName.setText(employee.getUserName());
                                userPhone.setText(employee.getUserPhone());
                                department.setText(employee.getDepartment());
                                position.setText(employee.getPosition());
                                admin.setSelected(employee.getAdmin());
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {
                        System.out.println("정보 불러오기 실패"+response.statusCode());
                        System.out.println("dd " + response.body());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
    private  void handleAdminedit() {
        try {
            String phoneNumber = userPhone.getText();
            String Department = department.getText();
            String rank = position.getText();
            boolean isadmin = admin.isSelected();

            String requestBody = String.format("""
                    {
                    "phoneNumber": "%s",
                    "department": "%s",
                    "position": "%s",
                    "admin": %b
                    }
                    """, phoneNumber, Department, rank, isadmin);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/users/" + userIndex))
                    .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString()) //requestBody를 Http 요청 본문에 포함
                    .thenAccept(response -> {
                        if(response.statusCode() == 200){
                            System.out.println("success");
                            javafx.application.Platform.runLater(()->showAlert("수정 완료"));
                        } else{
                            System.out.println("fail" + response.statusCode());
                            System.out.println(response.body());
                        }
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleCancel(){
        Stage stage =  (Stage) cancel.getScene().getWindow();
        stage.close();
    }
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
