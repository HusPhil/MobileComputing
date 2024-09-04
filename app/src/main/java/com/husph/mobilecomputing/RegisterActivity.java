package com.husph.mobilecomputing;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.husph.mobilecomputing.utils.FirebaseAuthUtils;
import com.husph.mobilecomputing.utils.FormValidation;

import java.text.Normalizer;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuthUtils firebaseAuthUtils;

    private TextView tv_to_login;
    private EditText et_email_register;
    private EditText et_password_register;
    private EditText et_confirm_password_register;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitializeComponents();
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthUtils = new FirebaseAuthUtils(mAuth);

        if(firebaseAuthUtils.isUserLoggedIn()) {

            Toast.makeText(
                    RegisterActivity.this,
                    FormValidation.WarningMessage.USER_ALREADY_LOGGED_IN_WARNING.getMessage(),
                    Toast.LENGTH_LONG
                )
                .show();

            Intent openMainActivity = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(openMainActivity);
        }

    }

    private void tv_to_login_OnClickEvent() {
        Toast.makeText(RegisterActivity.this, "Navigate to login", Toast.LENGTH_LONG)
                .show();
        navigateToLoginScreen();
    }

    private void navigateToLoginScreen() {
        Intent openLoginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(openLoginActivity);
    }

    private void btn_register_OnClickEvent() {

        final String email = et_email_register.getText().toString().trim();
        final  String password = et_password_register.getText().toString().trim();
        final  String confirm_password = et_confirm_password_register.getText().toString().trim();

        FormValidation.FormValidationResult registerFormResult = FormValidation.isRegisterFormValid(
                email,
                password,
                confirm_password
        );

        String message = "";

        switch (registerFormResult) {
            case INVALID_EMAIL:
                message = FormValidation.WarningMessage.INVALID_EMAIL_WARNING.getMessage();
                break;
            case INVALID_PASSWORD:
                message = FormValidation.WarningMessage.INVALID_PASSWORD_WARNING.getMessage();
                break;
            case INVALID_CONFIRM_PASSWORD:
                message = FormValidation.WarningMessage.INVALID_CONFIRM_PASSWORD.getMessage();
                break;
            case INPUT_NULL:
                message = FormValidation.WarningMessage.INPUT_NULL_WARNING.getMessage();
                break;
        }

        if(!TextUtils.isEmpty(message)) {
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            Log.i(TAG, "RESULT REGISTER: " + task.getResult());
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();

                                String message = "Success, email: " + user.getEmail();

                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG)
                                        .show();

                                navigateToLoginScreen();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                        catch(Exception e) {
                            Log.e(TAG, "An error occurred while trying to create a user");
                            Toast.makeText(RegisterActivity.this, FormValidation.WarningMessage.INVALID_EMAIL_WARNING.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void InitializeComponents() {
        tv_to_login = findViewById(R.id.tv_to_login);
        tv_to_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tv_to_login_OnClickEvent();
                    }
                }
        );

        et_email_register = findViewById(R.id.et_email_register);
        et_password_register = findViewById(R.id.et_password_register);
        et_confirm_password_register = findViewById(R.id.et_confirm_password_register);

        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        btn_register_OnClickEvent();
                    }
                }
        );
    }
}