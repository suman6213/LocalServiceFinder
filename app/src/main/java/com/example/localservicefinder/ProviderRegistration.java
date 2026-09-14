package com.example.localservicefinder;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProviderRegistration extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker centerMarker;
    private ViewFlipper viewFlipper;
    private LinearLayout stepperContainer;
    private ImageView btnBack, ivProfileImage;
    private RelativeLayout rlProfileImageContainer;
    private AutoCompleteTextView spinnerCategory;
    private TextView step1Circle, step2Circle, step3Circle, step4Circle, PvLogin;
    private MaterialButton btnNextStep1, btnNextStep2, btnNextStep3, btnRegisterProvider, btnGoToLogin, btnChooseLocation;

    // Step 1 Layout Containers & Inputs
    private TextInputLayout tilFullName, tilBusinessName, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private TextInputEditText etFullName, etBusinessName, etEmail, etPhone, etPassword, etConfirmPassword;

    // Step 2 Layout Containers & Inputs
    private TextInputLayout tilCategory, tilExperience, tilDescription;
    private TextInputEditText etExperience, etDescription;

    // Step 3 Layout Containers & Inputs
    private TextInputLayout tilAddressLocation, tilServiceAreaLocation;
    private TextInputEditText etAddressLocation, etServiceAreaLocation;

    // Saved Coordinates for Database Insertion
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;

    // Image Picker State
    private Uri selectedImageUri = null;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_registration);

        initViews();
        setupImagePicker();
        MapCallBack();
        setupClickListeners();
        setupCategoryDropdown();
        setTextWatchers();

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

    private void initViews() {
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
        btnChooseLocation = findViewById(R.id.btnChooseLocation);

        // Step 4 Views
        ivProfileImage = findViewById(R.id.ivProfileImage);
        if (ivProfileImage != null && ivProfileImage.getParent() instanceof RelativeLayout) {
            rlProfileImageContainer = (RelativeLayout) ivProfileImage.getParent();
        }

        // Step 1 EditText Views
        etFullName = findViewById(R.id.etProviderFullName);
        etBusinessName = findViewById(R.id.etBusinessName);
        etEmail = findViewById(R.id.etProviderEmail);
        etPhone = findViewById(R.id.etProviderPhone);
        etPassword = findViewById(R.id.etProviderPassword);
        etConfirmPassword = findViewById(R.id.etProviderConfirmPassword);

        // Step 1 Safe TextInputLayout Resolution
        tilFullName = findParentTextInputLayout(etFullName);
        tilBusinessName = findParentTextInputLayout(etBusinessName);
        tilEmail = findParentTextInputLayout(etEmail);
        tilPhone = findParentTextInputLayout(etPhone);
        tilPassword = findParentTextInputLayout(etPassword);
        tilConfirmPassword = findParentTextInputLayout(etConfirmPassword);

        // Step 2 Views
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etExperience = findViewById(R.id.etExperience);
        etDescription = findViewById(R.id.etDescription);

        tilCategory = findParentTextInputLayout(spinnerCategory);
        tilExperience = findParentTextInputLayout(etExperience);
        tilDescription = findParentTextInputLayout(etDescription);

        // Step 3 Views
        etAddressLocation = findViewById(R.id.etAddressLocation);
        etServiceAreaLocation = findViewById(R.id.etServiceAreaLocation);

        tilAddressLocation = findParentTextInputLayout(etAddressLocation);
        tilServiceAreaLocation = findParentTextInputLayout(etServiceAreaLocation);
    }

    /**
     * Safely traverses up the view hierarchy to locate the parent TextInputLayout,
     * preventing ClassCastExceptions if layout structure varies.
     */
    private TextInputLayout findParentTextInputLayout(View view) {
        if (view == null) return null;
        View parent = (View) view.getParent();
        while (parent != null) {
            if (parent instanceof TextInputLayout) {
                return (TextInputLayout) parent;
            }
            if (parent.getParent() instanceof View) {
                parent = (View) parent.getParent();
            } else {
                break;
            }
        }
        return null;
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null && ivProfileImage != null) {
                            // Clear background, padding, and vector tint
                            ivProfileImage.setBackgroundResource(0);
                            ivProfileImage.setPadding(0, 0, 0, 0);
                            ivProfileImage.setImageTintList(null);

                            // Load with circleCrop using Glide
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .circleCrop()
                                    .into(ivProfileImage);
                        }
                    }
                }
        );
    }

    private void MapCallBack() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupCategoryDropdown() {
        if (spinnerCategory == null) return;

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
        spinnerCategory.setFocusable(false);
        spinnerCategory.setFocusableInTouchMode(false);
        spinnerCategory.setInputType(android.text.InputType.TYPE_NULL);
    }

    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> handlePreviousStep());
        }

        if (btnNextStep1 != null) {
            btnNextStep1.setOnClickListener(v -> {
                if (validateStep1()) {
                    goToStep(1);
                }
            });
        }

        if (btnNextStep2 != null) {
            btnNextStep2.setOnClickListener(v -> {
                if (validateStep2()) {
                    goToStep(2);
                }
            });
        }

        if (btnNextStep3 != null) {
            btnNextStep3.setOnClickListener(v -> {
                if (validateStep3()) {
                    goToStep(3);
                }
            });
        }

        // Map Location Selection Button Listener
        if (btnChooseLocation != null) {
            btnChooseLocation.setOnClickListener(v -> {
                LatLng targetPosition = null;

                if (centerMarker != null) {
                    targetPosition = centerMarker.getPosition();
                } else if (mMap != null) {
                    targetPosition = mMap.getCameraPosition().target;
                }

                if (targetPosition != null) {
                    selectedLatitude = targetPosition.latitude;
                    selectedLongitude = targetPosition.longitude;

                    String selectedAddress = getAddressFromLatLng(targetPosition);

                    if (etServiceAreaLocation != null) {
                        etServiceAreaLocation.setText(selectedAddress);
                    }

                    if (tilServiceAreaLocation != null) {
                        tilServiceAreaLocation.setError(null);
                        tilServiceAreaLocation.setErrorEnabled(false);
                    }

                    Toast.makeText(this, "Service area set: " + selectedAddress, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Map is not ready yet", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (rlProfileImageContainer != null) {
            rlProfileImageContainer.setOnClickListener(v -> openGallery());
        }

        if (btnRegisterProvider != null) {
            btnRegisterProvider.setOnClickListener(v -> {
                if (validateStep4()) {
                    performFinalRegistration();
                }
            });
        }

        if (btnGoToLogin != null) {
            btnGoToLogin.setOnClickListener(v -> finish());
        }

        if (PvLogin != null) {
            PvLogin.setOnClickListener(v -> {
                Intent intent = new Intent(ProviderRegistration.this, ProviderLogin.class);
                startActivity(intent);
                finish();
            });
        }
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);

                StringBuilder areaName = new StringBuilder();

                // 1. Try Specific Local Area / Tole / Neighborhood (e.g., Baneshwor, Thimi, Radhe Radhe)
                if (address.getSubLocality() != null && !address.getSubLocality().isEmpty()) {
                    areaName.append(address.getSubLocality());
                }
                // 2. Try Street or Marg Name (e.g., Lekhnath Marg)
                else if (address.getThoroughfare() != null && !address.getThoroughfare().isEmpty()) {
                    areaName.append(address.getThoroughfare());
                }
                // 3. Try Point of Interest / Landmark (e.g., Dharahara)
                else if (address.getFeatureName() != null && !address.getFeatureName().isEmpty()) {
                    areaName.append(address.getFeatureName());
                }

                // Append City / Municipality (e.g., Kathmandu, Lalitpur, Bhaktapur)
                String city = address.getLocality();
                if (city == null || city.isEmpty()) {
                    city = address.getSubAdminArea();
                }

                if (city != null && !city.isEmpty()) {
                    if (areaName.length() > 0) {
                        // Check if city isn't already duplicated in the subLocality
                        if (!areaName.toString().toLowerCase().contains(city.toLowerCase())) {
                            areaName.append(", ").append(city);
                        }
                    } else {
                        areaName.append(city);
                    }
                }

                // Return built neighborhood string, or fallback to line 0 if empty
                String result = areaName.toString().trim();
                if (!result.isEmpty()) {
                    return result;
                } else if (address.getAddressLine(0) != null) {
                    return address.getAddressLine(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Fallback string if geocoding is offline or lookup returns nothing
        return String.format(Locale.getDefault(), "Lat: %.4f, Lng: %.4f", latLng.latitude, latLng.longitude);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(galleryIntent);
    }

    private boolean validateStep1() {
        ValidationManager validator = ValidationManager.getInstance();

        boolean isNameValid = validator.checkEmpty(tilFullName);
        boolean isBusinessValid = validator.checkEmpty(tilBusinessName);
        boolean isEmailValid = validator.checkEmail(tilEmail);
        boolean isPhoneValid = validator.checkPhoneNumber(tilPhone);
        boolean isPasswordValid = validator.checkEmpty(tilPassword);
        boolean isConfirmPasswordValid = validator.checkEmpty(tilConfirmPassword)
                && validator.matchPassword(tilPassword, tilConfirmPassword);

        return isNameValid && isBusinessValid && isEmailValid && isPhoneValid && isPasswordValid && isConfirmPasswordValid;
    }

    private boolean validateStep2() {
        ValidationManager validator = ValidationManager.getInstance();

        boolean isCategoryValid = validator.checkEmpty(tilCategory);
        boolean isExperienceValid = validator.checkEmpty(tilExperience);
        boolean isDescriptionValid = validator.checkEmpty(tilDescription);

        return isCategoryValid && isExperienceValid && isDescriptionValid;
    }

    private boolean validateStep3() {
        ValidationManager validator = ValidationManager.getInstance();

        boolean isAddressValid = validator.checkEmpty(tilAddressLocation);
        boolean isServiceAreaValid = validator.checkEmpty(tilServiceAreaLocation);

        return isAddressValid && isServiceAreaValid;
    }

    private boolean validateStep4() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a profile image to continue", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performFinalRegistration() {
        Toast.makeText(this, "Registration Submitted!", Toast.LENGTH_SHORT).show();
        goToStep(4);
    }

    private void setTextWatchers() {
        ValidationManager validator = ValidationManager.getInstance();

        addClearErrorWatcher(tilFullName, validator);
        addClearErrorWatcher(tilBusinessName, validator);
        addClearErrorWatcher(tilEmail, validator);
        addClearErrorWatcher(tilPhone, validator);
        addClearErrorWatcher(tilPassword, validator);
        addClearErrorWatcher(tilCategory, validator);
        addClearErrorWatcher(tilExperience, validator);
        addClearErrorWatcher(tilDescription, validator);
        addClearErrorWatcher(tilAddressLocation, validator);
        addClearErrorWatcher(tilServiceAreaLocation, validator);

        if (tilConfirmPassword != null && tilConfirmPassword.getEditText() != null) {
            tilConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validator.clearError(tilConfirmPassword);

                    String password = (tilPassword != null && tilPassword.getEditText() != null)
                            ? tilPassword.getEditText().getText().toString() : "";
                    String confirmPassword = s.toString();

                    if (!confirmPassword.isEmpty() && !confirmPassword.equals(password)) {
                        tilConfirmPassword.setError("Passwords do not match");
                    } else {
                        tilConfirmPassword.setError(null);
                        tilConfirmPassword.setErrorEnabled(false);
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

    private void goToStep(int stepIndex) {
        if (viewFlipper != null) {
            viewFlipper.setDisplayedChild(stepIndex);
            updateStepperUI(stepIndex);
        }
    }

    private void handlePreviousStep() {
        if (viewFlipper == null) {
            finish();
            return;
        }

        int currentChild = viewFlipper.getDisplayedChild();
        if (currentChild > 0 && currentChild < 4) {
            goToStep(currentChild - 1);
        } else {
            finish();
        }
    }

    private void updateStepperUI(int stepIndex) {
        if (stepperContainer == null) return;

        if (stepIndex == 4) {
            stepperContainer.setVisibility(View.GONE);
            return;
        }

        stepperContainer.setVisibility(View.VISIBLE);

        resetCircle(step1Circle);
        resetCircle(step2Circle);
        resetCircle(step3Circle);
        resetCircle(step4Circle);

        if (stepIndex >= 0) markActive(step1Circle);
        if (stepIndex >= 1) markActive(step2Circle);
        if (stepIndex >= 2) markActive(step3Circle);
        if (stepIndex >= 3) markActive(step4Circle);
    }

    private void resetCircle(TextView circle) {
        if (circle != null) {
            circle.setBackgroundResource(R.drawable.empty_circle);
            circle.setTextColor(Color.parseColor("#6B7280"));
        }
    }

    private void markActive(TextView circle) {
        if (circle != null) {
            circle.setBackgroundResource(R.drawable.bg_provider_icon);
            circle.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng initialLocation = new LatLng(27.7008, 85.3001); // Kathmandu default
        centerMarker = mMap.addMarker(new MarkerOptions()
                .position(initialLocation)
                .title("Service Location"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 14f));

        // Updates marker dynamically while dragging map
        mMap.setOnCameraMoveListener(() -> {
            if (centerMarker != null && mMap != null) {
                centerMarker.setPosition(mMap.getCameraPosition().target);
            }
        });

        // Ensures target position locks precisely when motion stops
        mMap.setOnCameraIdleListener(() -> {
            if (centerMarker != null && mMap != null) {
                centerMarker.setPosition(mMap.getCameraPosition().target);
            }
        });
    }
}