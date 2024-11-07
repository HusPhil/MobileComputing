package com.husph.mobilecomputing.infrared;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.husph.mobilecomputing.R;

public class InfraredStudyActivity extends AppCompatActivity {

    WebView wv_ytInfraredVid;
    String ytVidSrc = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/_wdqvUoqMuI\" title=\"Infrared Light Revealed: What You Need to Know\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>";
    LinearLayout dp_whatIsInfrared, dp_applications, dp_advantages, dp_limitations;
    TextView tv_whatIsInfrared, tv_applications, tv_advantages, tv_limitations;
    View vwc_whatIsInfrared, vwc_applications, vwc_advantages, vwc_limitations;
    boolean expanded_whatIsInfrared = false;
    boolean expanded_applications = false;
    boolean expanded_advantages = false;
    boolean expanded_limitations = false;

    LinearLayout dp_types, dp_working, dp_safety, dp_history, dp_future;
    TextView tv_types, tv_working, tv_safety, tv_history, tv_future;
    View vwc_types, vwc_working, vwc_safety, vwc_history, vwc_future;
    boolean expanded_types = false;
    boolean expanded_working = false;
    boolean expanded_safety = false;
    boolean expanded_history = false;
    boolean expanded_future = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infrared_info);

        // Setup WebView
        wv_ytInfraredVid = findViewById(R.id.wv_ytInfraredVid);
        wv_ytInfraredVid.loadData(ytVidSrc, "text/html", "utf-8");
        wv_ytInfraredVid.getSettings().setJavaScriptEnabled(true);
        wv_ytInfraredVid.setWebChromeClient(new WebChromeClient());

        initializeViews();
        setupClickListeners();
        closeAllDropdowns();
    }

    private void initializeViews() {
        // What is Infrared
        dp_whatIsInfrared = findViewById(R.id.dp_whatIsInfrared);
        tv_whatIsInfrared = findViewById(R.id.tv_whatIsInfrared);
        vwc_whatIsInfrared = findViewById(R.id.vwc_whatIsInfrared);
        
        // Applications
        dp_applications = findViewById(R.id.dp_applications);
        tv_applications = findViewById(R.id.tv_applications);
        vwc_applications = findViewById(R.id.vwc_applications);
        
        // Advantages
        dp_advantages = findViewById(R.id.dp_advantages);
        tv_advantages = findViewById(R.id.tv_advantages);
        vwc_advantages = findViewById(R.id.vwc_advantages);
        
        // Limitations
        dp_limitations = findViewById(R.id.dp_limitations);
        tv_limitations = findViewById(R.id.tv_limitations);
        vwc_limitations = findViewById(R.id.vwc_limitations);

        // Types of Infrared
        dp_types = findViewById(R.id.dp_types);
        tv_types = findViewById(R.id.tv_types);
        vwc_types = findViewById(R.id.vwc_types);

        // How Infrared Works
        dp_working = findViewById(R.id.dp_working);
        tv_working = findViewById(R.id.tv_working);
        vwc_working = findViewById(R.id.vwc_working);

        // Safety Considerations
        dp_safety = findViewById(R.id.dp_safety);
        tv_safety = findViewById(R.id.tv_safety);
        vwc_safety = findViewById(R.id.vwc_safety);

        // History
        dp_history = findViewById(R.id.dp_history);
        tv_history = findViewById(R.id.tv_history);
        vwc_history = findViewById(R.id.vwc_history);

        // Future Applications
        dp_future = findViewById(R.id.dp_future);
        tv_future = findViewById(R.id.tv_future);
        vwc_future = findViewById(R.id.vwc_future);

        // Set initial states
        toggleDescription(tv_whatIsInfrared, vwc_whatIsInfrared, expanded_whatIsInfrared);
        toggleDescription(tv_applications, vwc_applications, expanded_applications);
        toggleDescription(tv_advantages, vwc_advantages, expanded_advantages);
        toggleDescription(tv_limitations, vwc_limitations, expanded_limitations);
        toggleDescription(tv_types, vwc_types, expanded_types);
        toggleDescription(tv_working, vwc_working, expanded_working);
        toggleDescription(tv_safety, vwc_safety, expanded_safety);
        toggleDescription(tv_history, vwc_history, expanded_history);
        toggleDescription(tv_future, vwc_future, expanded_future);
    }

    private void setupClickListeners() {
        // What is Infrared
        dp_whatIsInfrared.setOnClickListener(v -> {
            toggleDescription(tv_whatIsInfrared, vwc_whatIsInfrared, expanded_whatIsInfrared);
            expanded_whatIsInfrared = !expanded_whatIsInfrared;
        });

        // Applications
        dp_applications.setOnClickListener(v -> {
            toggleDescription(tv_applications, vwc_applications, expanded_applications);
            expanded_applications = !expanded_applications;
        });

        // Advantages
        dp_advantages.setOnClickListener(v -> {
            toggleDescription(tv_advantages, vwc_advantages, expanded_advantages);
            expanded_advantages = !expanded_advantages;
        });

        // Limitations
        dp_limitations.setOnClickListener(v -> {
            toggleDescription(tv_limitations, vwc_limitations, expanded_limitations);
            expanded_limitations = !expanded_limitations;
        });

        // Types
        dp_types.setOnClickListener(v -> {
            toggleDescription(tv_types, vwc_types, expanded_types);
            expanded_types = !expanded_types;
        });

        // Working
        dp_working.setOnClickListener(v -> {
            toggleDescription(tv_working, vwc_working, expanded_working);
            expanded_working = !expanded_working;
        });

        // Safety
        dp_safety.setOnClickListener(v -> {
            toggleDescription(tv_safety, vwc_safety, expanded_safety);
            expanded_safety = !expanded_safety;
        });

        // History
        dp_history.setOnClickListener(v -> {
            toggleDescription(tv_history, vwc_history, expanded_history);
            expanded_history = !expanded_history;
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
        tv_whatIsInfrared.setVisibility(View.GONE);
        vwc_whatIsInfrared.setRotation(0);
        
        tv_applications.setVisibility(View.GONE);
        vwc_applications.setRotation(0);
        
        tv_advantages.setVisibility(View.GONE);
        vwc_advantages.setRotation(0);
        
        tv_limitations.setVisibility(View.GONE);
        vwc_limitations.setRotation(0);

        tv_types.setVisibility(View.GONE);
        vwc_types.setRotation(0);

        tv_working.setVisibility(View.GONE);
        vwc_working.setRotation(0);

        tv_safety.setVisibility(View.GONE);
        vwc_safety.setRotation(0);

        tv_history.setVisibility(View.GONE);
        vwc_history.setRotation(0);

        tv_future.setVisibility(View.GONE);
        vwc_future.setRotation(0);
    }
} 