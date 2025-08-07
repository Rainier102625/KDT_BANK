package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Employee;
import com.example.bankservice1.model.Product;
import com.example.bankservice1.model.UserSession;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class ProductManagementController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product,String> productName;
    @FXML private TableColumn<Product, String> type;
    @FXML private TableColumn<Product, Integer> duration;
    @FXML private TableColumn<Product, Double> rate;
    @FXML private TableColumn<Product, Void> state;
    @FXML private TextField nameSearch;
    @FXML private TextField typeSearch;
    @FXML private Button Search;
    @FXML private Button productAdd;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    @FXML
    public void initialize() {
        state.setVisible(false);
        if(UserSession.getInstance().getAdmin()) {
            state.setVisible(true);
        }

        productName.setCellValueFactory(new PropertyValueFactory<>("productName")); //cellvaluefactory는 데이터 값 설정
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        duration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        rate.setCellValueFactory(new PropertyValueFactory<>("rate"));
        handleProductSearch(productTable);
        Search.setOnAction(event -> handleProductSearch(productTable));
        productAdd.setVisible(false);
        if(UserSession.getInstance().getAdmin()){
            productAdd.setVisible(true);
        };

        productAdd.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/Productadd.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("상품 추가");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        state.setCellFactory(column -> new TableCell<>() {
            private final Button moreButton = new Button("⋮");
            {
                moreButton.setStyle("-fx-background-color: white; -fx-font-size: 12px; -fx-text-fill: black;");

                MenuItem deleteItem = new MenuItem("삭제");
                deleteItem.setStyle("-fx-text-fill: red;");

                ContextMenu contextMenu = new ContextMenu(deleteItem);

                deleteItem.setOnAction(event -> {
                    Product selectedProduct = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //alert confirmation 사용시 확인 , 취소 두 가지 종류 버튼 생성
                    alert.setTitle("상품 삭제");
                    alert.setHeaderText(null);
                    alert.setContentText("정말 삭제하겠습니까?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteProduct(selectedProduct.getProductsIndex());
                        }
                    });
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
    private  void handleProductSearch(TableView<Product> productTable) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/products"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            String responseBody = response.body();
                            Product[] productArray = objectMapper.readValue(responseBody, Product[].class);
                            List<Product> productList = Arrays.asList(productArray);
                            String query = nameSearch.getText();
                            String query1 =  typeSearch.getText();

                            List<Product> filterList  = query.isEmpty() && query1.isEmpty() ? productList :
                                    productList.stream()
                                            .filter(product -> product.getProductName().contains(query) && product.getType().contains(query1))
                                            .toList();
                            Platform.runLater(() -> {
                                productTable.setItems(FXCollections.observableList(filterList));
                            });
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else  {
                        System.out.println(response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
    private void deleteProduct(int productsIndex) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/products/"+productsIndex))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .DELETE()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        System.out.println("success");
                        Platform.runLater(() -> {
                            handleProductSearch(productTable);
                        });
                        Platform.runLater(() -> showAlert("삭제 완료"));
                    }else{
                        System.out.println("fail");
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}
