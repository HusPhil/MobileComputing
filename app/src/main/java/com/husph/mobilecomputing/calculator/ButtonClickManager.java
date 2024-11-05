package com.husph.mobilecomputing.calculator;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.husph.mobilecomputing.R;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.ArrayList;
import java.util.List;

public class ButtonClickManager implements View.OnClickListener {

    private StringBuilder input;
    private TextView resultTextView;
    private AppCompatActivity activity;
    private android.content.Context context;

    private SharedPreferences calcHistoryPrefs;
    private SharedPreferences.Editor calcHistoryPrefsEditor;

    private boolean isKeyboardOn;

    private static final String HISTORY_KEY = "calcHistory"; // Key for SharedPreferences
    private List<String> calcHistory;
    private static final String TAG = "ButtonClickManager";

    public ButtonClickManager(AppCompatActivity activity, TextView resultTextView, SharedPreferences calcHistoryPrefs, SharedPreferences.Editor calcHistoryPrefsEditor) {
        this.activity = activity;
        this.context = (android.content.Context) activity;
        this.resultTextView = resultTextView;
        this.calcHistoryPrefs = calcHistoryPrefs;
        this.calcHistoryPrefsEditor = calcHistoryPrefsEditor;

        this.calcHistory = new ArrayList<>(); // Initialize history list
        loadHistory(); // Load history from SharedPreferences
        input = new StringBuilder();
    }

    @Override
    public void onClick(View view) {
        int selectedButtonId = view.getId();

        if (selectedButtonId == R.id.btn_num0 ||
                selectedButtonId == R.id.btn_num1 ||
                selectedButtonId == R.id.btn_num2 ||
                selectedButtonId == R.id.btn_num3 ||
                selectedButtonId == R.id.btn_num4 ||
                selectedButtonId == R.id.btn_num5 ||
                selectedButtonId == R.id.btn_num6 ||
                selectedButtonId == R.id.btn_num7 ||
                selectedButtonId == R.id.btn_num8 ||
                selectedButtonId == R.id.btn_num9)
        {

            Button numberButton = (Button) view;
            String numberText = numberButton.getText().toString();

            if(input.length() == 1 && input.charAt(0) == '0') {
                input.setLength(0);
            }

            input.append(numberText);
            resultTextView.setText(input.toString());

            return;
        }

        if (selectedButtonId == R.id.btn_add) {
            resultTextView.setText(input.append(" + "));
        } else if (selectedButtonId == R.id.btn_subtract) {
            resultTextView.setText(input.append(" - "));
        } else if (selectedButtonId == R.id.btn_multiply) {
            resultTextView.setText(input.append(" * "));
        } else if (selectedButtonId == R.id.btn_divide) {
            resultTextView.setText(input.append(" / "));
        } else if (selectedButtonId == R.id.btn_backspace) {
            backSpace();
        } else if (selectedButtonId == R.id.btn_clearAll) {
            clearAll();
        } else if (selectedButtonId == R.id.btn_exit) {
            activity.finish();
        } else if (selectedButtonId == R.id.btn_history) {
            showCalcHistory();
        } else if (selectedButtonId == R.id.btn_doubleZero) {
            resultTextView.setText(input.append("00"));
        } else if (selectedButtonId == R.id.btn_decimalPoint) {
            resultTextView.setText(input.append("."));
        } else if (selectedButtonId == R.id.btn_keyboard) {
            toggleKeyboard();
        } else if (selectedButtonId == R.id.btn_enterInput) {
            EditText et_keyboardInput = activity.findViewById(R.id.et_keyboardInput);
            if(!et_keyboardInput.getText().toString().isEmpty()) {
                input.setLength(0);
                resultTextView.setText(input.append(et_keyboardInput.getText()));
            }
            toggleKeyboard();
        } else if (selectedButtonId == R.id.btn_equal) {
            calculateResult();
        }


    }

    private void toggleKeyboard() {
        CardView cv_keyboardInput = activity.findViewById(R.id.cv_keyboardInput);
        Button btn_enterInput = activity.findViewById(R.id.btn_enterInput);
        MaterialButton btn_keyboard = activity.findViewById(R.id.btn_keyboard);

        isKeyboardOn = !isKeyboardOn;

        if(isKeyboardOn) {
            cv_keyboardInput.setVisibility(View.VISIBLE);
            btn_enterInput.setVisibility(View.VISIBLE);
            btn_keyboard.setIconResource(R.drawable.ic_close);
            btn_keyboard.setIconGravity(MaterialButton.ICON_GRAVITY_END);
        }
        else {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            View view = activity.getCurrentFocus();
            if (imm != null && view != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                view.clearFocus(); // Optionally clear focus from the EditText
            }

            cv_keyboardInput.setVisibility(View.GONE);
            btn_enterInput.setVisibility(View.GONE);
            btn_keyboard.setIconResource(R.drawable.ic_keyboard);
            btn_keyboard.setIconGravity(MaterialButton.ICON_GRAVITY_START);
        }

    }

    private void showCalcHistory() {
        // Create AlertDialog builder
        AlertDialog.Builder historyAlertBuilder = new AlertDialog.Builder(activity);

        View historyView = activity.getLayoutInflater().inflate(R.layout.calc_history, null);

        LinearLayout historyList = historyView.findViewById(R.id.ll_historyList);

        historyAlertBuilder.setTitle("History");

        if(calcHistory.isEmpty()) {
            TextView tvHistoryItem = new TextView(activity);
            tvHistoryItem.setText("NO HISTORY YET");
            tvHistoryItem.setTextSize(20);
            tvHistoryItem.setPadding(10, 16, 10, 12);

            historyList.addView(tvHistoryItem);
        } else {
            for(String historyItem : calcHistory) {
                TextView tvHistoryItem = new TextView(activity);
                tvHistoryItem.setText(historyItem);
                tvHistoryItem.setTextSize(16);
                tvHistoryItem.setPadding(10, 16, 10, 12);

                View hr = new View(activity);
                hr.setBackgroundColor(activity.getResources().getColor(com.google.android.material.R.color.material_dynamic_neutral70));
                hr.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));

                historyList.addView(tvHistoryItem);
                historyList.addView(hr);
            }
        }


        historyAlertBuilder.setView(historyView);
        historyAlertBuilder.setNegativeButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                calcHistoryPrefsEditor.clear();
                calcHistoryPrefsEditor.apply();

                calcHistory.clear();

            }
        });

        historyAlertBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        // Create and show the AlertDialog
        historyAlertBuilder.create().show();
    }

    private void clearAll() {
        input.setLength(0);
        resultTextView.setText(input.append("0"));
    }

    private void backSpace() {
        if (input.length() <= 1) {
            input.setLength(0);
            resultTextView.setText(input.append("0"));
            return;
        }
        resultTextView.setText(input.deleteCharAt(input.length() - 1).toString());
    }

    private void calculateResult() {
        try {
            final String result = String.valueOf(eval(input.toString()));
            String formattedResult = result;

            if (result.endsWith(".0")) {
                formattedResult = result.substring(0, result.length() - 2);
            }

            // Add calculation to history
            if(!TextUtils.equals(input.toString(), formattedResult)) {
                saveToHistory(input.toString() + " = " + formattedResult);
            }

            input.setLength(0);
            input.append(formattedResult);
            resultTextView.setText(input);
        } catch (Exception e) {
            input.setLength(0);
            resultTextView.setText("Syntax Error");
        }
    }

    private double eval(String expression) {
        try {
            // Preprocess the expression to handle ^, sin, and cos
            expression = preprocessExpression(expression);

            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initSafeStandardObjects();
            return (double) context.evaluateString(scriptable, expression, "Javascript", 1, null);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating expression", e);
        } finally {
            Context.exit(); // Ensure we exit the context
        }
    }

    private String preprocessExpression(String expression) {
        // Replace '^' with 'Math.pow'
        expression = expression.replaceAll("([0-9]+(\\.[0-9]+)?)\\s*\\^\\s*([0-9]+(\\.[0-9]+)?)", "Math.pow($1, $3)");

        // Handle sin, cos, tan: Assuming the format is "sin(x)", "cos(x)", "tan(x)"
        expression = expression.replaceAll("sin\\(([^)]+)\\)", "Math.sin($1 * Math.PI / 180)"); // Convert degrees to radians
        expression = expression.replaceAll("cos\\(([^)]+)\\)", "Math.cos($1 * Math.PI / 180)"); // Convert degrees to radians
        expression = expression.replaceAll("tan\\(([^)]+)\\)", "Math.tan($1 * Math.PI / 180)"); // Convert degrees to radians

        // Handle inverse trig functions: asin, acos, atan
        expression = expression.replaceAll("asin\\(([^)]+)\\)", "Math.asin($1) * 180 / Math.PI"); // Result in degrees
        expression = expression.replaceAll("acos\\(([^)]+)\\)", "Math.acos($1) * 180 / Math.PI"); // Result in degrees
        expression = expression.replaceAll("atan\\(([^)]+)\\)", "Math.atan($1) * 180 / Math.PI"); // Result in degrees
        expression = expression.replaceAll("atan2\\(([^,]+),\\s*([^)]+)\\)", "Math.atan2($1, $2) * 180 / Math.PI"); // Result in degrees

        // Handle hypot: hypot(a, b)
        expression = expression.replaceAll("hypot\\(([^,]+),\\s*([^)]+)\\)", "Math.hypot($1, $2)");

        return expression;
    }

    private void saveToHistory(String entry) {
        calcHistory.add(entry);
        calcHistoryPrefsEditor.putString(HISTORY_KEY, String.join(";", calcHistory));
        calcHistoryPrefsEditor.apply();
    }

    private void loadHistory() {
        String historyString = calcHistoryPrefs.getString(HISTORY_KEY, "");
        if (!historyString.isEmpty()) {
            String[] historyArray = historyString.split(";");
            for (String entry : historyArray) {
                calcHistory.add(entry);
            }
        }
    }

    public List<String> getCalcHistory() {
        return calcHistory;
    }
}
