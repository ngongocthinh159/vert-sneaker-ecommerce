package com.rmit.ecommerce.repository;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;

public class CartModel {
    @DocumentId
    String id;

    private ArrayList<String> cartItemIds;
    private boolean status;
    private Timestamp timestamp;
    private String total;

    public CartModel() {};

    public CartModel(ArrayList<String> cartItemIds, boolean status, Timestamp timestamp, String total) {
        this.cartItemIds = cartItemIds;
        this.status = status;
        this.timestamp = timestamp;
        this.total = total;
    }

    public ArrayList<String> getCartItemIds() {
        return cartItemIds;
    }

    public void setCartItemIds(ArrayList<String> cartItemIds) {
        this.cartItemIds = cartItemIds;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getId() {
        return id;
    }
}
