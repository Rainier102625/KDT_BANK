package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Employee;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class mypageController {
    @FXML private Label name;
    @FXML private Label department;
    @FXML private Label position;
    @FXML private Label userName;
    @FXML private Label userPhone;
    @FXML private Label userDepartment;
    @FXML private Label userPosition;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    @FXML
    private void initialize() {
        handleme();
    }
    private void handleme() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/users/me"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();
        httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            Employee employee = objectMapper.readValue(response.body(), Employee.class);
                            javafx.application.Platform.runLater(()->{
                                name.setText(employee.getUserName());
                                department.setText(employee.getDepartment());
                                position.setText(employee.getPosition());
                                userName.setText(employee.getUserName());
                                userPhone.setText(employee.getUserPhone());
                                userDepartment.setText(employee.getDepartment());
                                userPosition.setText(employee.getPosition());
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("실패" + response.statusCode());
                        System.out.println(response.body());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}
