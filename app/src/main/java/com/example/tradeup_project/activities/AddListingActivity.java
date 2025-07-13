package com.example.tradeup_project.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tradeup_project.R;

public class AddListingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        // TODO: Implement add listing functionality
        setTitle("Add Listing");

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}