package com.husph.mobilecomputing.calculator;

import java.util.Stack;

public class MathEvaluator {

    public static double evaluateExpression(String expression) {
        char[] tokens = expression.toCharArray();

        // Stack for numbers (doubles now)
        Stack<Double> values = new Stack<>();

        // Stack for operators
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            // Current token is a whitespace, skip it
            if (tokens[i] == ' ')
                continue;

            // Current token is a number or decimal point, push it to the stack
            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                StringBuilder sbuf = new StringBuilder();

                // Handle numbers and decimals
                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.'))
                    sbuf.append(tokens[i++]);
                values.push(Double.parseDouble(sbuf.toString()));

                // Right now, the i is one step ahead, so decrease it
                i--;
            }

            // Current token is an opening brace, push it to 'operators'
            else if (tokens[i] == '(')
                operators.push(tokens[i]);

                // Closing brace encountered, solve the sub-expression
            else if (tokens[i] == ')') {
                while (operators.peek() != '(')
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                operators.pop();
            }

            // Current token is an operator
            else if (tokens[i] == '+' || tokens[i] == '-' ||
                    tokens[i] == '*' || tokens[i] == '/') {
                // While top of 'operators' has same or greater precedence to current token
                // apply the operator to the top two elements in 'values' stack
                while (!operators.isEmpty() && hasPrecedence(tokens[i], operators.peek()))
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));

                // Push current token to 'operators'
                operators.push(tokens[i]);
            }
        }

        // Entire expression has been parsed at this point, apply remaining operators to remaining values
        while (!operators.isEmpty())
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));

        // Top of 'values' contains the result
        return values.pop();
    }

    // Method to check operator precedence
    public static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')')
            return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'))
            return false;
        else
            return true;
    }

    // Method to apply an operator on two operands
    public static double applyOperator(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new UnsupportedOperationException("Cannot divide by zero");
                return a / b;
        }
        return 0;
    }
}
