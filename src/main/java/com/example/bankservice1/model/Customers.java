package com.example.bankservice1.model;

public class Customers {
    private int customerIndex;
    private String  customerId;
    private String  customerName;
    private String  customerPhone;
    private String  customerBirth;
    public Customers(){}
    public Customers(int customerIndex, String customerId, String customerName, String customerPhone, String customerBirth) {
        this.customerIndex = customerIndex;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerBirth = customerBirth;
    }
    public int getCustomerIndex() {return customerIndex;}
    public String getCustomerId() {return customerId;}
    public String getCustomerName() {return customerName;}
    public String getCustomerPhone() {return customerPhone;}
    public String getCustomerBirth() {return customerBirth;}
}
