package com.example.bankservice1.model;

public class Account {
    private int requestIndex; //인덱스
    private String accountNum;
    private String customerName; //이름
    private String customerBirth;  //주민등록번호
    private String productName; //상품 이름
    private String requestType;

    public Account() {}
    public Account(int requestIndex, String accountNum, String customerName, String customerBirth, String productName, String requestType) {
        this.requestIndex = requestIndex;
        this.accountNum = accountNum;
        this.customerName = customerName;
        this.customerBirth = customerBirth;
        this.productName = productName;
        this.requestType = requestType;
    }
    public int getRequestIndex() {return requestIndex;}
    public String getAccountNum() {return accountNum;}
    public String getCustomerName() {return customerName;}
    public String getCustomerBirth() {return customerBirth;}
    public String getProductName() {return productName;}
    public String getRequestType() {return requestType;}
}
