package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Friend;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class friendaddController {
    @FXML private TableView<Friend> friendTable;
    @FXML private TableColumn<Friend,String> userName;
    @FXML private TableColumn<Friend,String> department;
    @FXML private TableColumn<Friend,String> position;
    @FXML private TableColumn<Friend, Void> state;
    @FXML private Button search;
    @FXML private TextField searchText;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize(){
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));

        handleSearch(friendTable);
        search.setOnAction(event -> handleSearch(friendTable));

        state.setCellFactory(column -> new TableCell<>() {
            private final Button moreButton = new Button("⋮");
            {
                moreButton.setStyle("-fx-background-color: white; -fx-font-size: 12px; -fx-text-fill: black;");

                MenuItem addItem = new MenuItem("추가");
                MenuItem exitItem = new MenuItem("취소");
                exitItem.setStyle("-fx-text-fill: red;");

                ContextMenu contextMenu = new ContextMenu(addItem, exitItem);

                addItem.setOnAction(event -> {
                    Friend selectedFriend = getTableView().getItems().get(getIndex());
                    int userIndex = selectedFriend.getUserIndex();
                    friendrequest(userIndex);
                });

                moreButton.setOnAction(e -> {
                    if(!contextMenu.isShowing()) {
                        contextMenu.show(moreButton, Side.BOTTOM, 0, 0);
                    } else {
                        contextMenu.hide();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if(empty) {
                    setGraphic(null);
                } else {
                    setGraphic(moreButton);
                }
            }
        });

    }
    public void handleSearch(TableView<Friend> friendTable) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/users"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode()==200) {
                        try{
                            String responseBody = response.body(); //응답 받아서 string으로 변환
                            Friend[] friendArray = objectMapper.readValue(responseBody, Friend[].class); //위 내용을 friend 배열에 저장
                            List<Friend> friendList = Arrays.asList(friendArray); //배열을 리스트로 변환

                            String query = searchText.getText().trim();
                            List<Friend> filterList = query.isEmpty() ? friendList : //검색어가 비어있을 경우 전체 friendList 출력
                                    friendList.stream() //friendlist stream 생성
                                            .filter(friend -> friend.getUserName().contains(query))
                                            .toList(); //리스트로 반환

                            Platform.runLater(() -> { //UI 스레드를 사용해서 friendtable 값을 friendlist에 있는 값으로 세팅
                                friendTable.setItems(FXCollections.observableList(filterList));
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace(); //예외 발생 위치, 호출 경로 출력
                        }
                    } else{
                        System.out.println("서버 오류" + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
    private void friendrequest(int userIndex){
        try {
            String requestBody = String.format("{\"userIndex\": %d}", userIndex);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/friends"))
                    .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        System.out.println("응답 코드: " + response.statusCode());
                        System.out.println("응답 바디: " + response.body());
                        if(response.statusCode()==200) {
                            System.out.println("success");
                        } else {
                            System.out.println("fail");
                        }
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
