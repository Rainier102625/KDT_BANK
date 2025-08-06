package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.Employee;
import com.example.bankservice1.model.UserSession;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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

public class EmployeeCheckController {
    @FXML
    private Button employeeSearch;
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee,String> userName;
    @FXML private TableColumn<Employee,String> department;
    @FXML private TableColumn<Employee,String> position;
    @FXML private TableColumn<Employee, Void> state;
    @FXML private TextField searchName;
    @FXML private TextField searchDepartment;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @FXML
    public void initialize() {

        handleEmployeeSearch(employeeTable);
        employeeSearch.setOnAction(event -> handleEmployeeSearch(employeeTable));

        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        department.setCellValueFactory(new PropertyValueFactory<>("department"));
        position.setCellValueFactory(new PropertyValueFactory<>("position"));
        state.setCellFactory(column -> new TableCell<>() {
            private final javafx.scene.control.Button moreButton = new javafx.scene.control.Button("⋮");
            {
                moreButton.setStyle("-fx-background-color: white; -fx-font-size: 12px; -fx-text-fill: black;");

                javafx.scene.control.MenuItem editItem = new javafx.scene.control.MenuItem("수정");
                javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem("삭제");
                deleteItem.setStyle("-fx-text-fill: red;");

                ContextMenu contextMenu = new ContextMenu(editItem, deleteItem);

                deleteItem.setOnAction(event -> {
                    Employee selectedEmployee = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION); //alert confirmation 사용시 확인 , 취소 두 가지 종류 버튼 생성
                    alert.setTitle("회원 삭제");
                    alert.setHeaderText(null);
                    alert.setContentText("정말 삭제하겠습니까?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            deleteEmployee(selectedEmployee.getUserIndex());
                        }
                    });
                });
                editItem.setOnAction(event -> {
                    try {
                        Employee selectedEmployee = getTableView().getItems().get(getIndex()); //현재 행의 인덱스를 알려줌
                        System.out.println("선택된 Employee의 userIndex: " + selectedEmployee.getUserIndex());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/EmployeeEdit.fxml"));
                        Parent root = loader.load();

                        EmployeeEditController controller = loader.getController();
                        controller.setUserIndex(selectedEmployee.getUserIndex());

                        Stage stage = new Stage();
                        stage.setTitle("사원 수정");
                        stage.setScene(new Scene(root));
                        stage.setResizable(false);
                        stage.show();


                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
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
    private void handleEmployeeSearch(TableView<Employee> employeeTable) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/users"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        try {
                            String responseBody = response.body();
                            Employee[] employeeArray = objectMapper.readValue(responseBody, Employee[].class);
                            List<Employee>  employeeList = Arrays.asList(employeeArray);

                            String query = searchName.getText().trim();
                            String query1 = searchDepartment.getText().trim();
                            List<Employee> filterList = query.isEmpty() && query1.isEmpty() ?employeeList :
                                    employeeList.stream()
                                            .filter(employee -> employee.getUserName().contains(query) && employee.getDepartment().contains(query1))
                                            .toList();
                            Platform.runLater(() -> {
                                employeeTable.setItems(FXCollections.observableList(filterList));
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else{
                        System.out.println("서버 오류");
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
    private void deleteEmployee(int userIndex) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/users/"+userIndex))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .DELETE()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode() == 200) {
                        System.out.println("success");
                        Platform.runLater(() -> {
                            handleEmployeeSearch(employeeTable);
                        });
                    }else{
                        System.out.println("fail");
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }
}

