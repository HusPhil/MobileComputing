package com.husph.mobilecomputing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.husph.mobilecomputing.utils.Constants;
import com.husph.mobilecomputing.utils.FirebaseAuthUtils;
import com.husph.mobilecomputing.utils.FormValidation;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuthUtils firebaseAuthUtils;

    private Button btn_login;
    private TextView tv_to_register;
    private EditText et_email_login;
    private EditText et_password_login;
    private ProgressBar pb_login;

    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginPrefsEditor;


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


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!firebaseAuthUtils.isUserLoggedIn()) {
                    return;
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        handleUserLoggedIn();
    }

    private void handleUserLoggedIn() {

        if (firebaseAuthUtils.isUserLoggedIn()) {

            Toast.makeText(
                LoginActivity.this,
                FormValidation.WarningMessage.USER_ALREADY_LOGGED_IN_WARNING.getMessage(),
                Toast.LENGTH_LONG
                )
                .show();
            finish();
        }
    }

    private void btn_login_OnClickEvent() {

        pb_login.setVisibility(View.VISIBLE);
        handleUserLoggedIn();

        final String email = et_email_login.getText().toString().trim();
        final  String password = et_password_login.getText().toString().trim();

        FormValidation.FormValidationResult loginFormResult = FormValidation.isLoginFormValid(
                email,
                password
        );

        String message = "";

        switch (loginFormResult) {
            case INVALID_EMAIL:
                message = FormValidation.WarningMessage.INVALID_EMAIL_WARNING.getMessage();
                break;
            case INVALID_PASSWORD:
                message = FormValidation.WarningMessage.INVALID_PASSWORD_WARNING.getMessage();
                break;
            case INPUT_NULL:
                message = FormValidation.WarningMessage.INPUT_NULL_WARNING.getMessage();
                break;
        }

        if(!TextUtils.isEmpty(message)) {
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
            pb_login.setVisibility(View.INVISIBLE);
            return;
        }

        if(isLoginLocked()) {
            pb_login.setVisibility(View.INVISIBLE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            pb_login.setVisibility(View.INVISIBLE);
                            resetFailedLoginCounter();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            pb_login.setVisibility(View.INVISIBLE);
                            handleFailedLoginAttempt();
                        }
                    }
                });

    }

    private void handleFailedLoginAttempt() {
        int attempts = loginPrefs.getInt(Constants.LOGIN_ATTEMPTS_COUNT_PREFKEY, 0);
        attempts++;
        loginPrefsEditor.putInt(Constants.LOGIN_ATTEMPTS_COUNT_PREFKEY, attempts);
        loginPrefsEditor.apply();

        if (attempts >= Constants.LOGIN_MAX_ATTEMPTS) {
            // Lock the user out for the specified time
            long lockoutEndTime = System.currentTimeMillis() + Constants.LOGIN_LOCKOUT_TIME;
            loginPrefsEditor.putLong(Constants.LOGIN_LOCK_OUT_END_TIME_PREFKEY, lockoutEndTime);
            loginPrefsEditor.apply();
            Toast.makeText(this, "Too many attempts. Try again in 1 minute.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect password. Attempt " + attempts + " of " + Constants.LOGIN_MAX_ATTEMPTS, Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isLoginLocked() {
        long lockoutEndTime = loginPrefs.getLong(Constants.LOGIN_LOCK_OUT_END_TIME_PREFKEY, 0);
        if (System.currentTimeMillis() < lockoutEndTime) {
            long timeLeft = (lockoutEndTime - System.currentTimeMillis()) / 1000;
            Toast.makeText(this, "Try again in " + timeLeft + " seconds", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void resetFailedLoginCounter() {
        loginPrefsEditor.putInt(Constants.LOGIN_ATTEMPTS_COUNT_PREFKEY, 0);
        loginPrefsEditor.apply();
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

    private void InitializeComponents()     {
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthUtils = new FirebaseAuthUtils(mAuth);
        loginPrefs = getSharedPreferences("LogInPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPrefs.edit();

        pb_login = findViewById(R.id.pb_login);
        pb_login.setVisibility(View.INVISIBLE);

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