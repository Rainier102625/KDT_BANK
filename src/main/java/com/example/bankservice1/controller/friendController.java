package com.example.bankservice1.controller;

import com.example.bankservice1.model.Friend;
import com.example.bankservice1.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class friendController {
    @FXML private TableView<Friend> friendTable;
    @FXML private TableColumn<Friend,String> name;
    @FXML private TableColumn<Friend,String> department;
    @FXML private TableColumn<Friend,String> rank;
    @FXML private TableColumn<Friend, Void> state;

    @FXML
    public void initialize(){
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        state.setCellFactory(column -> new TableCell<>() {
            private final Button moreButton = new Button("⋮");
            {
                moreButton.setStyle("-fx-background-color: white; -fx-font-size: 12px; -fx-text-fill: balck;");

                MenuItem editItem = new MenuItem("추가");
                MenuItem deleteItem = new MenuItem("취소");
                deleteItem.setStyle("-fx-text-fill: red;");

                ContextMenu contextMenu = new ContextMenu(editItem, deleteItem);

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
        ObservableList<Friend> friendList = FXCollections.observableArrayList(
                new Friend("박우현", "영업부", "대리" ),
                new Friend("조대원", "영업부", "사원" )
        );
        friendTable.setItems(friendList);
    }
}
