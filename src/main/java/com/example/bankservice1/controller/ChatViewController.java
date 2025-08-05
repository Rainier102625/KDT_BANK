package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.ChatRoom;
import com.example.bankservice1.model.Friend;
import com.example.bankservice1.model.User;
import com.example.bankservice1.model.tokenManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatViewController {

    @FXML private VBox friendList;
    @FXML private VBox chatList;
    @FXML private ToggleButton chatbtn;
    @FXML private ToggleButton friendbtn;

    @FXML private Label chatmain; //채팅을 시작하세요 화면
    @FXML private BorderPane chatWindow;
    @FXML private Label chatRoomTitle;
    @FXML private VBox chatMessageContainer;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private VBox friendPanel;
    @FXML private Button toggleFriendPanelButton;

    @FXML private Button createGroupbtn;
    @FXML private Button inviteButton;
    @FXML private Button addUserButton;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    //친구 리스트
    @FXML private ListView<Friend> friendListView;
    private ObservableList<Friend> friendObservableList;
    private List<Friend> friendsListset = new ArrayList<>();

    // 채팅방 리스트
    @FXML private ListView<ChatRoom> chatListView;
    private ObservableList<ChatRoom> chatObservableList;
    private List<ChatRoom> ChatRoomList = new ArrayList<>();

    public ChatViewController() {}

    public List<Friend> getfriendsListset(){
        return this.friendsListset;
    }

    @FXML
    public void initialize() {
        FriendListSet();
        ChatListSet();
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

            createChatController controller = loader.getController();

            controller.initData(this.friendsListset);

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

                friendinviteController controller = loader.getController();

                controller.initData(this.friendsListset);

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


        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                openChatRoom(String.valueOf(newVal));
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

    @FXML
    private void FriendListSet(){

        friendObservableList = FXCollections.observableArrayList();
        friendListView.setItems(friendObservableList);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/friends"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
            if(response.statusCode()==200) {
                try{
                    String responseBody = response.body(); //응답 받아서 string으로 변환

                    // json 객체 리스트를 바로 friend 리스트에 저장
                    friendsListset = objectMapper.readValue(responseBody, new TypeReference<List<Friend>>() {});
                    friendObservableList.setAll(friendsListset);
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

    @FXML
    private void ChatListSet(){

        chatObservableList = FXCollections.observableArrayList();
        chatListView.setItems(chatObservableList);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiconstants.BASE_URL + "/chat/me"))
                .header("Authorization", "Bearer " + tokenManager.getInstance().getJwtToken())
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if(response.statusCode()==200) {
                        try{
                            String responseBody = response.body(); //응답 받아서 string으로 변환

                            // json 객체 리스트를 바로 friend 리스트에 저장
                            ChatRoomList = objectMapper.readValue(responseBody, new TypeReference<List<ChatRoom>>() {});
                            chatObservableList.setAll(ChatRoomList);
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
