package com.example.bankservice1.controller;

import com.example.bankservice1.model.Friend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class friendinviteController {
    @FXML private TableView<Friend> friendTable;
    @FXML private TableColumn<Friend,String> name;
    @FXML private TableColumn<Friend,String> department;
    @FXML private TableColumn<Friend,String> rank;

    @FXML
    public void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));

        ObservableList<Friend> friendList = FXCollections.observableArrayList(
                new Friend("박우현", "영업부", "대리" ),
                new Friend("조대원", "영업부", "사원" )
        );
        friendTable.setItems(friendList);
    }
}
