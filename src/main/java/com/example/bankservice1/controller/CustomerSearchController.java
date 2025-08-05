package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Customers;
import com.example.bankservice1.model.Employee;
import com.example.bankservice1.model.Friend;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextField;

import java.awt.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;

public class CustomerSearchController {
    @FXML private TextField searchName;
    @FXML private TextField searchPhone;
    @FXML private TextField searchBirth;
    @FXML private TableView<Customers>  customerTable;
    @FXML private TableColumn<Customers,String> customerName;
    @FXML private TableColumn<Customers,String> customerPhone;
    @FXML private TableColumn<Customers,String> customerBirth;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        customerBirth.setCellValueFactory(new PropertyValueFactory<>("customerBirth"));

        handleSearchCustomer(customerTable);
    }
    private void handleSearchCustomer(TableView<Customers> tableView) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/customers"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();
        httpClient.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try{
                            String responseBody = response.body();
                            Customers[] customersArray = objectMapper.readValue(responseBody, Customers[].class);
                            List<Customers>  customersList = Arrays.asList(customersArray);

                            String queryName = searchName.getText().trim();
                            String queryPhone = searchPhone.getText().trim();
                            String queryBirth = searchBirth.getText().trim();

                            List<Customers> filterlist = queryName.isEmpty() && queryPhone.isEmpty() && queryBirth.isEmpty() ? customersList :
                                    customersList.stream()
                                            .filter(customers -> customers.getCustomerName().contains(queryName) && customers.getCustomerPhone().contains(queryPhone) && customers.getCustomerBirth().contains(queryBirth))
                                            .toList();

                            Platform.runLater(() -> {
                                customerTable.setItems(FXCollections.observableList(filterlist));
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace(); //예외 발생 위치, 호출 경로 출력
                        }
                    } else{
                        System.out.println("서버 오류" + response.statusCode());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}
