package com.example.bankservice1.model;

import com.example.bankservice1.constants.apiconstants;
import com.example.bankservice1.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

public class WebSocketManager {

    // 1. 싱글톤 인스턴스 생성
    private static final WebSocketManager instance = new WebSocketManager();

    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    // 2. private 생성자로 외부에서 new 키워드로 생성하는 것을 막음
    private WebSocketManager() {
        WebSocketClient client = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(client);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(objectMapper);
        this.stompClient.setMessageConverter(converter);
    }

    // 3. 외부에서 인스턴스를 얻을 수 있는 public static 메서드 제공
    public static WebSocketManager getInstance() {
        return instance;
    }

    // 4. 연결 메서드
    public void connect(String token, Long currentUserIndex) {
        if (stompSession != null && stompSession.isConnected()) {
            System.out.println("이미 연결되어 있습니다.");
            return;
        }

        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);

        try {
            stompClient.connectAsync(apiconstants.BASE_WS_URL, (StompSessionHandler) connectHeaders, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    stompSession = session;
                    System.out.println("전역 WebSocket 연결 성공!");

                    // ** 중요: 개인 알림 채널 구독 **
                    // 이 부분은 서버와 약속이 필요합니다.
                    // 예를 들어 /topic/user/{userIndex}/notify 와 같은 주소입니다.
                    session.subscribe("/topic/user/" + currentUserIndex + "/notify", new StompFrameHandler() {
                        @Override
                        public java.lang.reflect.Type getPayloadType(StompHeaders headers) {
                            // 서버에서 보내주는 알림 DTO 클래스로 변경해야 함
                            return String.class; // 예: "새 메시지가 도착했습니다"
                        }
                        @Override
                        public void handleFrame(StompHeaders headers, Object payload) {
                            // 여기에 알림 로직 구현
                            Platform.runLater(() -> {
                                System.out.println("새로운 알림: " + payload);
                                // 예: 메인 뷰의 채팅 아이콘에 빨간 점 표시
                                new Alert(Alert.AlertType.INFORMATION, "새로운 메시지 알림: " + payload).show();
                            });
                        }
                    });
                }
                @Override
                public void handleTransportError(StompSession session, Throwable exception) {
                    System.err.println("전역 WebSocket 연결 오류: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("전역 WebSocket 연결 실패: " + e.getMessage());
        }
    }

    // 5. 채팅방 구독/구독취소/메시지전송 메서드
    public StompSession.Subscription subscribeToChatRoom(Long chatIndex, StompFrameHandler frameHandler) {
        if (stompSession == null || !stompSession.isConnected()) return null;
        return stompSession.subscribe("/topic/chat/" + chatIndex, frameHandler);
    }

    public void sendMessage(SendMessageRequest message) {
        if (stompSession == null || !stompSession.isConnected()) return;
        stompSession.send("/app/chat.sendMessage", message);
    }

    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }

    public boolean isConnected() {
        return stompSession != null && stompSession.isConnected();
    }
}
