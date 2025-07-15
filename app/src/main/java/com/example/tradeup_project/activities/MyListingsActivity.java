package com.example.tradeup_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tradeup_project.R;
import com.example.tradeup_project.adapters.ListingAdapter;
import com.example.tradeup_project.models.Listing;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyListingsActivity extends AppCompatActivity implements ListingAdapter.OnListingClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private TextView emptyStateText;
    private FloatingActionButton fabAddListing;

    private ListingAdapter adapter;
    private List<Listing> allListings = new ArrayList<>();
    private List<Listing> displayedListings = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ValueEventListener listingsListener;

    private String currentFilter = "all"; // all, active, sold, paused

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_listings);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Set up toolbar
        setTitle("My Listings");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
        initializeViews();

        // Set up tabs
        setupTabs();

        // Set up RecyclerView
        setupRecyclerView();

        // Load listings
        loadUserListings();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        emptyStateText = findViewById(R.id.emptyStateText);
        fabAddListing = findViewById(R.id.fabAddListing);

        // Set up listeners
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUserListings();
            }
        });

        fabAddListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyListingsActivity.this, AddListingActivity.class));
            }
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Active"));
        tabLayout.addTab(tabLayout.newTab().setText("Sold"));
        tabLayout.addTab(tabLayout.newTab().setText("Paused"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentFilter = "all";
                        break;
                    case 1:
                        currentFilter = "Available";
                        break;
                    case 2:
                        currentFilter = "Sold";
                        break;
                    case 3:
                        currentFilter = "Paused";
                        break;
                }
                filterListings();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new ListingAdapter(displayedListings, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadUserListings() {
        if (mAuth.getCurrentUser() == null) return;

        showLoading(true);

        Query query = mDatabase.child("listings")
                .orderByChild("userId")
                .equalTo(mAuth.getCurrentUser().getUid());

        listingsListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allListings.clear();

                for (DataSnapshot listingSnapshot : snapshot.getChildren()) {
                    Listing listing = listingSnapshot.getValue(Listing.class);
                    if (listing != null) {
                        allListings.add(listing);
                    }
                }

                filterListings();
                showLoading(false);
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MyListingsActivity.this,
                        "Error loading listings: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterListings() {
        displayedListings.clear();

        if (currentFilter.equals("all")) {
            displayedListings.addAll(allListings);
        } else {
            for (Listing listing : allListings) {
                if (listing.getStatus().equals(currentFilter)) {
                    displayedListings.add(listing);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (displayedListings.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            if (currentFilter.equals("all")) {
                emptyStateText.setText("You haven't created any listings yet");
            } else {
                emptyStateText.setText("No " + currentFilter.toLowerCase() + " listings");
            }
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onListingClick(Listing listing) {
        Intent intent = new Intent(this, ListingDetailActivity.class);
        intent.putExtra("listing", listing);
        startActivity(intent);
    }

    @Override
    public void onEditClick(Listing listing) {
        Intent intent = new Intent(this, EditListingActivity.class);
        intent.putExtra("listing", listing);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Listing listing) {
        // TODO: Show confirmation dialog and delete listing
        Toast.makeText(this, "Delete: " + listing.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChange(Listing listing, String newStatus) {
        // Update listing status in database
        mDatabase.child("listings")
                .child(listing.getListingId())
                .child("status")
                .setValue(newStatus)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this,
                                "Status updated to " + newStatus,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Failed to update status",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listingsListener != null) {
            mDatabase.removeEventListener(listingsListener);
        }
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