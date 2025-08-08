package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.NotificationSet;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NotificationPanelController {

    @FXML
    private ListView<NotificationSet> notificationListView;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObservableList<NotificationSet> notificationList = FXCollections.observableArrayList();
    MainViewController mainViewController;

    @FXML
    public void initialize() {
        notificationListView.setItems(notificationList);
        setupNotificationCellFactory();
        loadAndDisplayNotifications();
    }

    private void loadAndDisplayNotifications() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/notifications"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            String responseBody = response.body();
                            final List<NotificationSet> fetchedNotifications = objectMapper.readValue(responseBody, new TypeReference<>() {});
                            Platform.runLater(() -> {
                                notificationList.setAll(fetchedNotifications);
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("알림 목록 로드 실패: " + response.statusCode());
                    }
                });

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
}