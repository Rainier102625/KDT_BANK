package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.ChatMemberAdd;
import com.example.bankservice1.model.ChatRoom;
import com.example.bankservice1.model.Friend;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class friendinviteController {
    @FXML private TableView<Friend> friendTable;
    @FXML private TableColumn<Friend,String> userName;
    @FXML private TableColumn<Friend,String> department;
    @FXML private TableColumn<Friend,String> position;

    @FXML private Button chatmemberadd;
    private List<Friend> friendsList = new ArrayList<>();

    private ObservableList<Friend> friendObservableList;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Long currentChatIndex;

    private ChatViewController chatViewController;

    public void setChatViewController(ChatViewController chatViewController){
        this.chatViewController = chatViewController;
    }

    @FXML
    public void initialize() {

        friendObservableList = FXCollections.observableArrayList();
        friendTable.setItems(friendObservableList);


        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));


        friendTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        chatmemberadd.setOnAction(event -> {
            handleAddSelectionFriend();
        });
    }
    public void initData(List<Friend> friends) {
        if (friends != null) {
            friendObservableList.setAll(friends);
            System.out.println("전달받은 친구 목록 개수: " + friends.size());
        }
    }

    public void loadChatIndex(long currentChatIndex){
        this.currentChatIndex = currentChatIndex;
        System.out.println("전달받은 채팅방 인덱스: " + this.currentChatIndex);
    }

    @FXML
    private void handleAddSelectionFriend(){
        ObservableList<Friend> friendObservableList = friendTable.getSelectionModel().getSelectedItems();

        if (friendObservableList.isEmpty()) {
            showAlert("추가할 멤버를 선택하세요");
            return;
        }

        List<Long> indexList = new ArrayList<>();

        for(Friend friend : friendObservableList){
            indexList.add((long) friend.getUserIndex());
        }

        ChatMemberAdd chatMemberAdd = new ChatMemberAdd(this.currentChatIndex,indexList);
        try {
            String requestBody = objectMapper.writeValueAsString(chatMemberAdd);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/chat/member"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                System.out.println("멤버요청 성공: " + response.statusCode());
                            } else {
                                System.out.println("멤버요청 실패: " + response.statusCode());
                            }
                        });
                    });
        }catch (Exception e){
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
