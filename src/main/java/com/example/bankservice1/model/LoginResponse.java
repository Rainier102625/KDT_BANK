package com.example.bankservice1.model;

public class LoginResponse {
    private String token;
    private String userName;
    private boolean admin;

    // Getters
    public String getToken() { return token; }
    public String getUserName() { return userName; }
    public boolean isAdmin() { return admin; }
}
