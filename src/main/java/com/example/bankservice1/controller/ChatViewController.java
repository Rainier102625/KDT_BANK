package com.example.bankservice1.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatViewController {

    @FXML private VBox friendList;
    @FXML private VBox chatList;
    @FXML private ToggleButton chatbtn;
    @FXML private ToggleButton friendbtn;
    @FXML private ListView<String> chatListView;
    @FXML private ListView<String> friendListView;
    @FXML private Label chatmain; //채팅을 시작하세요 화면
    @FXML private BorderPane chatWindow;
    @FXML private Label chatRoomTitle;
    @FXML private VBox chatMessageContainer;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private VBox friendPanel;
    @FXML private Button toggleFriendPanelButton;
    @FXML private ListView<String> participantListView;
    @FXML private Button createGroupbtn;
    @FXML private Button inviteButton;
    @FXML private Button addUserButton;

    @FXML
    public void initialize() {

        participantListView.getItems().addAll(
                "박우현",
                "조대원",
                "박은지"
        );

        friendList.setVisible(true);
        friendList.setManaged(true);
        chatList.setVisible(false);
        chatList.setManaged(false);

        friendbtn.setOnAction(e -> {
            friendList.setVisible(true);
            friendList.setManaged(true);
            chatList.setVisible(false);
            chatList.setManaged(false);
        });
        chatbtn.setOnAction(e -> {
            friendList.setVisible(false);
            friendList.setManaged(false);
            chatList.setVisible(true);
            chatList.setManaged(true);
        });
        createGroupbtn.setOnAction(e -> {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/createchat.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("채팅방 만들기");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        });
        inviteButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/friendinvite.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("친구 초대");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        addUserButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bankservice1/view/friendadd.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("친구 초대");
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.show();


            } catch (IOException ex) {
                ex.printStackTrace();
            }


        });

        toggleFriendPanelButton.setOnAction(e -> {
            boolean isVisible = friendPanel.isVisible();
            friendPanel.setVisible(!isVisible);
            friendPanel.setManaged(!isVisible);
        });
        // 채팅 목록 샘플 데이터
        chatListView.setItems(FXCollections.observableArrayList("KDT 1조", "KDT 2조", "KDT 3조"));
        friendListView.setItems(FXCollections.observableArrayList("박우현", "조대원", "박은지"));


        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                openChatRoom(newVal);
            } //채팅방 클릭시 openchatroom
        });

        // 메시지 전송 버튼
        sendButton.setOnAction(e -> {
            String text = messageInput.getText().trim();
            if (!text.isEmpty()) {
                addMessage(text, true);
                messageInput.clear();
            }
        });
    }

    public void openChatRoom(String roomName) {
        // 안내 문구 숨기고 채팅창 보이게
        chatmain.setVisible(false);
        chatmain.setManaged(false);
        chatWindow.setVisible(true);
        chatWindow.setManaged(true);

        chatRoomTitle.setText(roomName);
        chatMessageContainer.getChildren().clear(); // 기존 메시지 제거

        // 예시 메시지
        addMessage("안녕하세요! 여기는 " + roomName + " 입니다.", false);
    }

    public void addMessage(String content, boolean isMine) {
        HBox messageBox = new HBox();
        Label bubble = new Label(content);
        bubble.setWrapText(true);
        bubble.setPadding(new Insets(10));
        bubble.setMaxWidth(300);
        bubble.setStyle("-fx-background-radius: 12; -fx-font-size: 13px;");

        if (isMine) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #4f6df5; -fx-text-fill: white;"); //내가 보낸 메세지
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
            bubble.setStyle(bubble.getStyle() + "-fx-background-color: #f0f0f0; -fx-text-fill: black;"); //상대가 보낸 메세지
        }

        messageBox.getChildren().add(bubble);
        chatMessageContainer.getChildren().add(messageBox);
    }
}
