package com.husph.mobilecomputing.utils;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.husph.mobilecomputing.MainActivity;
import com.husph.mobilecomputing.RegisterActivity;

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
