package com.rmit.ecommerce.repository;

public class SizeModel {
    private String sizeLabel;
    private int quantity;

    public SizeModel(String sizeLabel, int quantity) {
        this.sizeLabel = sizeLabel;
        this.quantity = quantity;
    }

    public String getSizeLabel() {
        return sizeLabel;
    }

    public void setSizeLabel(String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
