package com.husph.mobilecomputing.menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.husph.mobilecomputing.MainActivity;
import com.husph.mobilecomputing.R;
import com.husph.mobilecomputing.authentication.UserProfileActivity;
import com.husph.mobilecomputing.bluetooth.BluetoothActivity;
import com.husph.mobilecomputing.bluetooth.BluetoothStudyActivity;
import com.husph.mobilecomputing.calculator.CalculatorActivity;
import com.husph.mobilecomputing.countries.CountryFlagActivity;
import com.husph.mobilecomputing.infrared.InfraredStudyActivity;

public class MenuActivity extends AppCompatActivity {

    CardView cv_helloWorld;
    CardView cv_userProfile;
    CardView cv_calculator;
    CardView cv_infrared;
    CardView cv_bluetooth;
    CardView cv_BTFileTransfer;
    CardView cv_countryFlags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponents();
    }

    private void InitializeComponents() {
        cv_bluetooth = findViewById(R.id.cv_bluetooth);
        cv_bluetooth.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, BluetoothStudyActivity.class));
        });
        cv_BTFileTransfer = findViewById(R.id.cv_BTFileTransfer);
        cv_BTFileTransfer.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, BluetoothActivity.class));
        });
        cv_calculator = findViewById(R.id.cv_calculator);
        cv_calculator.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, CalculatorActivity.class));
        });
        cv_helloWorld = findViewById(R.id.cv_helloWorld);
        cv_helloWorld.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, MainActivity.class));
        });
        cv_infrared = findViewById(R.id.cv_infrared);
        cv_infrared.setOnClickListener(v -> {
//            startActivity(new Intent(MenuActivity.this, InfraredActivity.class));
            startActivity(new Intent(MenuActivity.this, InfraredStudyActivity.class));
        });
        cv_userProfile = findViewById(R.id.cv_userProfile);
        cv_userProfile.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, UserProfileActivity.class));
        });

        cv_countryFlags = findViewById(R.id.cv_countryFlags);
        cv_countryFlags.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, CountryFlagActivity.class));
        });




    }
}