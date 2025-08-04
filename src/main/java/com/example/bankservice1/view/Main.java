package com.example.bankservice1.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("JavaFX 애플리케이션 시작...");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/bankservice1/view/login.fxml"));
//        // FXML 파일을 로드
//        Parent root = FXMLLoader.load(getClass().getResource("NoticeView.fxml"));

//        // Stage(윈도우) 설정
//        primaryStage.setTitle("공지사항 관리 시스템");
//        primaryStage.setScene(scene);
//        primaryStage.show();

            // Scene 생성
            Scene scene = new Scene(root, 800, 600);

            primaryStage.setTitle("로그인");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        public static void main(String[] args) {
            launch(args);
        }
    }