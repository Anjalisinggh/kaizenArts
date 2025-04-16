package com.example.kaizenarts.models;
import java.io.Serializable;

public class PopularProductsmodel implements Serializable {
     private String description;
    private String name;
    private String rating;
    private int price;
    private String img_url;

    // Default constructor
    public void PopularProductsModel() {
    }

    // Parameterized constructor
    public void PopularProductsModel(String description, String name, String rating, int price, String img_url) {
        this.description = description;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.img_url = img_url;
    }

    // Getter and setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for rating
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    // Getter and setter for img_url
    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    // Getter and setter for price
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
