package com.rmit.ecommerce.repository;

import com.google.firebase.firestore.DocumentId;

import java.util.ArrayList;

public class UserModel {
    @DocumentId
    String id;

    String address;
    String cardNumber;
    String currentCartId;
    String displayName;
    ArrayList<String> historyCartIds;
    String image;
    boolean isAdmin;
    String phone;

    public UserModel() {};

    public UserModel(String address, String cardNumber, String currentCartId, String displayName, ArrayList<String> historyCartIds, String image, boolean isAdmin, String phone) {
        this.address = address;
        this.cardNumber = cardNumber;
        this.currentCartId = currentCartId;
        this.displayName = displayName;
        this.historyCartIds = historyCartIds;
        this.image = image;
        this.isAdmin = isAdmin;
        this.phone = phone;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public ArrayList<String> getHistoryCartIds() {
        return historyCartIds;
    }

    public void setHistoryCartIds(ArrayList<String> historyCartIds) {
        this.historyCartIds = historyCartIds;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }
}
