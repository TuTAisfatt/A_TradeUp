package com.example.tradeup_project.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tradeup_project.R;
import com.example.tradeup_project.models.Listing;
import com.example.tradeup_project.models.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddListingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST = 2;
    private static final int MAX_IMAGES = 10;

    // UI Elements
    private TextInputEditText titleInput, descriptionInput, priceInput;
    private TextInputLayout titleLayout, descriptionLayout, priceLayout;
    private Spinner categorySpinner, conditionSpinner;
    private CheckBox negotiableCheckBox;
    private MaterialButton addPhotosButton, locationButton, submitButton;
    private RecyclerView photosRecyclerView;
    private LinearLayout locationContainer;
    private TextView locationText;
    private ProgressBar progressBar;

    // Data
    private List<Uri> selectedImageUris = new ArrayList<>();
    private Location currentLocation;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        // Set up toolbar
        setTitle("Create Listing");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initializeViews();

        // Set up spinners
        setupSpinners();

        // Set up listeners
        setupListeners();
    }

    private void initializeViews() {
        // Text inputs
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        priceInput = findViewById(R.id.priceInput);

        titleLayout = findViewById(R.id.titleLayout);
        descriptionLayout = findViewById(R.id.descriptionLayout);
        priceLayout = findViewById(R.id.priceLayout);

        // Spinners
        categorySpinner = findViewById(R.id.categorySpinner);
        conditionSpinner = findViewById(R.id.conditionSpinner);

        // Other inputs
        negotiableCheckBox = findViewById(R.id.negotiableCheckBox);

        // Buttons
        addPhotosButton = findViewById(R.id.addPhotosButton);
        locationButton = findViewById(R.id.locationButton);
        submitButton = findViewById(R.id.submitButton);

        // Location
        locationContainer = findViewById(R.id.locationContainer);
        locationText = findViewById(R.id.locationText);

        // RecyclerView
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        photosRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Progress
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinners() {
        // Category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Condition spinner
        ArrayAdapter<CharSequence> conditionAdapter = ArrayAdapter.createFromResource(
                this, R.array.conditions, android.R.layout.simple_spinner_item);
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        conditionSpinner.setAdapter(conditionAdapter);
    }

    private void setupListeners() {
        // Text change listeners for validation
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateForm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        titleInput.addTextChangedListener(textWatcher);
        descriptionInput.addTextChangedListener(textWatcher);
        priceInput.addTextChangedListener(textWatcher);

        // Button listeners
        addPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImages();
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLocationPermission();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitListing();
            }
        });
    }

    private void validateForm() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();

        boolean isValid = !title.isEmpty() &&
                !description.isEmpty() &&
                !price.isEmpty() &&
                selectedImageUris.size() > 0 &&
                currentLocation != null;

        submitButton.setEnabled(isValid);
    }

    private void selectImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUris.clear();

            if (data.getClipData() != null) {
                int count = Math.min(data.getClipData().getItemCount(), MAX_IMAGES);
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                selectedImageUris.add(data.getData());
            }

            updatePhotosDisplay();
            validateForm();
        }
    }

    private void updatePhotosDisplay() {
        addPhotosButton.setText(String.format("Photos (%d/%d)", selectedImageUris.size(), MAX_IMAGES));
        // TODO: Update RecyclerView with image adapter
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        // TODO: Implement location fetching
        // For now, use dummy location
        currentLocation = new Location(0, 0, "Current Location");
        locationContainer.setVisibility(View.VISIBLE);
        locationText.setText(currentLocation.toString());
        validateForm();
    }

    private void submitListing() {
        if (!validateInputs()) return;

        showProgress(true);
        submitButton.setEnabled(false);

        // Create listing ID
        String listingId = UUID.randomUUID().toString();

        // Upload images first
        uploadImages(listingId);
    }

    private boolean validateInputs() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String priceStr = priceInput.getText().toString().trim();

        if (title.isEmpty()) {
            titleLayout.setError("Title is required");
            return false;
        }

        if (description.isEmpty()) {
            descriptionLayout.setError("Description is required");
            return false;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                priceLayout.setError("Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            priceLayout.setError("Invalid price");
            return false;
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(this, "Please add at least one photo", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Please add location", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void uploadImages(String listingId) {
        List<String> uploadedUrls = new ArrayList<>();
        final int totalImages = selectedImageUris.size();

        for (int i = 0; i < selectedImageUris.size(); i++) {
            Uri imageUri = selectedImageUris.get(i);
            String fileName = listingId + "_" + i + ".jpg";
            StorageReference imageRef = mStorage.child("listings").child(fileName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    uploadedUrls.add(downloadUri.toString());

                                    if (uploadedUrls.size() == totalImages) {
                                        // All images uploaded, create listing
                                        createListing(listingId, uploadedUrls);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        showProgress(false);
                        submitButton.setEnabled(true);
                        Toast.makeText(AddListingActivity.this,
                                "Failed to upload images: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void createListing(String listingId, List<String> imageUrls) {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        double price = Double.parseDouble(priceInput.getText().toString().trim());
        String category = categorySpinner.getSelectedItem().toString();
        String condition = conditionSpinner.getSelectedItem().toString();
        boolean isNegotiable = negotiableCheckBox.isChecked();

        Listing listing = new Listing(listingId, mAuth.getCurrentUser().getUid(),
                title, description, price, category, condition);
        listing.setNegotiable(isNegotiable);
        listing.setLocation(currentLocation);
        listing.setImageUrls(imageUrls);

        mDatabase.child("listings").child(listingId).setValue(listing.toMap())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showProgress(false);

                        if (task.isSuccessful()) {
                            Toast.makeText(AddListingActivity.this,
                                    "Listing created successfully!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            submitButton.setEnabled(true);
                            Toast.makeText(AddListingActivity.this,
                                    "Failed to create listing: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_preview) {
            // TODO: Show preview
            Toast.makeText(this, "Preview", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}