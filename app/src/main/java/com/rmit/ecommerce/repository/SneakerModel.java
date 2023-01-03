package com.rmit.ecommerce.repository;

import java.util.ArrayList;

public class SneakerModel {
    private String title;
    private String brand;
    private String image;
    private ArrayList<String> sizes;
    private String figureImage;

    public SneakerModel(String title, String brand, String image, ArrayList<String> sizes) {
        this.title = title;
        this.brand = brand;
        this.image = image;
        this.sizes = sizes;
    }

    public String getFigureImage() {
        return figureImage;
    }

    public void setFigureImage(String figureImage) {
        this.figureImage = figureImage;
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

    public ArrayList<String> getSizes() {
        return sizes;
    }

    public void setSizes(ArrayList<String> sizes) {
        this.sizes = sizes;
    }
}
