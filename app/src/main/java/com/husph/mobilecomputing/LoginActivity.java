package com.husph.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private Button btn_login;
    private TextView tv_to_register;
    private EditText et_email_login;
    private EditText et_password_login;


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
        mAuth = FirebaseAuth.getInstance();


    }

    private void btn_login_OnClickEvent() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent openMainActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(openMainActivity);
            return;
        }

        final String emailInputText = String.valueOf(et_email_login.getText());
        final  String passwordInputText = String.valueOf(et_password_login.getText());

        mAuth.signInWithEmailAndPassword(emailInputText, passwordInputText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent openMainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(openMainActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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

        et_email_login = findViewById(R.id.et_email_login);
        et_password_login = findViewById(R.id.et_password_login);

    }
}