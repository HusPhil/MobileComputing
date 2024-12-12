package com.husph.mobilecomputing.authentication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.husph.mobilecomputing.R;
import com.husph.mobilecomputing.models.UserProfile;
import com.husph.mobilecomputing.utils.Constants;

public class UserProfileActivity extends AppCompatActivity {


    private Gson gson;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userDBRef;

    private TextView tv_display_email;
    private TextView tv_display_username;
    private TextView tv_display_phone;
    private TextView tv_display_province;
    private TextView tv_display_gender;
    private TextView tv_display_interests;
    private TextView tv_display_birthDate;
    private Button btn_signOut;
    private Button btn_back;

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

    }

    private void InitializeComponents() {
        gson = new Gson();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        userDBRef = firebaseDatabase.getReference("users");
        userDBRef.child(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    try {
                        UserProfile userProfile = task.getResult().getValue(UserProfile.class);
                        showUserDetails(userProfile);
                    } catch (Exception e) {
                        Log.e("FIREBASE_READ_ERR", e.getMessage());
                    }
                }
            }
        });

        tv_display_email = findViewById(R.id.tv_display_email);
        tv_display_username = findViewById(R.id.tv_display_username);
        tv_display_phone = findViewById(R.id.tv_display_phone);
        tv_display_province = findViewById(R.id.tv_display_province);
        tv_display_gender = findViewById(R.id.tv_display_gender);
        tv_display_interests = findViewById(R.id.tv_display_interests);

        tv_display_birthDate = findViewById(R.id.tv_display_birthday);

        btn_signOut = findViewById(R.id.btn_signOut);
        btn_signOut.setOnClickListener(v -> {
            mAuth.signOut();
            finish();
        });
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            finish();
            Log.d("GET_USER_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        });
    }

    private void showUserDetails(UserProfile userProfile) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null) {
            Toast.makeText(this, "User is not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        tv_display_email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if(userProfile == null) {
            Toast.makeText(this, "Profile is not set up yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        Glide.with(this).load(currentUser.getPhotoUrl()).into((ImageView) findViewById(R.id.circularImageView));


        tv_display_username.setText(userProfile.getUsername());
        tv_display_phone.setText(userProfile.getPhoneNumber());
        tv_display_province.setText(userProfile.getProvince());
        tv_display_gender.setText(userProfile.getGender());

        if(userProfile.getGender().equals("Male")) {
            ImageView iv_genderIcon = findViewById(R.id.iv_genderIcon);
            iv_genderIcon.setImageResource(R.drawable.ic_gender_male);
        }
        else {
            ImageView iv_genderIcon = findViewById(R.id.iv_genderIcon);
            iv_genderIcon.setImageResource(R.drawable.ic_gender_female);
        }

        tv_display_interests.setText(userProfile.getInterests());

        tv_display_birthDate.setText(userProfile.getBirthDate());
    }
}