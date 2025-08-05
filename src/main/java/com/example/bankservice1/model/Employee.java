package com.example.bankservice1.model;

public class Employee {
    private int userIndex;
    private String userId;
    private String userName;
    private String userPhone;
    private String department;
    private String position;
    private boolean admin;

    public Employee() {}
    public Employee(int userIndex, String userId, String userName, String userPhone, String department, String position, boolean admin) {
        this.userIndex = userIndex;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.department = department;
        this.position = position;
        this.admin = admin;
    }

    public int getUserIndex() {return userIndex;}
    public String getUserId(){return userId;}
    public String getUserName(){return userName;}
    public String getUserPhone(){return userPhone;}
    public String getDepartment(){return department;}
    public String getPosition(){return position;}
    public boolean getAdmin(){return admin;}
}
