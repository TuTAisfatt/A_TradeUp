package com.example.tradeup_project.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tradeup_project.R;
import com.example.tradeup_project.models.Listing;

public class EditListingActivity extends AppCompatActivity {

    private Listing listing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        // Get listing from intent
        listing = (Listing) getIntent().getSerializableExtra("listing");

        setTitle("Edit Listing");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // TODO: Implement edit listing functionality
        // Similar to AddListingActivity but pre-populate fields
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}