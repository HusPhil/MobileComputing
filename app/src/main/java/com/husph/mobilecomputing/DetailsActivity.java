package com.husph.mobilecomputing;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.husph.mobilecomputing.utils.Constants;
import com.husph.mobilecomputing.utils.FormValidation;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class DetailsActivity extends AppCompatActivity {

    final static String TAG = "DetailsActivity";
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersRef;
    Dialog loadingDialog;


    private final String[] philippinesProvinces = {
            "Abra", "Agusan del Norte", "Agusan del Sur", "Aklan", "Albay", "Antique",
            "Apayao", "Aurora", "Basilan", "Bataan", "Batanes", "Batangas",
            "Benguet", "Biliran", "Bohol", "Bukidnon", "Bulacan", "Cagayan",
            "Camarines Norte", "Camarines Sur", "Camiguin", "Capiz", "Catanduanes",
            "Cavite", "Cebu", "Compostela Valley", "Cotabato", "Davao del Norte",
            "Davao del Sur", "Davao Occidental", "Davao Oriental", "Dinagat Islands",
            "Eastern Samar", "Guimaras", "Ifugao", "Ilocos Norte", "Ilocos Sur",
            "Iloilo", "Isabela", "Kalinga", "La Union", "Laguna", "Lanao del Norte",
            "Lanao del Sur", "Leyte", "Maguindanao", "Marinduque", "Masbate",
            "Misamis Occidental", "Misamis Oriental", "Mountain Province",
            "Negros Occidental", "Negros Oriental", "Northern Samar",
            "Nueva Ecija", "Nueva Vizcaya", "Occidental Mindoro", "Oriental Mindoro",
            "Palawan", "Pampanga", "Pangasinan", "Quezon", "Quirino",
            "Rizal", "Romblon", "Samar", "Sarangani", "Siquijor", "Sorsogon",
            "South Cotabato", "Southern Leyte", "Sultan Kudarat", "Sulu",
            "Surigao del Norte", "Surigao del Sur", "Tarlac", "Tawi-Tawi",
            "Zambales", "Zamboanga del Norte", "Zamboanga del Sur",
            "Zamboanga Sibugay"
    };

    private TextView tv_birthDate;
    private TextView tv_birthTime;
    private ImageButton btn_pickBirthDate;
    private ImageButton btn_pickBirthTime;
    private Button btn_finish_register;
    private Button btn_goBack;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private RadioGroup rg_gender;
    private TextInputEditText et_username;
    private TextInputEditText et_phone_number;
    private TextInputEditText et_province;
    private EditText et_interests;

    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitializeComponents();

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                btn_goBack_OnClickEvent();
            }

        });


    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(new ProgressBar(this));
        loadingDialog.setCancelable(false); // Prevent closing by tapping outside
        Objects.requireNonNull(loadingDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        loadingDialog.show();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void btn_pickBirthDate_onClickEvent() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog birthDatePicker = new DatePickerDialog(DetailsActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = " " + selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tv_birthDate.setText(selectedDate);
                }, year, month, day
        );

        birthDatePicker.show();
    }

    private void btn_pickBirthTime_onClickEvent() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(DetailsActivity.this,
                (view, selectedHour, selectedMinute) -> {

                    String amPm;
                    if (selectedHour >= 12) {
                        amPm = "PM";
                        if (selectedHour > 12) {
                            selectedHour -= 12; // Convert 24-hour to 12-hour format
                        }
                    } else {
                        amPm = "AM";
                        if (selectedHour == 0) {
                            selectedHour = 12; // Midnight case
                        }
                    }

                    String formattedSelectedMinute = selectedMinute < 10 ? ("0" + selectedMinute) : String.valueOf(selectedMinute);
                    String selectedTime = " " + selectedHour + ":" + formattedSelectedMinute + " " + amPm;
                    tv_birthTime.setText(selectedTime);
                }, hour, minute, false);

        timePickerDialog.show();

    }

    private void btn_goBack_OnClickEvent() {
        finish();
    }

    private void btn_finish_register_OnClickEvent() {
        final String username = et_username.getText().toString().trim();
        final String phoneNumber = et_phone_number.getText().toString().trim();
        final String province = et_province.getText().toString().trim();
        final String gender = String.valueOf(rg_gender.getCheckedRadioButtonId() == R.id.rb_female ? rb_female.getText() : rb_male.getText());
        final String birthDate = tv_birthDate.getText().toString().trim();
        final String birthTime = tv_birthTime.getText().toString().trim();
        final String interests = et_interests.getText().toString().trim();

        FormValidation.FormValidationResult registerFormResult = FormValidation.isDetailsFormValid(
                username,
                phoneNumber,
                province,
                gender,
                birthDate,
                birthTime,
                interests,
                new HashSet<>(Arrays.asList(philippinesProvinces))
        );

        String message = "";

        switch (registerFormResult) {
            case INVALID_USERNAME:
                message = FormValidation.WarningMessage.INVALID_USERNAME.getMessage();
                break;
            case INVALID_PHONE_NUMBER:
                message = FormValidation.WarningMessage.INVALID_PHONE_NUMBER.getMessage();
                break;
            case INVALID_PROVINCE:
                message = FormValidation.WarningMessage.INVALID_PROVINCE.getMessage();
                break;
            case INVALID_STATE:
                message = FormValidation.WarningMessage.INVALID_STATE.getMessage();
                break;
            case INVALID_GENDER:
                message = FormValidation.WarningMessage.INVALID_GENDER.getMessage();
                break;
            case INVALID_BIRTH_DATE:
                message = FormValidation.WarningMessage.INVALID_BIRTH_DATE.getMessage();
                break;
            case INVALID_BIRTH_TIME:
                message = FormValidation.WarningMessage.INVALID_BIRTH_TIME.getMessage();
                break;
            case INVALID_INTERESTS:
                message = FormValidation.WarningMessage.INVALID_INTERESTS.getMessage();
                break;
        }

        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();

        userData.put("username", username);
        userData.put("phoneNumber", phoneNumber);
        userData.put("province", province);
        userData.put("gender", gender);
        userData.put("birthDate", birthDate);
        userData.put("birthTime", birthTime);
        userData.put("interests", interests);


        // Create an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Information");

        String toastMessage = "Username: " + username + "\n" +
                "Phone Number: " + phoneNumber + "\n" +
                "Province: " + province + "\n" +
                "Gender: " + gender + "\n" +
                "Birth Date: " + birthDate + "\n" +
                "Birth Time: " + birthTime + "\n" +
                "Interests: " + interests;

// Set the toastMessage to the dialog
        builder.setMessage(toastMessage);

// Add an OK button
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent onCreateIntent = getIntent();
                String email = onCreateIntent.getStringExtra(Constants.PASSKEY_UserEmail);
                String password = onCreateIntent.getStringExtra(Constants.PASSKEY_UserPassword);

                showLoadingDialog();

                registerUserAsync(email, password, userData)
                        .thenAccept(result -> {
                            // Handle success (e.g., navigating to another activity)
                            dialog.dismiss();
                            finish();
                        })
                        .exceptionally(e -> {
                            // Handle failure (e.g., show an error dialog)
                            Log.e(TAG, "Error occurred: ", e);
                            dialog.dismiss();
                            return null;
                        })
                        .whenComplete((res, ex) -> {
                            hideLoadingDialog();
                        })
                        ;
            }
        });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private CompletableFuture<Void> registerUserAsync(String email, String password, Map<String, Object> userData) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String userId = user.getUid();

                            usersRef.child(userId).setValue(userData)
                                    .addOnCompleteListener(detailsTask -> {
                                        if (detailsTask.isSuccessful()) {
                                            runOnUiThread(() -> Toast.makeText(DetailsActivity.this, "User data saved", Toast.LENGTH_SHORT).show());
                                            Log.i(TAG, "User data saved for: " + user.getEmail());
                                            mAuth.signOut();
                                            future.complete(null);
                                        } else {
                                            Log.e(TAG, "Error saving user data: " + detailsTask.getException());
                                            runOnUiThread(() -> Toast.makeText(DetailsActivity.this, "Error saving user data", Toast.LENGTH_SHORT).show());
                                            future.completeExceptionally(detailsTask.getException());
                                        }
                                    });
                        } else {
                            Log.e(TAG, "FirebaseUser is null after successful registration");
                            runOnUiThread(() -> Toast.makeText(DetailsActivity.this, "Error: User data is missing", Toast.LENGTH_SHORT).show());
                            future.completeExceptionally(new Exception("User is null"));
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        runOnUiThread(() -> Toast.makeText(DetailsActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show());
                        future.completeExceptionally(task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Registration failed: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(DetailsActivity.this, FormValidation.WarningMessage.INVALID_EMAIL_WARNING.getMessage(), Toast.LENGTH_SHORT).show());
                    future.completeExceptionally(e);
                });

        return future;
    }

    private void InitializeComponents() {

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("users");

        tv_birthDate = findViewById(R.id.tv_birthDate);
        tv_birthTime = findViewById(R.id.tv_birthTime);

        btn_pickBirthDate = findViewById(R.id.btn_pickBirthDate);
        btn_pickBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_pickBirthDate_onClickEvent();
            }
        });

        btn_pickBirthTime = findViewById(R.id.btn_pickBirthTime);
        btn_pickBirthTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_pickBirthTime_onClickEvent();
            }
        });

        btn_finish_register = findViewById(R.id.btn_finish_register);
        btn_finish_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_finish_register_OnClickEvent();
            }
        });

        btn_goBack = findViewById(R.id.btn_goBack);
        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_goBack_OnClickEvent();
            }
        });

        et_username = findViewById(R.id.et_username);
        et_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: save details");
                usersRef.push().setValue("test");
            }
        });
        et_phone_number = findViewById(R.id.et_phone_number);
        et_interests = findViewById(R.id.et_interests);
        et_province = findViewById(R.id.et_province);

        rb_male = findViewById(R.id.rb_male);
        rb_female = findViewById(R.id.rb_female);
        rg_gender = findViewById(R.id.rg_gender);


    }




}