package com.husph.mobilecomputing.calculator;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.husph.mobilecomputing.R;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class ButtonClickManager implements View.OnClickListener{

    StringBuilder input;
    TextView resultTextView;
    AppCompatActivity activity;

    public ButtonClickManager(AppCompatActivity activity, TextView resultTextView) {
        this.activity = activity;
        this.resultTextView = resultTextView;
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
        } else if (selectedButtonId == R.id.btn_doubleZero) {
            resultTextView.setText(input.append("00"));
        } else if (selectedButtonId == R.id.btn_decimalPoint) {
            resultTextView.setText(input.append("."));
        } else if (selectedButtonId == R.id.btn_equal) {
            calculateResult();
        }


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

            if(result.endsWith(".0")) {
                formattedResult = result.substring(0, result.length() - 2);
            }

            input.setLength(0);
            input.append(formattedResult);

            resultTextView.setText(input);
        }
        catch (Exception e) {
            input.setLength(0);
            resultTextView.setText("Syntax Error");
        }
    }

    private double eval(String expression) {
        try {

            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initSafeStandardObjects();
            return (double) context.evaluateString(scriptable, expression, "Javascript", 1, null);

        } catch (Exception e) {
            throw new RuntimeException("Error evaluating expression", e);
        }

    }
}
