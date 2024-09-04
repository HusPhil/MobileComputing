package com.husph.mobilecomputing.utils;

import android.widget.Toast;

import org.jetbrains.annotations.Contract;

import java.security.SecureRandom;

public class FormValidation {

    public enum WarningMessage {
        INPUT_NULL("Please fill up required fields."),
        INVALID_EMAIL_WARNING("Invalid email, please try another one."),
        INVALID_PASSWORD_WARNING("Password must contain 8 or more characters."),
        INVALID_CONFIRM_PASSWORD("Passwords do not match, try again");

        private final String message;

        WarningMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum FormValidationResult {
        INPUT_NULL,
        INVALID_EMAIL,
        INVALID_CONFIRM_PASSWORD,
        INVALID_PASSWORD,
        FORM_VALID
    }


    private static final String emailPatternRegEx = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(emailPatternRegEx);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 8;
    }

    public static FormValidationResult isRegisterFormValid(String email, String password, String confirm_password) {

        if(email == null || password == null || confirm_password == null) return FormValidationResult.INPUT_NULL;

        if(!isValidEmail(email)) return FormValidationResult.INVALID_EMAIL;

        if(!isValidPassword(password)) return FormValidationResult.INVALID_PASSWORD;

        if(!password.equals(confirm_password)) return FormValidationResult.INVALID_CONFIRM_PASSWORD;

        return FormValidationResult.FORM_VALID;
    }

    public static FormValidationResult isLoginFormValid(String email, String password) {

        if(email == null || password == null) return FormValidationResult.INPUT_NULL;

        if(!isValidEmail(email)) return FormValidationResult.INVALID_EMAIL;

        if(!isValidPassword(password)) return FormValidationResult.INVALID_PASSWORD;

        return FormValidationResult.FORM_VALID;
    }
}
