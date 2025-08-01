package com.example.bankservice1.model;

public class User {
    private String token;
    private String userName;
    private boolean admin;

    public User(String token, String name, boolean admin) {
        this.token = token;
        this.userName = name;
        this.admin = admin;
    }
    public String getUserName() {
        return userName;
    }

    public boolean getAdmin() {
        return admin;
    }

    public String getJwtToken() {
        return token;
    }
}
