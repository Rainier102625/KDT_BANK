package com.example.bankservice1.model;

public class User {
    private String token;
    private long userIndex;
    private String userName;
    private boolean admin;

    public User(String token,long userIndex,String name, boolean admin) {
        this.token = token;
        this.userIndex = userIndex;
        this.userName = name;
        this.admin = admin;
    }

    public Long getUserIndex(){ return userIndex; }

    public String getUserName() { return userName; }

    public boolean getAdmin() {
        return admin;
    }

    public String getJwtToken() {
        return token;
    }
}
