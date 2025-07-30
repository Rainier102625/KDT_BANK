package com.example.bankservice1.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // FXML 파일을 로드
        Parent root = FXMLLoader.load(getClass().getResource("NoticeView.fxml"));

        // Scene 생성
        Scene scene = new Scene(root, 800, 600);

        // Stage(윈도우) 설정
        primaryStage.setTitle("공지사항 관리 시스템");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}