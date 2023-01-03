package com.rmit.ecommerce.repository;

import com.google.firebase.firestore.DocumentId;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class SneakerModel extends SneakerBase {
    @DocumentId
    private String id;
    private Uri figureImage;

    public SneakerModel(String title, String brand, String image, ArrayList<String> sizes) {
        super(title, brand, image, sizes);
    }

    public SneakerModel(String title, String brand, String image, String description, String price, ArrayList<HashMap<String, Integer>> sizes, Uri figureImage) {
        super(title, brand, image, description, price, sizes);
        this.figureImage = figureImage;
    }

    public SneakerModel(String title, String brand, String image, String description, double price, ArrayList<HashMap<String, Integer>> size) {
        super(title, brand, image, description, price, size)
    }

    public SneakerModel(String title, String brand, String image, ArrayList<HashMap<String, Integer>> sizes, Uri figureImage) {
        super(title, brand, image, sizes);
        this.figureImage = figureImage;
    }

    public Uri getFigureImage() {
        return figureImage;
    }

    public void setFigureImage(Uri figureImage) {
        this.figureImage = figureImage;
    }
}
