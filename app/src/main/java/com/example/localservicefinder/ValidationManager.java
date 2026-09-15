package com.example.localservicefinder;

import android.text.TextUtils;
import android.util.Patterns;
import com.google.android.material.textfield.TextInputLayout;

public class ValidationManager {

    private static ValidationManager instance = null;

    private final String ERR_MSG_CHECK_EMPTY = "Field cannot be empty";
    private final String ERR_MSG_CHECK_PHONE_NUMBER = "Please enter valid 10-digit phone number";
    private final String ERR_MSG_CHECK_EMAIL = "Invalid email address";
    private final String ERR_MSG_CHECK_PASSWORD = "Password must be 6 character long";
    private final String ERR_MSG_MATCH_PASSWORD = "Passwords do not match";

    private ValidationManager() {}

    public static synchronized ValidationManager getInstance() {
        if (instance == null) {
            instance = new ValidationManager();
        }
        return instance;
    }

    // Clears existing error if valid
    public void clearError(TextInputLayout layout) {
        if (layout != null) {
            layout.setError(null);
            layout.setErrorEnabled(false);
        }
    }

    public boolean checkEmpty(TextInputLayout layout) {
        if (layout == null || layout.getEditText() == null) return false;

        String input = layout.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(input)) {
            layout.setError(ERR_MSG_CHECK_EMPTY);
            return false;
        }
        clearError(layout);
        return true;
    }

    public boolean checkPhoneNumber(TextInputLayout layout) {
        if (!checkEmpty(layout)) return false;

        String input = layout.getEditText().getText().toString().trim();
        if (input.length() != 10) {
            layout.setError(ERR_MSG_CHECK_PHONE_NUMBER);
            return false;
        }
        clearError(layout);
        return true;
    }

    public boolean checkEmail(TextInputLayout layout) {
        if (!checkEmpty(layout)) return false;

        String input = layout.getEditText().getText().toString().trim();
        // Native Android email pattern check
        if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            layout.setError(ERR_MSG_CHECK_EMAIL);
            return false;
        }
        clearError(layout);
        return true;
    }

    public boolean checkPassword(TextInputLayout layout) {
        if (layout == null || layout.getEditText() == null) return false;

        String input = layout.getEditText().getText().toString();

        if (TextUtils.isEmpty(input.trim())) {
            layout.setError(ERR_MSG_CHECK_EMPTY);
            return false;
        }

        if (input.length() < 6) {
            layout.setError(ERR_MSG_CHECK_PASSWORD);
            return false;
        }

        clearError(layout);
        return true;
    }

    public boolean matchPassword(TextInputLayout passLayout, TextInputLayout confirmPassLayout) {
        if (!checkEmpty(passLayout) || !checkEmpty(confirmPassLayout)) return false;

        String pass = passLayout.getEditText().getText().toString();
        String confirmPass = confirmPassLayout.getEditText().getText().toString();

        if (pass.length() != 6){
            confirmPassLayout.setError(ERR_MSG_MATCH_PASSWORD);
            return false;
        }

        if (!pass.equals(confirmPass)) {
            confirmPassLayout.setError(ERR_MSG_MATCH_PASSWORD);
            return false;
        }
        clearError(confirmPassLayout);
        return true;
    }
}