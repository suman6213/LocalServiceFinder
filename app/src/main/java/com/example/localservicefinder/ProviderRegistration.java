package com.example.localservicefinder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;

public class ProviderRegistration extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    private Marker centerMarker;
    private ViewFlipper viewFlipper;
    private LinearLayout stepperContainer;
    private ImageView btnBack;
    private AutoCompleteTextView spinnerCategory;
    private TextView step1Circle, step2Circle, step3Circle, step4Circle, PvLogin;
    private MaterialButton btnNextStep1, btnNextStep2, btnNextStep3, btnRegisterProvider, btnGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.provider_registration);

        initViews(savedInstanceState);
        MapCallBack();
        setupClickListeners();
        setupCategoryDropdown();

        // Initialize stepper UI for Step 1 on screen launch
        updateStepperUI(0);

        // Standard Device Back / Swipe Gesture Handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handlePreviousStep();
            }
        });
    }

    private void initViews(Bundle savedInstanceState) {
        spinnerCategory = findViewById(R.id.spinnerCategory);
        viewFlipper = findViewById(R.id.viewFlipper);
        stepperContainer = findViewById(R.id.stepperContainer);
        btnBack = findViewById(R.id.btnBack);

        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        step3Circle = findViewById(R.id.step3Circle);
        step4Circle = findViewById(R.id.step4Circle);
        PvLogin = findViewById(R.id.tvLogin);

        btnNextStep1 = findViewById(R.id.btnNextStep1);
        btnNextStep2 = findViewById(R.id.btnNextStep2);
        btnNextStep3 = findViewById(R.id.btnNextStep3);
        btnRegisterProvider = findViewById(R.id.btnRegisterProvider);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);
    }

    private void MapCallBack(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);
    }

    private void setupCategoryDropdown() {
        String[] categories = new String[]{
                "Plumbing & Pipefitting",
                "Electrical Work",
                "AC & Appliance Repair",
                "House Cleaning & Maid Service",
                "Painting & Renovation",
                "Carpentry & Woodwork",
                "Pest Control",
                "Gardening & Landscaping"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories
        );

        spinnerCategory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Toolbar Back Arrow
        btnBack.setOnClickListener(v -> handlePreviousStep());

        // Step Navigation Buttons
        btnNextStep1.setOnClickListener(v -> goToStep(1)); // Step 1 -> Step 2
        btnNextStep2.setOnClickListener(v -> goToStep(2)); // Step 2 -> Step 3
        btnNextStep3.setOnClickListener(v -> goToStep(3)); // Step 3 -> Step 4
        btnRegisterProvider.setOnClickListener(v -> goToStep(4)); // Step 4 -> Success Screen

        // Completion Finish
        btnGoToLogin.setOnClickListener(v -> finish());

        PvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProviderRegistration.this, ProviderLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void goToStep(int stepIndex) {
        viewFlipper.setDisplayedChild(stepIndex);
        updateStepperUI(stepIndex);
    }

    private void handlePreviousStep() {
        int currentChild = viewFlipper.getDisplayedChild();
        if (currentChild > 0 && currentChild < 4) {
            goToStep(currentChild - 1);
        } else {
            finish(); // Close activity if on first step or final submission screen
        }
    }

    private void updateStepperUI(int stepIndex) {
        // Hide stepper header only on step 4 (Success screen)
        if (stepIndex == 4) {
            stepperContainer.setVisibility(View.GONE);
            return;
        }

        stepperContainer.setVisibility(View.VISIBLE);

        // 1. Reset all circles to empty/inactive state first
        resetCircle(step1Circle);
        resetCircle(step2Circle);
        resetCircle(step3Circle);
        resetCircle(step4Circle);

        // 2. Keep current step AND all previous steps active
        if (stepIndex >= 0) markActive(step1Circle);
        if (stepIndex >= 1) markActive(step2Circle);
        if (stepIndex >= 2) markActive(step3Circle);
        if (stepIndex >= 3) markActive(step4Circle);
    }

    private void resetCircle(TextView circle) {
        circle.setBackgroundResource(R.drawable.empty_circle);
        circle.setTextColor(Color.parseColor("#6B7280"));
    }

    private void markActive(TextView circle) {
        circle.setBackgroundResource(R.drawable.bg_provider_icon);
        circle.setTextColor(Color.WHITE);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Position camera over Sydney
        LatLng location = new LatLng(27.7008, 85.3001);
        centerMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title("Service Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f));
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (centerMarker != null){
                    LatLng centerLatlang = mMap.getCameraPosition().target;
                    centerMarker.setPosition(centerLatlang);
                }
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (centerMarker != null){
                    LatLng finalCenterLatlang = mMap.getCameraPosition().target;
                    double latitude = finalCenterLatlang.latitude;
                    double longitude = finalCenterLatlang.longitude;

                    centerMarker.setPosition(finalCenterLatlang);
                }
            }
        });
    }

}