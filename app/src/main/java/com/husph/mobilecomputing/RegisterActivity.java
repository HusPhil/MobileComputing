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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;

    private TextView tv_to_login;
    private EditText et_email_register;
    private EditText et_password_register;
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
    }

    private void tv_to_login_OnClickEvent() {
        Toast.makeText(RegisterActivity.this, "Navigate to login", Toast.LENGTH_LONG)
                .show();


    }

    private void btn_register_OnClickEvent() {
        final String emailInputText = String.valueOf(et_email_register.getText());
        final  String passwordInputText = String.valueOf(et_password_register.getText());

        mAuth.createUserWithEmailAndPassword(emailInputText, passwordInputText)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            Toast.makeText(RegisterActivity.this, "Created new user successfully.", Toast.LENGTH_LONG)
                                    .show();

                            Intent openMainActivity = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(openMainActivity);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
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