package com.example.tradeup_project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tradeup_project.R;
import com.example.tradeup_project.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private TextView welcomeText, seeAllRecent;
    private RecyclerView categoriesRecyclerView, recentListingsRecyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        // Initialize views
        initializeViews(view);

        // Set up listeners
        setupListeners();

        // Load data
        loadUserData();
        loadCategories();
        loadRecentListings();
    }

    private void initializeViews(View view) {
        welcomeText = view.findViewById(R.id.welcomeText);
        seeAllRecent = view.findViewById(R.id.seeAllRecent);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        recentListingsRecyclerView = view.findViewById(R.id.recentListingsRecyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);
        emptyState = view.findViewById(R.id.emptyState);

        // Set up RecyclerViews
        categoriesRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recentListingsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRecentListings();
            }
        });

        seeAllRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to search fragment
                Toast.makeText(getContext(), "See all listings", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            mDatabase.child("users").child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    welcomeText.setText("Welcome back, " + user.getDisplayName() + "!");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(),
                                    "Error loading user data: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadCategories() {
        // For now, we'll use the array from resources
        String[] categories = getResources().getStringArray(R.array.categories);
        // TODO: Create CategoryAdapter and set it to categoriesRecyclerView
    }

    private void loadRecentListings() {
        showLoading(true);

        mDatabase.child("listings")
                .orderByChild("status")
                .equalTo("Available")
                .limitToLast(10)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showLoading(false);
                        swipeRefresh.setRefreshing(false);

                        if (snapshot.exists()) {
                            // TODO: Parse listings and update adapter
                            emptyState.setVisibility(View.GONE);
                            recentListingsRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            // No listings found
                            emptyState.setVisibility(View.VISIBLE);
                            recentListingsRecyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showLoading(false);
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getContext(),
                                "Error loading listings: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}