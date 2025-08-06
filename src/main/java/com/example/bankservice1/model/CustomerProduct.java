package com.example.bankservice1.model;

import java.math.BigDecimal;

public class CustomerProduct {
    private int accountIndex;
    private  String accountNum;
    private String accountCreateDate;
    private  String accountExpirationDate;
    private String customerName;
    private String productName;
    private BigDecimal accountBalance;

    public  CustomerProduct() {}
    public  CustomerProduct(int accountIndex, String accountNum, BigDecimal accountBalance, String accountCreateDate, String accountExpirationDate, String customerName, String productName) {
        this.accountIndex = accountIndex;
        this.accountNum = accountNum;
        this.accountBalance = accountBalance;
        this.accountCreateDate = accountCreateDate;
        this.accountExpirationDate = accountExpirationDate;
        this.customerName = customerName;
        this.productName = productName;
    }
    public int getAccountIndex() {return accountIndex;}
    public String getAccountNum() {return accountNum;}
    public BigDecimal getAccountBalance() {return accountBalance;}
    public String getAccountCreateDate() {return accountCreateDate;}
    public String getAccountExpirationDate() {return accountExpirationDate;}
    public String getCustomerName() {return customerName;}
    public String getProductName() {return productName;}

}
