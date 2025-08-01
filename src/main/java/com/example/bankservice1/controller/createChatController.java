package com.example.bankservice1.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class createChatController {
    @FXML private ListView<String> friendlist;

    @FXML
    public void initialize(){
        friendlist.setItems(FXCollections.observableArrayList(
                "박우현","조대원","박은지"
        ));
        friendlist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); //friendlist 다중 선택 가능하게 설정
    }
}
