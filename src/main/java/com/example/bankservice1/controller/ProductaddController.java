package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.UserSession;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class ProductaddController {
    @FXML private TextField productname;
    @FXML private TextField productduration;
    @FXML private TextField productrate;
    @FXML private ComboBox<String> typeCombo;
    @FXML private Button complete;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    @FXML
    private void initialize() {
        complete.setOnAction(event -> handleAddProduct());
    }
    public void handleAddProduct() {
        if (UserSession.getInstance().getAdmin()) {
            try {
                String productName = productname.getText();
                Integer duration = null;
                String durationText = productduration.getText();
                String rateText = productrate.getText();
                String type = typeCombo.getValue();

                if(!durationText.isEmpty()) {
                    duration = Integer.parseInt(durationText);
                }
                if (productName.isEmpty() || type == null || rateText.isEmpty()) {
                    return;
                }
                double rate = Double.parseDouble(rateText);
                String requestBody = String.format("""
                        {
                        "productName": "%s",
                        "type": "%s",
                        "rate": %f,
                        "duration": %d
                        }
                        """, productName, type, rate, duration); //double일 경우 %f, int일 경우 %d

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiconstants.BASE_URL + "/products"))
                        .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            if (response.statusCode() == 200) {
                                System.out.println("success");
                            }else if (response.statusCode() == 400){
                                Platform.runLater(() -> showAlert("이미 존재하는 상품 이름입니다"));
                            }
                            else {
                                System.out.println("fail" + response.statusCode());
                            }
                        })
                        .exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
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
