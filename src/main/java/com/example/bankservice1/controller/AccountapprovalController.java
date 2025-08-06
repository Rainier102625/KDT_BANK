package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Account;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    @FXML
    private void initialize() {
        handleSearchAccount();
    }
    private void handleSearchAccount() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL+"/accounts/pending"))
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
                            List<Account> filterList = query.isEmpty() ? accountList :
                                    accountList.stream()
                                            .filter(account -> account.getCustomerName().contains(query))
                                            .toList();
                            Platform.runLater(() -> {
                                requestListContainer.getChildren().clear();

                                for(Account account : filterList){
                                    String status;
                                    switch (account.getAccountStatus()) {
                                        case "PENDING" -> status = "생성";
                                        case "DELETE_PENDING" -> status ="삭제";
                                        default -> status = "알 수 없음";
                                    }
                                    addRequestCard(
                                            account.getCustomerName(),
                                            account.getCustomerBirth(),
                                            account.getCustomerBirth(),
                                            account.getProductName(),
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

    private void addRequestCard(String name, String customerBirth, String accountNum, String productName, String accountStatus) {
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
                new Label(name),
                new Label(customerBirth)
        );
        VBox accountBox = new VBox(3,
                new Label("계좌") {{ setStyle("-fx-font-size: 12px; -fx-text-fill: #999;"); }},
                new Label(accountNum),
                new Label(productName)
                );
        Label statusLabel = new Label(accountStatus);
        statusLabel.setStyle("-fx-background-color: #fceabb; -fx-padding: 2 8; -fx-background-radius: 8;");

        VBox statusBox = new VBox(3,
                new Label("요청") {{ setStyle("-fx-font-size: 12px; -fx-text-fill: #999;"); }},
                new HBox(statusLabel)
                );
        Button approve = new Button("승인");
        approve.setStyle("-fx-background-color: #3bb273; -fx-text-fill: white;");
        Button reject = new Button("거절");
        reject.setStyle("-fx-background-color: #f87171; -fx-text-fill: white;");
        HBox buttonBox = new HBox(10, approve, reject);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(imageView, customerBox, accountBox, statusBox, buttonBox);
        requestListContainer.getChildren().add(card);
    }

}
