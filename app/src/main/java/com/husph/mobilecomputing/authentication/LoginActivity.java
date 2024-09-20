package com.husph.mobilecomputing.authentication;

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
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.husph.mobilecomputing.R;
import com.husph.mobilecomputing.utils.Constants;
import com.husph.mobilecomputing.utils.FirebaseAuthUtils;
import com.husph.mobilecomputing.utils.FormValidation;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private FirebaseAuth mAuth;
    private FirebaseAuthUtils firebaseAuthUtils;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;

    private Button btn_login;
    private Button btn_login_google;
    private TextView tv_to_register;
    private EditText et_email_login;
    private EditText et_password_login;
    private ProgressBar pb_login;

    GoogleSignInClient googleSignInClient ;


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

        if(googleSignInClient != null) {
            googleSignInClient.signOut();
        }
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

    private void handleSigningInWithGoogle(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(Exception.class);

            if(account != null) {
                pb_login.setVisibility(View.INVISIBLE);
                resetFailedLoginCounter();

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                HashMap<String, String> userData = new HashMap<>();
                userData.put("username", account.getDisplayName());


                mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(LoginActivity.this, "Successfully signed in with Google!", Toast.LENGTH_SHORT).show();
                        finish();

                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String userId = user.getUid();

                            usersRef.child(userId).setValue(userData)
                                    .addOnCompleteListener(detailsTask -> {
                                        if (detailsTask.isSuccessful()) {
                                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "User data saved", Toast.LENGTH_SHORT).show());
                                            Log.i(TAG, "User data saved for: " + user.getEmail());
                                        } else {
                                            Log.e(TAG, "Error saving user data: " + detailsTask.getException());
                                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show());
                                        }
                                    });
                        } else {
                            Log.e(TAG, "FirebaseUser is null after successful registration");
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error: User data is missing", Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            }

        }
        catch (Exception e) {
            Log.e(TAG, e.toString());
            Toast.makeText(this, "An error happened while getting google account!", Toast.LENGTH_SHORT).show();
        }


        pb_login.setVisibility(View.INVISIBLE);
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

    //CLICK EVENTS HERE
    private void btn_login_google_OnClickEvent() {
        pb_login.setVisibility(View.VISIBLE);
        Intent openGoogleSignIn = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(openGoogleSignIn);

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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthUtils = new FirebaseAuthUtils(mAuth);
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");
        loginPrefs = getSharedPreferences("LogInPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPrefs.edit();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        Intent data = result.getData();


                        if(resultCode == RESULT_OK) {
                            handleSigningInWithGoogle(data);
                        }
                        else {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Google Sign In Cancelled",
                                    Toast.LENGTH_SHORT
                            ).show();
                            pb_login.setVisibility(View.INVISIBLE);
                        }


                    }
                }
        );

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
        btn_login_google = findViewById(R.id.btn_login_google);
        btn_login_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_login_google_OnClickEvent();
            }
        });

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