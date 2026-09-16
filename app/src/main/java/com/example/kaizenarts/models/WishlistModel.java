package com.example.kaizenarts.models;

import java.io.Serializable;

/** Mirrors ShowAllModel's fields so a wishlisted product can be reopened in DetailedActivity. */
public class WishlistModel implements Serializable {
    private String description;
    private String name;
    private String rating;
    private int price;
    private String img_url;
    private String type;

    public WishlistModel() {
    }

    public WishlistModel(ShowAllModel product) {
        this.description = product.getDescription();
        this.name = product.getName();
        this.rating = product.getRating();
        this.price = product.getPrice();
        this.img_url = product.getImg_url();
        this.type = product.getType();
    }

    public ShowAllModel toShowAllModel() {
        return new ShowAllModel(description, name, rating, price, img_url, type);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
