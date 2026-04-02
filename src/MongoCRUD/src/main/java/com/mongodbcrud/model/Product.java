package com.mongodbcrud.model;

import java.util.Date;

public class Product {
    private int id;
    private String productName;
    private double price;
    private Date releaseDate;

    public Product(){

    }

    public Product(int id, String productName, double price, Date releaseDate) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return this.productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String toString(){
        int var1000 = this.id;
        return "Product{id=" + var1000 + ", productName=" + this.productName + ", price=" + this.price + ", releaseDate= " + String.valueOf(this.releaseDate) + "}";
    }

}
