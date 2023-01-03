package com.rmit.ecommerce.repository;

import java.util.ArrayList;

public class SneakerBase {
    private String title;
    private String brand;
    private String image;
    private String description;
    private String price;
    private ArrayList<String> sizes;

    public SneakerBase(String title, String brand, String image, String description, String price, ArrayList<String> sizes) {
        this.title = title;
        this.brand = brand;
        this.image = image;
        this.description = description;
        this.price = price;
        this.sizes = sizes;
    }

    public SneakerBase(String title, String brand, String image, ArrayList<String> sizes) {
        this.title = title;
        this.brand = brand;
        this.image = image;
        this.sizes = sizes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<String> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<String> sizes) {
        this.sizes = sizes;
    }
}
