package com.example.bankservice1.controller;

import com.example.bankservice1.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductManagementController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product,String> product_name;
    @FXML private TableColumn<Product, String> products_type;
    @FXML private TableColumn<Product, Integer> products_duration;
    @FXML private TableColumn<Product, Integer> interest_rate;
    @FXML private TableColumn<Product, Void> state;

    @FXML
    public void initialize() {
        product_name.setCellValueFactory(new PropertyValueFactory<>("product_name")); //cellvaluefactory는 데이터 값 설정
        products_type.setCellValueFactory(new PropertyValueFactory<>("products_type"));
        products_duration.setCellValueFactory(new PropertyValueFactory<>("products_duration"));
        interest_rate.setCellValueFactory(new PropertyValueFactory<>("interest_rate"));

        state.setCellFactory(column -> new TableCell<>() {
            private final Button moreButton = new Button("⋮");
            {
                moreButton.setStyle("-fx-background-color: white; -fx-font-size: 12px; -fx-text-fill: balck;");

                MenuItem editItem = new MenuItem("수정");
                MenuItem deleteItem = new MenuItem("삭제");
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
        }); //setCellFactory 셀의 외형, 행동 커스터마이징

        ObservableList<Product> productList = FXCollections.observableArrayList(
                new Product("슈퍼 정기예금", "예금", 12, 3),
                new Product("골드 적금", "적금", 6, 2)
        );
        productTable.setItems(productList);
    }

}
