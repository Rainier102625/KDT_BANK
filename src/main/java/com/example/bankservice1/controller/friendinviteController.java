package com.example.bankservice1.controller;

import com.example.bankservice1.model.ChatMemberAdd;
import com.example.bankservice1.model.Friend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class friendinviteController {
    @FXML private TableView<Friend> friendTable;
    @FXML private TableColumn<Friend,String> userName;
    @FXML private TableColumn<Friend,String> department;
    @FXML private TableColumn<Friend,String> position;

    ChatViewController chatViewController = new ChatViewController();

    private List<Friend> friendsList = new ArrayList<>();

    private ObservableList<Friend> friendObservableListList;

    @FXML
    public void initialize() {

        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));

        friendObservableListList = FXCollections.observableArrayList();
        friendTable.setItems(friendObservableListList);

        friendTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    public void initData(List<Friend> friends) {
        if (friends != null) {
            friendObservableListList.setAll(friends);
            System.out.println("전달받은 친구 목록 개수: " + friends.size());
        }
    }

    @FXML
    private void handleAddSelectionFriend(){
        ObservableList<Friend> friendObservableList = friendTable.getSelectionModel().getSelectedItems();

        if (friendObservableList.isEmpty()) {
            showAlert("추가할 멤버를 선택하세요");
            return;
        }

        List<Integer> indexList = new ArrayList<>();

        for(Friend friend : friendObservableList){
            indexList.add(friend.getUserIndex());
        }

        ChatMemberAdd chatMemberAdd = new ChatMemberAdd(,indexList);


    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
