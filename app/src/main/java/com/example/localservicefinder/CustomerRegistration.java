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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerRegistration extends AppCompatActivity {

    FirebaseAuth fAuth;
    private TextInputLayout etFullName, etEmail, etPhone, etAddress, etPassword, etConfirmPassword;
    private Button csBtnRegister;
    private ImageView btnBack;
    private TextView csLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_registration);

        initViews();
        setUpClickListeners();
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
        btnBack = findViewById(R.id.btnBack);
        csLogin = findViewById(R.id.tvLogin);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        csBtnRegister = findViewById(R.id.CsBtnRegister);

        fAuth = FirebaseAuth.getInstance();
    }

    private void setUpClickListeners() {
        btnBack.setOnClickListener(v -> navigateBackToRoleSelection());

        csLogin.setOnClickListener(v -> {
            Intent intent = new Intent(CustomerRegistration.this, CustomerLogin.class);
            startActivity(intent);
            finish();
        });

        if (csBtnRegister != null) {
            csBtnRegister.setOnClickListener(v -> performRegistration());
        }
    }

    private void performRegistration() {
        ValidationManager validator = ValidationManager.getInstance();

        // Single-ampersand (&) ensures every evaluation runs so all invalid fields highlight at once
        boolean isNameValid = validator.checkEmpty(etFullName);
        boolean isEmailValid = validator.checkEmail(etEmail);
        boolean isPhoneValid = validator.checkPhoneNumber(etPhone);
        boolean isAddressValid = validator.checkEmpty(etAddress);
        boolean isPasswordValid = validator.checkEmpty(etPassword);
        boolean isConfirmPasswordValid = validator.checkEmpty(etConfirmPassword) & validator.matchPassword(etPassword, etConfirmPassword);

        boolean isFormValid = isNameValid & isEmailValid & isPhoneValid & isAddressValid & isPasswordValid & isConfirmPasswordValid;

        if (!isFormValid) {
            return;
        }

        String name = etFullName.getEditText().getText().toString().trim();
        String email = etEmail.getEditText().getText().toString().trim();
        String phone = etPhone.getEditText().getText().toString().trim();
        String address = etAddress.getEditText().getText().toString().trim();
        String password = etPassword.getEditText().getText().toString();

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CustomerRegistration.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(CustomerRegistration.this, CustomerLogin.class);
                    startActivity(intent);
                    finish();
                }else{
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                    Toast.makeText(CustomerRegistration.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setTextWatchers() {
        ValidationManager validator = ValidationManager.getInstance();

        addClearErrorWatcher(etFullName, validator);
        addClearErrorWatcher(etEmail, validator);
        addClearErrorWatcher(etPhone, validator);
        addClearErrorWatcher(etAddress, validator);
        addClearErrorWatcher(etPassword, validator);

        // Real-time password matching check on Confirm Password field
        if (etConfirmPassword != null && etConfirmPassword.getEditText() != null) {
            etConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validator.clearError(etConfirmPassword);

                    String password = (etPassword != null && etPassword.getEditText() != null)
                            ? etPassword.getEditText().getText().toString() : "";
                    String confirmPassword = s.toString();

                    if (!confirmPassword.isEmpty() && !confirmPassword.equals(password)) {
                        etConfirmPassword.setError("Passwords do not match");
                    } else {
                        etConfirmPassword.setError(null);
                        etConfirmPassword.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void addClearErrorWatcher(TextInputLayout layout, ValidationManager validator) {
        if (layout != null && layout.getEditText() != null) {
            layout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validator.clearError(layout);
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