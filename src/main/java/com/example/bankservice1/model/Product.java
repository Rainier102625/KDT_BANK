package com.example.bankservice1.model;

public class Product {
    private int products_duration;
    private int interest_rate;
    private String products_type;
    private String product_name;

    public Product(String product_name, String products_type, int products_duration, int interest_rate) {
        this.product_name = product_name;
        this.products_type = products_type;
        this.products_duration = products_duration;
        this.interest_rate = interest_rate;
    }
    public String getProduct_name() {
        return product_name;
    }
    public String getProducts_type() {
        return products_type;
    }
    public int getProducts_duration() {
        return products_duration;
    }
    public int getInterest_rate() {
        return interest_rate;
    }

}
