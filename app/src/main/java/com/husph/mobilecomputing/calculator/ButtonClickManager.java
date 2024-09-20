package com.husph.mobilecomputing.calculator;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.husph.mobilecomputing.R;

public class ButtonClickManager implements View.OnClickListener{

    StringBuilder input;
    TextView resultTextView;

    public ButtonClickManager(TextView resultTextView) {
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
            selectedButtonId == R.id.btn_num9 )
        {

            Button numberButton = (Button) view;

            String numberText = numberButton.getText().toString();

            if(input.length() != 0 && input.charAt(0) == '0') {
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
            if (input.length() <= 1) {
                input.setLength(0);
                resultTextView.setText(input.append("0"));
                return;
            }
            resultTextView.setText(input.deleteCharAt(input.length() - 1).toString());
        } else if (selectedButtonId == R.id.btn_clearAll) {
            resultTextView.setText(input.append(" / "));
        } else if (selectedButtonId == R.id.btn_percent) {
            resultTextView.setText(input.append(" / "));
        } else if (selectedButtonId == R.id.btn_doubleZero) {
            resultTextView.setText(input.append(" / "));
        } else if (selectedButtonId == R.id.btn_equal) {
            calculateResult();
        }


    }

    private void calculateResult() {
        try {
            String result = String.valueOf(eval(input.toString()));
            input.setLength(0);
            input.append(result);
            resultTextView.setText(input);
        }
        catch (Exception e) {
            input.setLength(0);
            resultTextView.setText("Syntax Error");
        }
    }

    private double eval(String expression) {
        try {
            double result = MathEvaluator.evaluateExpression(expression);
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error evaluating expression", e);
        }

    }
}
