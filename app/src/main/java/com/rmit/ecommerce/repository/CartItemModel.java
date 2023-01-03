package com.rmit.ecommerce.repository;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;

public class CartItemModel {
    @DocumentId
    String id;

    DocumentReference pid;
    int quantity;
    int size;
    Timestamp timestamp;

    public CartItemModel() {};

    public CartItemModel(DocumentReference pid, int quantity, int size, Timestamp timestamp) {
        this.pid = pid;
        this.quantity = quantity;
        this.size = size;
        this.timestamp = timestamp;
    }

    public DocumentReference getPid() {
        return pid;
    }

    public void setPid(DocumentReference pid) {
        this.pid = pid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
