package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.CustomerProduct;
import com.example.bankservice1.model.Customers;

import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;


public class CustomerCheckController {
    @FXML private Label customerName;
    @FXML private Label customerPhone;
    @FXML private Label customerBirth;
    @FXML private TableView<CustomerProduct> productTable;
    @FXML private TableColumn<CustomerProduct, String> productName;
    @FXML private TableColumn<CustomerProduct, String> accountnum;
    @FXML private TableColumn<CustomerProduct, String> accountCreateDate;
    @FXML private TableColumn<CustomerProduct, String> accountExpirationDate;
    @FXML private TableColumn<CustomerProduct, String> accountBalance;
    private int customersIndex;


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    private void initialize() {
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        accountnum.setCellValueFactory(new PropertyValueFactory<>("accountNum"));
        accountCreateDate.setCellValueFactory(new PropertyValueFactory<>("accountCreateDate"));
        accountExpirationDate.setCellValueFactory(new PropertyValueFactory<>("accountExpirationDate"));
        accountBalance.setCellValueFactory(new PropertyValueFactory<>("accountBalance"));
        handleCustomerProduct(productTable);
    }
    public void setCustomer(Customers customers) {
        customerName.setText(customers.getCustomerName());
        customerPhone.setText(customers.getCustomerPhone());
        customerBirth.setText(customers.getCustomerBirth());
       this.customersIndex = customers.getCustomerIndex();
       System.out.println(customersIndex);
        handleCustomerProduct(productTable);
    }
    public void handleCustomerProduct(TableView<CustomerProduct> productTable) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/accounts/customer/"+customersIndex))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            String responseBody = response.body();
                            CustomerProduct[] productArray = objectMapper.readValue(responseBody, CustomerProduct[].class);
                            List<CustomerProduct> productList = Arrays.asList(productArray);
                            Platform.runLater(() -> {
                                productTable.setItems(FXCollections.observableList(productList));
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace(); //예외 발생 위치, 호출 경로 출력
                        }
                    }else {
                        System.out.println(response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}