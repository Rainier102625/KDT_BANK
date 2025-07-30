package com.example.bankservice1.model;

public class LoginDTO {

    private final String userId;
    private final String userPassword;

    // 생성자를 통해 아이디와 비밀번호를 받음
    public LoginDTO(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
    }

    // 외부에서 값을 읽을 수 있도록 Getter 메서드를 제공
    public String getuserId() {
        return userId;
    }

    public String getUserPassword() {
        return userPassword;
    }
}
