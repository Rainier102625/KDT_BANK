package com.example.bankservice1.model;



public class UserSession {
    private static final UserSession instance = new UserSession();
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void login(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
