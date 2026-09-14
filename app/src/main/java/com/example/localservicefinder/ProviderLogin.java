package com.example.localservicefinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

public class ProviderLogin extends AppCompatActivity {
    private TextInputLayout etProviderEmail, etProviderPassword;
    private Button prBtnLogin;
    private ImageView btnBack;
    private TextView PvRegister;

    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.provider_login);

        initviews();
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
    private void initviews(){
        etProviderEmail = findViewById(R.id.etProviderEmail);
        etProviderPassword = findViewById(R.id.etProviderPassword);
        prBtnLogin = findViewById(R.id.prBtnLogin);
        btnBack = findViewById(R.id.btnBack);
        PvRegister = findViewById(R.id.tvRegister);
    }

    private void setupClickListeners(){
        PvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProviderLogin.this, ProviderRegistration.class);
                startActivity(intent);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBackToRoleSelection();
            }
        });

        prBtnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        ValidationManager validator = ValidationManager.getInstance();

        // Single-ampersand (&) ensures both evaluations run so both fields show errors if empty
        boolean isEmailValid = validator.checkEmail(etProviderEmail);
        boolean isPasswordValid = validator.checkEmpty(etProviderPassword);

        if (isEmailValid && isPasswordValid) {
            String email = etProviderEmail.getEditText().getText().toString().trim();
            String password = etProviderPassword.getEditText().getText().toString();

            // Proceed with authentication/SQLite login verification
            Toast.makeText(ProviderLogin.this, "Logging in...", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTextWatchers() {
        ValidationManager validator = ValidationManager.getInstance();

        if (etProviderEmail.getEditText() != null) {
            etProviderEmail.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validator.clearError(etProviderEmail);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        if (etProviderPassword.getEditText() != null) {
            etProviderPassword.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validator.clearError(etProviderPassword);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
    private void navigateBackToRoleSelection() {
        finish(); // Closes registration activity and seamlessly returns to MainActivity
    }
}
