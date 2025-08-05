package com.example.bankservice1.controller;

import com.example.bankservice1.model.Friend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class createChatController {
    @FXML private TableView<Friend> friendTable;
    @FXML private TableColumn<Friend,String> userName;
    @FXML private TableColumn<Friend,String> department;
    @FXML private TableColumn<Friend,String> position;
    @FXML private TextField chatNameField;

    private List<Friend> friendsList = new ArrayList<>();

    private ObservableList<Friend> friendObservableListList;

    ChatViewController chatViewController;

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

    private void handleCreateButtonClink(ActionEvent actionEvent) throws IOException {
        ObservableList<Friend> selectedFriends = friendTable.getSelectionModel().getSelectedItems();

        if (selectedFriends.isEmpty()) {
            showAlert(Alert.AlertType.WARNING,"경고","채팅에 참여할 친구를 한 명 이상 선택해주세요");
        }

        String chatRoomName = chatNameField.getText().trim();
        if(chatRoomName.isEmpty()){
            showAlert(Alert.AlertType.WARNING,"경고","채팅방 이름을 설정하세요");
        }

        System.out.println("채팅방 이름: " + chatRoomName);
        System.out.println("--- 선택된 친구 목록 ---");

        for (Friend friend : selectedFriends) {
            System.out.println("이름: " + friend.getUserName() + ", 부서: " + friend.getDepartment());
        }



    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
