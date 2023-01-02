package com.rmit.ecommerce.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean isLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }
}
