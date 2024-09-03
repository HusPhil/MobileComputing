package com.husph.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    private Button btn_login;
    private TextView tv_to_register;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitializeComponents();
    }

    private void btn_login_OnClickEvent() {
        finish();
    }

    private void tv_to_register_OnClickEvent() {
        Toast.makeText(
                LoginActivity.this,
                "Navigate to Register",
                Toast.LENGTH_SHORT
        ).show();
        Intent openRegisterScreen = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(openRegisterScreen);
        InitializeComponents();
    }

    private void InitializeComponents() {
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_login_OnClickEvent();
                    }
                }
        );

        tv_to_register = findViewById(R.id.tv_to_register);
        tv_to_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv_to_register_OnClickEvent();
                    }
                }
        );
    }
}