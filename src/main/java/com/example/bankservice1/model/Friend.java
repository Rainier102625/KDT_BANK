package com.example.bankservice1.model;

public class Friend {
    private String userName;
    private String department;
    private String position;
    private int userIndex;

    public Friend(){}

    public Friend(int userIndex, String userName, String department, String position) {
        this.userIndex = userIndex;
        this.userName = userName;
        this.department = department;
        this.position = position;
    }
    public int getUserIndex() {
        return userIndex;
    }
    public String getUserName() {
        return userName;
    }
    public String getDepartment() {
        return department;
    }
    public String getPosition() {
        return position;
    }
}
