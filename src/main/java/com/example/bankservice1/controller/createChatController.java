package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class createChatController {
    @FXML private TableView<Friend> friendTable;
    @FXML private TableColumn<Friend,String> userName;
    @FXML private TableColumn<Friend,String> department;
    @FXML private TableColumn<Friend,String> position;
    @FXML private TextField chatNameField;


    @FXML private Button chatCreateBtn;
    @FXML private Button chatCloseBtn;


    private List<Friend> friendsList = new ArrayList<>();

    private ObservableList<Friend> friendObservableListList;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize(){

        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));

        friendObservableListList = FXCollections.observableArrayList();
        friendTable.setItems(friendObservableListList);

        friendTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //friendlist 다중 선택 가능하게 설정
    }

    public void initData(List<Friend> friends) {
        if (friends != null) {
            friendObservableListList.setAll(friends);
            System.out.println("전달받은 친구 목록 개수: " + friends.size());
        }
    }


    @FXML
    public void handleCreateButtonClick(javafx.event.ActionEvent event) {
        ObservableList<Friend> selectedFriends = friendTable.getSelectionModel().getSelectedItems();

        if (selectedFriends.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "경고", "채팅에 참여할 친구를 한 명 이상 선택해주세요");
        }

        String chatRoomName = chatNameField.getText().trim();
        if (chatRoomName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "경고", "채팅방 이름을 설정하세요");
        }

        System.out.println("채팅방 이름: " + chatRoomName);
        System.out.println("--- 선택된 친구 목록 ---");

        for (Friend friend : selectedFriends) {
            System.out.println("이름: " + friend.getUserName() + ", 부서: " + friend.getDepartment());
        }

        List<Integer> selectedFriendIds = selectedFriends.stream()
                .map(Friend::getUserIndex) // Friend 클래스에 getUserIndex()가 있다고 가정
                .collect(Collectors.toList());

        ChatCreate chatCreate = new ChatCreate(chatRoomName, selectedFriendIds);

        try {
            //http에 담을 데이터 json으로 변환
            String requestBody = objectMapper.writeValueAsString(chatCreate);
            System.out.println(requestBody);

            //http 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiconstants.BASE_URL + "/chat"))// 로그인 API 주소
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 비동기로 서버에 요청 전송
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        Platform.runLater(() -> {
                            if (response.statusCode() == 200) {
                                String responseBody = response.body();
                                System.out.println("채팅방 생성 성공: " + responseBody);
                                showAlert(Alert.AlertType.INFORMATION, "성공", "채팅방 생성에 성공했습니다.");
                                // 여기서 화면 전환 로직 호출
                                handleCloseButtonClick(event);
                            } else if (response.statusCode() == 400) {
                                System.out.println("채팅방 생성 실패");
                                showAlert(Alert.AlertType.ERROR, "실패", "채팅방 생성에 실패했습니다.");
                            } else {
                                // 로그인 실패 처리
                                System.out.println("응답 실패");
                                showAlert(Alert.AlertType.ERROR, "오류", "잘못된 요청");
                            }
                        });
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Exception 발생", "채팅방 생성 요청 중 오류가 발생했습니다.");
        }
    }

    @FXML
    private void handleCloseButtonClick(javafx.event.ActionEvent event){
        Stage stage = (Stage) chatCloseBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
