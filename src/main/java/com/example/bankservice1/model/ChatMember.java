package com.example.bankservice1.model;

public class ChatMember {
    private String userName;
    private String department;
    private String position;

    public ChatMember(){}
    public ChatMember(String userName, String department, String position) {
        this.userName = userName;
        this.department = department;
        this.position = position;
    }
    public String getUserName() {return userName;}
    public String getDepartment() {return department;}
    public String getPosition() {return position;}

}
