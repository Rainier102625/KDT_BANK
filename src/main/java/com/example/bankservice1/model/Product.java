package com.example.bankservice1.model;

public class Product {
    private int productsIndex;
    private String type;
    private  double rate;
    private String productName;
    private int duration;
    public Product() {}
    public Product(int productsIndex, String type, double rate, String productName, int duration) {
        this.productsIndex = productsIndex;
        this.type = type;
        this.rate = rate;
        this.productName = productName;
        this.duration = duration;
    }
    public int getProductsIndex() {return productsIndex;}
    public String getType() {return type;}
    public double getRate() {return rate;}
    public String getProductName() {return productName;}
    public int getDuration() {return duration;}

}
