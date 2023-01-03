package com.rmit.ecommerce.repository;

import com.google.firebase.firestore.DocumentId;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class SneakerModel {
    @DocumentId
    private String id;

    private String title;
    private String brand;
    private String image;
    private String description;
    private double price;
    private ArrayList<HashMap<String, Integer>> size;
    private Uri figureImage;

    public SneakerModel() {};

    public SneakerModel(String title, String brand, String image, String description, double price, ArrayList<HashMap<String, Integer>> size) {
        this.title = title;
        this.brand = brand;
        this.image = image;
        this.description = description;
        this.price = price;
        this.size = size;
    }

    public Uri getFigureImage() {
        return figureImage;
    }

    public void setFigureImage(Uri figureImage) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ArrayList<HashMap<String, Integer>> getSize() {
        return size;
    }

    public void setSize(ArrayList<HashMap<String, Integer>> size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }
}
