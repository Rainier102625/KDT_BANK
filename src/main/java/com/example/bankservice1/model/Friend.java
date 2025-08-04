package com.example.bankservice1.model;

public class Friend {
    private String name;
    private String dapartment;
    private String rank;

    public Friend(String name, String department, String rank) {
        this.name = name;
        this.dapartment = department;
        this.rank = rank;
    }
    public String getName() {
        return name;
    }
    public String getDepartment() {
        return dapartment;
    }
    public String getRank() {
        return rank;
    }
}
