package com.example.bankservice1.model;

public class User {

    private String userName;
    private boolean admin;

    public User(String name, boolean admin) {
        this.userName = name;
        this.admin = admin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
