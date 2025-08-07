package com.example.bankservice1.controller;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.*;
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
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

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
    private ObservableList<ChatRoom> chatObservableList = FXCollections.observableArrayList();

    @FXML private ListView<ChatMessageResponse> messageListView;
    private ObservableList<ChatMessageResponse> messageObservableList;
    private List<ChatMessageResponse> messageList = new ArrayList<>();

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private StompSession.Subscription currentSubscription; // 현재 구독 정보를 저장하기 위함
    private Long currentChatIndex; // 현재 접속한 채팅방의 인덱스
    private Long currentUserIndex = UserSession.getInstance().getUserIndex();


    public ChatViewController() {}

    public List<Friend> getfriendsListset(){
        return this.friendsListset;
    }

    @FXML
    public void initialize() {
        chatListView.setItems(chatObservableList);

        FriendListSet();
        ChatListSet();
        friendList.setVisible(true);
        friendList.setManaged(true);
        chatList.setVisible(false);
        chatList.setManaged(false);

//        connectWebSocket();

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

        chatListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newSelection) -> {
            if (newSelection != null) {
                chatmain.setVisible(false);
                chatWindow.setVisible(true);
                chatWindow.setManaged(true);
                openChatRoom(newSelection);
            } //채팅방 클릭시 openchatroom
        });

        // 메시지 전송 버튼
        sendButton.setOnAction(e -> {
            String content = messageInput.getText().trim();
            if (content.isEmpty() || currentChatIndex == null) {
                return; // 내용이 없거나, 들어간 채팅방이 없으면 전송 안 함
            }

            // ✅ 1. 서버로 메시지를 전송하는 로직 추가
            ChatMessagePayload payload = new ChatMessagePayload(currentChatIndex, currentUserIndex, content);
            WebSocketManager.getInstance().getSession().send("/app/chat.sendMessage", payload);

            // ✅ 2. 내가 보낸 메시지를 즉시 내 화면에 표시 (기존 로직)
//            addMessage(content, true);

            messageInput.clear();
        });
    }

    public void openChatRoom(ChatRoom room) {
        if (currentSubscription != null) {
            currentSubscription.unsubscribe();
            System.out.println("이전 채팅방(" + currentChatIndex + ") 구독을 해지합니다.");
        }

        // 2. 현재 채팅방 정보를 갱신합니다.
        this.currentChatIndex = room.getChatIndex(); // ChatRoom 객체에서 ID를 가져옵니다.

        // 3. UI를 업데이트합니다.
        chatmain.setVisible(false);
        chatWindow.setVisible(true);
        chatRoomTitle.setText(room.getChatName()); // ChatRoom 객체에서 이름을 가져옵니다.
        chatMessageContainer.getChildren().clear(); // 이전 대화 내용 삭제

        // 4. (선택사항) REST API로 이 채팅방의 과거 대화 기록을 불러오는 로직을 여기에 추가할 수 있습니다.

        // 5. 새로운 채팅방의 채널을 구독합니다.
        StompSession session = WebSocketManager.getInstance().getSession();
        if (session == null || !session.isConnected()) {
            System.err.println("웹소켓이 연결되지 않아 채팅방에 참여할 수 없습니다.");
            return;
        }

        String destination = "/topic/chat/" + this.currentChatIndex;
        currentSubscription = session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessageResponse.class; // 서버로부터 받을 메시지 DTO
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ChatMessageResponse chatMessage = (ChatMessageResponse) payload;

                // UI 업데이트는 반드시 Platform.runLater 안에서 처리해야 합니다.
                Platform.runLater(() -> {
                    // 내가 보낸 메시지인지 확인
                    boolean isMine = (chatMessage.getSenderIndex() == currentUserIndex);
                    // 수신한 메시지를 화면에 추가
                    addMessage(chatMessage.getContent(), isMine);
                });
            }
        });

        System.out.println("새로운 채팅방(" + this.currentChatIndex + ") 구독을 시작합니다. 주소: " + destination);
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

    private void connectWebSocket() {
        String token = tokenManager.getInstance().getJwtToken();
        if (token == null || token.isEmpty()) {
            System.err.println("JWT 토큰이 없어 WebSocket에 연결할 수 없습니다.");
            return;
        }

        String URL = "ws://localhost:8080/ws?token=" + token;

        WebSocketClient client = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(client);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token); // Bearer 접두사 추가
//
//        try {
//            this.stompSession = stompClient.connect(URL, connectHeaders, new StompSessionHandlerAdapter() {
//                @Override
//                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                    Platform.runLater(() -> System.out.println("WebSocket 연결 성공. 채팅방을 선택하세요."));
//                }
//                @Override
//                public void handleTransportError(StompSession session, Throwable exception) {
//                    Platform.runLater(() -> System.err.println("WebSocket 연결 오류: " + exception.getMessage()));
//                }
//            }).get(); // .get()을 통해 연결이 완료될 때까지 기다림 (실제 앱에서는 비동기 처리 고려)
//        } catch (Exception e) {
//            Platform.runLater(() -> System.err.println("WebSocket 연결 실패: " + e.getMessage()));
//        }
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
                            List<ChatRoom> ChatRoomList = objectMapper.readValue(responseBody, new TypeReference<List<ChatRoom>>() {});
                            Platform.runLater(() -> {
                                // 이미 연결된 chatObservableList의 내용물만 교체
                                chatObservableList.setAll(ChatRoomList);
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
