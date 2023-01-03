package com.rmit.ecommerce.repository;

import android.net.Uri;

import java.util.ArrayList;

public class SneakerModel extends SneakerBase {
    private Uri figureImage;

    public SneakerModel(String title, String brand, String image, ArrayList<String> sizes) {
        super(title, brand, image, sizes);
    }

    public SneakerModel(String title, String brand, String image, String description, String price, ArrayList<String> sizes, Uri figureImage) {
        super(title, brand, image, description, price, sizes);
        this.figureImage = figureImage;
    }

    public SneakerModel(String title, String brand, String image, ArrayList<String> sizes, Uri figureImage) {
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
