package com.rmit.ecommerce.repository;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;

public class UserModel {
    @DocumentId
    String id;

    String address;
    String cardNumber;
    String currentCartId;
    ArrayList<String> historyCarts;
    boolean isAdmin;
    String phone;

    public UserModel() {};

    public UserModel(String address, String cardNumber, String currentCartId, ArrayList<String> historyCarts, boolean isAdmin, String phone) {
        this.address = address;
        this.cardNumber = cardNumber;
        this.currentCartId = currentCartId;
        this.historyCarts = historyCarts;
        this.isAdmin = isAdmin;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCurrentCartId() {
        return currentCartId;
    }

    public void setCurrentCartId(String currentCartId) {
        this.currentCartId = currentCartId;
    }

    public ArrayList<String> getHistoryCarts() {
        return historyCarts;
    }

    public void setHistoryCarts(ArrayList<String> historyCarts) {
        this.historyCarts = historyCarts;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
