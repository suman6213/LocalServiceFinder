package com.example.localservicefinder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class CustomerLogin extends AppCompatActivity {

    private TextInputLayout etEmail, etPassword;
    private Button csBtnLogin;
    private ImageView btnBack;
    private TextView csRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_login);

        initViews();
        setupClickListeners();
        setTextWatchers();

        // System Back Button / Back Gesture Handler
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBackToRoleSelection();
            }
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnBack = findViewById(R.id.btnBack);
        csRegister = findViewById(R.id.tvRegister);
        csBtnLogin = findViewById(R.id.CsBtnLogin);
    }

    private void setupClickListeners() {
        csRegister.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerLogin.this, CustomerRegistration.class);
            startActivity(intent);
            finish();
        });

        btnBack.setOnClickListener(v -> navigateBackToRoleSelection());

        csBtnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        ValidationManager validator = ValidationManager.getInstance();

        // Single-ampersand (&) ensures both evaluations run so both fields show errors if empty
        boolean isEmailValid = validator.checkEmail(etEmail);
        boolean isPasswordValid = validator.checkEmpty(etPassword);

        if (isEmailValid && isPasswordValid) {
            String email = etEmail.getEditText().getText().toString().trim();
            String password = etPassword.getEditText().getText().toString();

            // Proceed with authentication/SQLite login verification
            Toast.makeText(CustomerLogin.this, "Logging in...", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTextWatchers() {
        ValidationManager validator = ValidationManager.getInstance();

        if (etEmail.getEditText() != null) {
            etEmail.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validator.clearError(etEmail);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (etPassword.getEditText() != null) {
            etPassword.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validator.clearError(etPassword);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void navigateBackToRoleSelection() {
        finish();
    }
}