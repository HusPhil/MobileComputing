package com.husph.mobilecomputing.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthUtils {
    private final FirebaseAuth mAuth;

    public FirebaseAuthUtils(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }
}
