package com.example.bankservice1.model;

public class Account {
    private int accountIndex;
    private String accountNum;
    private String customerName;
    private String customerBirth;
    private String productName;
    private String accountStatus;

    public Account() {}
    public Account(int accountIndex, String accountNum, String customerName, String customerBirth, String productName, String accountStatus) {
        this.accountIndex = accountIndex;
        this.accountNum = accountNum;
        this.customerName = customerName;
        this.customerBirth = customerBirth;
        this.productName = productName;
        this.accountStatus = accountStatus;
    }
    public int getAccountIndex() {return accountIndex;}
    public String getAccountNum() {return accountNum;}
    public String getCustomerName() {return customerName;}
    public String getCustomerBirth() {return customerBirth;}
    public String getProductName() {return productName;}
    public String getAccountStatus() {return accountStatus;}
}
