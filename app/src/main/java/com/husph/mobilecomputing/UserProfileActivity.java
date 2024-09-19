package com.husph.mobilecomputing;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.husph.mobilecomputing.models.UserProfile;
import com.husph.mobilecomputing.utils.Constants;

public class UserProfileActivity extends AppCompatActivity {

    private UserProfile userProfile;
    private Gson gson;

    private EditText et_display_email;
    private EditText et_display_username;
    private EditText et_display_phone;
    private EditText et_display_province;
    private EditText et_display_gender;
    private EditText et_display_interests;
    private TextView tv_display_birthDate;
    private TextView tv_display_birthTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponents();

        showUserDetails();
    }

    private void InitializeComponents() {
        gson = new Gson();

        String userProfileJson = getIntent().getStringExtra(Constants.PASSKEY_UseProfile);

        if(userProfileJson != null) {
            userProfile =  gson.fromJson(userProfileJson, UserProfile.class);
        }

        et_display_email = findViewById(R.id.et_display_email);
        et_display_username = findViewById(R.id.et_display_username);
        et_display_phone = findViewById(R.id.et_display_phone);
        et_display_province = findViewById(R.id.et_display_province);
        et_display_gender = findViewById(R.id.et_display_gender);
        et_display_interests = findViewById(R.id.et_display_interests);

        tv_display_birthDate = findViewById(R.id.tv_display_birthDate);
        tv_display_birthTime = findViewById(R.id.tv_display_birthTime);


    }

    private void showUserDetails() {
        if(userProfile == null) {
            Toast.makeText(this, "Profile is not set up yet.", Toast.LENGTH_SHORT).show();
        }
        et_display_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        et_display_username.setText(userProfile.getUsername());
        et_display_phone.setText(userProfile.getPhoneNumber());
        et_display_province.setText(userProfile.getProvince());
        et_display_gender.setText(userProfile.getGender());
        et_display_interests.setText(userProfile.getInterests());

        tv_display_birthDate.setText(userProfile.getBirthDate());
        tv_display_birthTime.setText(userProfile.getBirthTime());
    }
}