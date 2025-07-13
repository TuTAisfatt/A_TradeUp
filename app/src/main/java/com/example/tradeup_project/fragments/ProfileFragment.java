package com.example.tradeup_project.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tradeup_project.MainActivity;
import com.example.tradeup_project.R;
import com.example.tradeup_project.activities.EditProfileActivity;
import com.example.tradeup_project.activities.MyListingsActivity;
import com.example.tradeup_project.activities.SettingsActivity;
import com.example.tradeup_project.activities.TransactionHistoryActivity;
import com.example.tradeup_project.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ImageView profileImage;
    private TextView usernameText, emailText, ratingText, transactionsText;
    private LinearLayout myListingsLayout, transactionHistoryLayout, settingsLayout;
    private Button editProfileButton, logoutButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
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

        // Load user data
        loadUserProfile();
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        usernameText = view.findViewById(R.id.usernameText);
        emailText = view.findViewById(R.id.emailText);
        ratingText = view.findViewById(R.id.ratingText);
        transactionsText = view.findViewById(R.id.transactionsText);

        myListingsLayout = view.findViewById(R.id.myListingsLayout);
        transactionHistoryLayout = view.findViewById(R.id.transactionHistoryLayout);
        settingsLayout = view.findViewById(R.id.settingsLayout);

        editProfileButton = view.findViewById(R.id.editProfileButton);
        logoutButton = view.findViewById(R.id.logoutButton);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        myListingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyListingsActivity.class));
            }
        });

        transactionHistoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TransactionHistoryActivity.class));
            }
        });

        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });

        // Logout button - implements FR-1.1.5
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void loadUserProfile() {
        if (currentUser == null) return;

        showLoading(true);

        mDatabase.child("users").child(currentUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showLoading(false);

                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                updateUI(user);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showLoading(false);
                        Toast.makeText(getContext(),
                                "Error loading profile: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(User user) {
        usernameText.setText(user.getDisplayName());
        emailText.setText(user.getEmail());
        ratingText.setText(String.format("%.1f ⭐", user.getRating()));
        transactionsText.setText(String.format("%d transactions", user.getTotalTransactions()));

        // TODO: Load profile image with Glide
        // Glide.with(this).load(user.getProfilePictureUrl()).into(profileImage);
    }

    private void logoutUser() {
        mAuth.signOut();

        // Navigate back to login
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).logout();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}