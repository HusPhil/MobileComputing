package com.husph.mobilecomputing.utils;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.husph.mobilecomputing.R;

public class DetailsActivity extends AppCompatActivity {

    String[] countries = {"India","Indonesia","Africa","Afghanistan"};
    String[] states = {"gujarat","goa","maharashtra","rajsthan","aasam","bihar","west bangol"};

    private Spinner spn_state;
    private AutoCompleteTextView act_country;

    private ArrayAdapter stateArrAdapter, countryArrAdapter;

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

    }

    private void InitializeComponents() {
        spn_state = findViewById(R.id.spn_state);
        stateArrAdapter = new ArrayAdapter(DetailsActivity.this, android.R.layout.simple_list_item_1, states);
        spn_state.setAdapter(stateArrAdapter);

        act_country = findViewById(R.id.act_country);
        countryArrAdapter = new ArrayAdapter(DetailsActivity.this, android.R.layout.simple_list_item_1, countries);
        act_country.setAdapter(countryArrAdapter);
    }
}