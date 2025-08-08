package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Account;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;

public class AccountapprovalController {
    @FXML private VBox requestListContainer;
    @FXML private TextField SearchName;
    @FXML private ComboBox<String> accountRequest;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private void initialize() {
        handleSearchAccount();
    }
    private void handleSearchAccount() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL+"/account-request/pending"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();
        httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try{
                            String responseBody = response.body();
                            Account[] accountArray = objectMapper.readValue(responseBody, Account[].class);
                            List<Account> accountList = Arrays.asList(accountArray);

                            String query = SearchName.getText();
                            String query2 = accountRequest.getValue();
                            List<Account> filterList = query.isEmpty() ? accountList :
                                    accountList.stream()
                                            .filter(account -> account.getCustomerName().contains(query) && account.getRequestType().equals(query2))
                                            .toList();
                            Platform.runLater(() -> {
                                requestListContainer.getChildren().clear();

                                for(Account account : filterList){
                                    String status;
                                    switch (account.getRequestType()) {
                                        case "CREATE" -> status = "생성";
                                        case "DELETE" -> status ="삭제";
                                        default -> status = "알 수 없음";
                                    }
                                    addRequestCard(
                                            account,
                                            status
                                    );
                                }
                            });
                        }catch (Exception ex) {
                            ex.printStackTrace(); //예외 발생 위치, 호출 경로 출력
                        }
                    } else{
                        System.out.println(response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
    private void handleApprove(int requestIndex) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL+"/account-request/"+requestIndex + "/approve"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .POST(HttpRequest.BodyPublishers.noBody()) //요청 본문 없음
                .build();
        httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        System.out.println("success");
                        Platform.runLater(() -> this.handleSearchAccount());
                    }else {
                        System.out.println("fail");
                        System.out.println(response.statusCode());
                        System.out.println(requestIndex);
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void handleReject(int requestIndex) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL+"/account-request/"+requestIndex + "/reject"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .POST(HttpRequest.BodyPublishers.noBody()) //요청 본문 없음
                .build();
        httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        System.out.println("success");
                        Platform.runLater(() -> this.handleSearchAccount());
                        Platform.runLater(() -> showAlert("완료"));
                    }else {
                        System.out.println("fail");
                        System.out.println(response.statusCode());
                        System.out.println(requestIndex);
                        Platform.runLater(() -> showAlert("실패"));
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
    //요청 카드 디자인
    private void addRequestCard(Account account, String status) {
        HBox card = new HBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 5; -fx-border-color: #eeeeee; -fx-border-radius: 10;");
        card.setPrefWidth(560);
        card.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView(new Image(getClass().getResource("/icons/profile.png").toExternalForm()));
        imageView.setFitWidth(39);
        imageView.setFitHeight(39);
        imageView.setPreserveRatio(true);

        VBox customerBox = new VBox(3,
                new Label("고객정보") {{ setStyle("-fx-font-size: 12px; -fx-text-fill: #999;"); }},
                new Label(account.getCustomerName()),
                new Label(account.getCustomerBirth())
        );
        VBox accountBox = new VBox(3,
                new Label("계좌") {{ setStyle("-fx-font-size: 12px; -fx-text-fill: #999;"); }},
                new Label(account.getAccountNum()),
                new Label(account.getProductName())
        );
        Label statusLabel = new Label(status);
        statusLabel.setStyle("-fx-background-color: #fceabb; -fx-padding: 2 8; -fx-background-radius: 8;");

        VBox statusBox = new VBox(3,
                new Label("요청") {{ setStyle("-fx-font-size: 12px; -fx-text-fill: #999;"); }},
                new HBox(statusLabel)
        );
        Button approve = new Button("승인");
        approve.setStyle("-fx-background-color: #3bb273; -fx-text-fill: white;");
        approve.setOnAction(event -> handleApprove(account.getRequestIndex()));
        Button reject = new Button("거절");
        reject.setStyle("-fx-background-color: #f87171; -fx-text-fill: white;");
        reject.setOnAction(event -> handleReject(account.getRequestIndex()));
        HBox buttonBox = new HBox(10, approve, reject);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(imageView, customerBox, accountBox, statusBox, buttonBox);
        requestListContainer.getChildren().add(card);
    }
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
