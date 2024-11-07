package com.husph.mobilecomputing.bluetooth;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.husph.mobilecomputing.R;

public class BluetoothStudyActivity extends AppCompatActivity {

    WebView wv_ytInfraredVid;
    String ytVidSrc = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/cxP0Mdoz_Bo\" title=\"How Does Bluetooth Work?\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    LinearLayout dp_whatIsBluetooth;
    TextView tv_whatIsBluetooth;
    View vwc_whatIsBluetooth;

    boolean expanded_whatIsBluetooth = false;

    LinearLayout dp_point2;
    TextView tv_point2;
    View vwc_point2;
    boolean expanded_point2 = false;

    LinearLayout dp_versions, dp_profiles, dp_security, dp_wifi, dp_mesh, dp_ble, dp_troubleshooting, dp_future;
    TextView tv_versions, tv_profiles, tv_security, tv_wifi, tv_mesh, tv_ble, tv_troubleshooting, tv_future;
    View vwc_versions, vwc_profiles, vwc_security, vwc_wifi, vwc_mesh, vwc_ble, vwc_troubleshooting, vwc_future;
    boolean expanded_versions, expanded_profiles, expanded_security, expanded_wifi, expanded_mesh, expanded_ble, expanded_troubleshooting, expanded_future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bluetooth_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        wv_ytInfraredVid = findViewById(R.id.wv_ytInfraredVid);
        wv_ytInfraredVid.loadData(ytVidSrc, "text/html", "utf-8");
        wv_ytInfraredVid.getSettings().setJavaScriptEnabled(true);
        wv_ytInfraredVid.setWebChromeClient(new WebChromeClient());

        dp_whatIsBluetooth = findViewById(R.id.dp_whatIsBluetooth);
        tv_whatIsBluetooth = findViewById(R.id.tv_whatIsBluetooth);
        vwc_whatIsBluetooth = findViewById(R.id.vwc_whatIsBluetooth);
        toggleDescription(tv_whatIsBluetooth, vwc_whatIsBluetooth, expanded_whatIsBluetooth);
        dp_whatIsBluetooth.setOnClickListener(v -> {
            toggleDescription(tv_whatIsBluetooth, vwc_whatIsBluetooth, expanded_whatIsBluetooth);
            expanded_whatIsBluetooth = !expanded_whatIsBluetooth;
        });

        dp_point2 = findViewById(R.id.dp_point2);
        tv_point2 = findViewById(R.id.tv_point2);
        vwc_point2 = findViewById(R.id.vwc_point2);
        toggleDescription(tv_point2, vwc_point2, expanded_point2);
        dp_point2.setOnClickListener(v -> {
            toggleDescription(tv_point2, vwc_point2, expanded_point2);
            expanded_point2 = !expanded_point2;
        });

        initializeViews();
        setupClickListeners();
        closeAllDropdowns();
    }

    private void initializeViews() {
        // Versions
        dp_versions = findViewById(R.id.dp_versions);
        tv_versions = findViewById(R.id.tv_versions);
        vwc_versions = findViewById(R.id.vwc_versions);
        
        // Profiles
        dp_profiles = findViewById(R.id.dp_profiles);
        tv_profiles = findViewById(R.id.tv_profiles);
        vwc_profiles = findViewById(R.id.vwc_profiles);
        
        // Security
        dp_security = findViewById(R.id.dp_security);
        tv_security = findViewById(R.id.tv_security);
        vwc_security = findViewById(R.id.vwc_security);
        
        // WiFi
        dp_wifi = findViewById(R.id.dp_wifi);
        tv_wifi = findViewById(R.id.tv_wifi);
        vwc_wifi = findViewById(R.id.vwc_wifi);
        
        // Mesh
        dp_mesh = findViewById(R.id.dp_mesh);
        tv_mesh = findViewById(R.id.tv_mesh);
        vwc_mesh = findViewById(R.id.vwc_mesh);
        
        // BLE
        dp_ble = findViewById(R.id.dp_ble);
        tv_ble = findViewById(R.id.tv_ble);
        vwc_ble = findViewById(R.id.vwc_ble);
        
        // Troubleshooting
        dp_troubleshooting = findViewById(R.id.dp_troubleshooting);
        tv_troubleshooting = findViewById(R.id.tv_troubleshooting);
        vwc_troubleshooting = findViewById(R.id.vwc_troubleshooting);
        
        // Future
        dp_future = findViewById(R.id.dp_future);
        tv_future = findViewById(R.id.tv_future);
        vwc_future = findViewById(R.id.vwc_future);

        // Set initial states
        toggleDescription(tv_versions, vwc_versions, expanded_versions);
        toggleDescription(tv_profiles, vwc_profiles, expanded_profiles);
        toggleDescription(tv_security, vwc_security, expanded_security);
        toggleDescription(tv_wifi, vwc_wifi, expanded_wifi);
        toggleDescription(tv_mesh, vwc_mesh, expanded_mesh);
        toggleDescription(tv_ble, vwc_ble, expanded_ble);
        toggleDescription(tv_troubleshooting, vwc_troubleshooting, expanded_troubleshooting);
        toggleDescription(tv_future, vwc_future, expanded_future);
    }

    private void setupClickListeners() {
        // Versions
        dp_versions.setOnClickListener(v -> {
            toggleDescription(tv_versions, vwc_versions, expanded_versions);
            expanded_versions = !expanded_versions;
        });

        // Profiles
        dp_profiles.setOnClickListener(v -> {
            toggleDescription(tv_profiles, vwc_profiles, expanded_profiles);
            expanded_profiles = !expanded_profiles;
        });

        // Security
        dp_security.setOnClickListener(v -> {
            toggleDescription(tv_security, vwc_security, expanded_security);
            expanded_security = !expanded_security;
        });

        // WiFi
        dp_wifi.setOnClickListener(v -> {
            toggleDescription(tv_wifi, vwc_wifi, expanded_wifi);
            expanded_wifi = !expanded_wifi;
        });

        // Mesh
        dp_mesh.setOnClickListener(v -> {
            toggleDescription(tv_mesh, vwc_mesh, expanded_mesh);
            expanded_mesh = !expanded_mesh;
        });

        // BLE
        dp_ble.setOnClickListener(v -> {
            toggleDescription(tv_ble, vwc_ble, expanded_ble);
            expanded_ble = !expanded_ble;
        });

        // Troubleshooting
        dp_troubleshooting.setOnClickListener(v -> {
            toggleDescription(tv_troubleshooting, vwc_troubleshooting, expanded_troubleshooting);
            expanded_troubleshooting = !expanded_troubleshooting;
        });

        // Future
        dp_future.setOnClickListener(v -> {
            toggleDescription(tv_future, vwc_future, expanded_future);
            expanded_future = !expanded_future;
        });
    }

    private void toggleDescription(TextView descriptionView, View arrowView, boolean isExpanded) {
        if (isExpanded) {
            descriptionView.setVisibility(View.GONE);
            arrowView.setRotation(0);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            arrowView.setRotation(90);
        }
    }

    private void closeAllDropdowns() {
        // Set all description TextViews to GONE and arrows to 0 rotation
        tv_versions.setVisibility(View.GONE);
        vwc_versions.setRotation(0);

        tv_profiles.setVisibility(View.GONE);
        vwc_profiles.setRotation(0);

        tv_security.setVisibility(View.GONE);
        vwc_security.setRotation(0);

        tv_wifi.setVisibility(View.GONE);
        vwc_wifi.setRotation(0);

        tv_mesh.setVisibility(View.GONE);
        vwc_mesh.setRotation(0);

        tv_ble.setVisibility(View.GONE);
        vwc_ble.setRotation(0);

        tv_troubleshooting.setVisibility(View.GONE);
        vwc_troubleshooting.setRotation(0);

        tv_future.setVisibility(View.GONE);
        vwc_future.setRotation(0);
    }
}