package com.husph.mobilecomputing.calculator;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.husph.mobilecomputing.R;

public class CalculatorActivity extends AppCompatActivity {

    TextView tv_result;
    int[] buttonIds;
    ButtonClickManager buttonClickManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponents();
    }

    private void InitializeComponents() {

        tv_result = findViewById(R.id.tv_result);

        buttonIds = new int[]{
                R.id.btn_num0, R.id.btn_num1, R.id.btn_num2, R.id.btn_num3,
                R.id.btn_num4, R.id.btn_num5, R.id.btn_num6, R.id.btn_num7,
                R.id.btn_num8, R.id.btn_num9, R.id.btn_add, R.id.btn_doubleZero,
                R.id.btn_subtract, R.id.btn_multiply, R.id.btn_divide,
                R.id.btn_equal, R.id.btn_clearAll, R.id.btn_percent, R.id.btn_backspace
        };

        buttonClickManager = new ButtonClickManager(tv_result);

        for (int buttonId : buttonIds) {
            findViewById(buttonId).setOnClickListener(buttonClickManager);
        }


    }
}