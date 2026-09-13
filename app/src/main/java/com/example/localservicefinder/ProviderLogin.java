package com.example.localservicefinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class ProviderLogin extends AppCompatActivity {

    private ImageView btnBack;
    private TextView PvRegister;

    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.provider_login);

        initviews();

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

        // System Back Button / Back Gesture Handler
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateBackToRoleSelection();
            }
        });
    }
    private void initviews(){
        btnBack = findViewById(R.id.btnBack);
        PvRegister = findViewById(R.id.tvRegister);
    }
    private void navigateBackToRoleSelection() {
        finish(); // Closes registration activity and seamlessly returns to MainActivity
    }
}
