package com.example.bankservice1.model;



public class UserSession {
    private static final UserSession instance = new UserSession();

    private String userName;
    private boolean admin;

    private UserSession() {}

    public static UserSession getInstance() { return instance; }

    public boolean getAdmin() { return admin; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public void setAdmin(boolean admin) { this.admin = admin; }

    // 6. 로그아웃 시 토큰을 삭제하는 메서드
    public void clearLogin() {
        this.userName = null;
        this.admin = false;
    }
}
