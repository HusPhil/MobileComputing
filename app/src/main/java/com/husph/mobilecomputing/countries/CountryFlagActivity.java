package com.husph.mobilecomputing.countries;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.husph.mobilecomputing.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryFlagActivity extends AppCompatActivity {

    HashMap<String, String> countryMap = new HashMap<>();
    private ImageView iv_selectedFlag;
    private TextView tv_selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_country_flag);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        iv_selectedFlag = findViewById(R.id.iv_selectedFlag);
        tv_selectedCountry = findViewById(R.id.tv_selectedCountry);


        countryMap.put("AE", "United Arab Emirates");
        countryMap.put("AR", "Argentina");
        countryMap.put("AT", "Austria");
        countryMap.put("AU", "Australia");
        countryMap.put("BE", "Belgium");
        countryMap.put("BR", "Brazil");
        countryMap.put("CA", "Canada");
        countryMap.put("CH", "Switzerland");
        countryMap.put("CL", "Chile");
        countryMap.put("CN", "China");
        countryMap.put("CO", "Colombia");
        countryMap.put("CZ", "Czech Republic");
        countryMap.put("DE", "Germany");
        countryMap.put("DK", "Denmark");
        countryMap.put("EG", "Egypt");
        countryMap.put("ES", "Spain");
        countryMap.put("FI", "Finland");
        countryMap.put("FR", "France");
        countryMap.put("GB", "United Kingdom");
        countryMap.put("GR", "Greece");
        countryMap.put("HK", "Hong Kong");
        countryMap.put("ID", "Indonesia");
        countryMap.put("IE", "Ireland");
        countryMap.put("IL", "Israel");
        countryMap.put("IN", "India");
        countryMap.put("IT", "Italy");
        countryMap.put("JP", "Japan");
        countryMap.put("KR", "South Korea");
        countryMap.put("MX", "Mexico");
        countryMap.put("MY", "Malaysia");
        countryMap.put("NG", "Nigeria");
        countryMap.put("NL", "Netherlands");
        countryMap.put("NO", "Norway");
        countryMap.put("NZ", "New Zealand");
        countryMap.put("PH", "Philippines");
        countryMap.put("PK", "Pakistan");
        countryMap.put("PL", "Poland");
        countryMap.put("PT", "Portugal");
        countryMap.put("RU", "Russia");
        countryMap.put("SA", "Saudi Arabia");
        countryMap.put("SE", "Sweden");
        countryMap.put("SG", "Singapore");
        countryMap.put("TH", "Thailand");
        countryMap.put("TR", "Turkey");
        countryMap.put("TW", "Taiwan");
        countryMap.put("US", "United States");
        countryMap.put("VN", "Vietnam");
        countryMap.put("ZA", "South Africa");

        List<Map.Entry<String, String>> countryList = new ArrayList<>(countryMap.entrySet());
        CountryAdapter adapter = new CountryAdapter(this, countryList);
        ListView listView = findViewById(R.id.lv_countryList);
        listView.setAdapter(adapter);

        // Add click listener for list items
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Map.Entry<String, String> selectedCountry = countryList.get(position);
            updateSelectedFlag(selectedCountry.getKey(), selectedCountry.getValue());
        });
    }

    private void updateSelectedFlag(String countryCode, String countryName) {
        iv_selectedFlag = findViewById(R.id.iv_selectedFlag);
        Glide.with(this).load("https://flagsapi.com/"+ countryCode +"/flat/64.png").into(iv_selectedFlag);
        tv_selectedCountry.setText(countryName);
    }
}