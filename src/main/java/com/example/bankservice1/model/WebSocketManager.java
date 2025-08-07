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
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

import static com.example.bankservice1.constants.apiconstants.*;

public class WebSocketManager {

    // 1. 싱글톤 인스턴스 생성
    private static final WebSocketManager instance = new WebSocketManager();

    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    // 2. private 생성자로 외부에서 new 키워드로 생성하는 것을 막음
    private WebSocketManager() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        this.stompClient = new WebSocketStompClient(new SockJsClient(transports));
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    // 3. 외부에서 인스턴스를 얻을 수 있는 public static 메서드 제공
    public static WebSocketManager getInstance() {
        return instance;
    }

    public void connect(final Runnable onConnected) {
        StompSessionHandlerAdapter sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("✅ 웹소켓 연결 성공! Session ID: " + session.getSessionId());
                stompSession = session; // 연결된 세션을 저장

                // 연결 성공 후 실행할 작업을 UI 스레드에서 실행
                Platform.runLater(onConnected);
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                System.err.println("🚨 웹소켓 통신 중 예외 발생: " + exception.getMessage());
                // 실제 운영 코드에서는 로그를 남기거나, UI에 연결 오류를 알리는 등의 처리를 할 수 있습니다.
                exception.printStackTrace();
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("🚨 웹소켓 연결 자체에 오류 발생: " + exception.getMessage());
                // 네트워크 문제 등으로 연결이 끊겼을 때 호출됩니다.
                // 여기서 재연결 로직을 구현할 수 있습니다.
            }
        };
        // 서버에 연결 시도
        stompClient.connect(apiconstants.BASE_WS_URL, sessionHandler);
    }

    // 현재 세션 반환
    public StompSession getSession() {
        return this.stompSession;
    }

    // 연결 해제
    public void disconnect() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }
}
