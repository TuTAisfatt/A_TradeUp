package com.example.tradeup_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tradeup_project.activities.AddListingActivity;
import com.example.tradeup_project.activities.LoginActivity;
import com.example.tradeup_project.fragments.HomeFragment;
import com.example.tradeup_project.fragments.MessagesFragment;
import com.example.tradeup_project.fragments.ProfileFragment;
import com.example.tradeup_project.fragments.SearchFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private FirebaseAuth mAuth;

    // Fragment tags
    private static final String TAG_HOME = "home";
    private static final String TAG_SEARCH = "search";
    private static final String TAG_MESSAGES = "messages";
    private static final String TAG_PROFILE = "profile";
    private String currentTag = TAG_HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        initializeViews();

        // Set up toolbar
        setSupportActionBar(toolbar);

        // Set up bottom navigation
        setupBottomNavigation();

        // Load home fragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment(), TAG_HOME);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || !currentUser.isEmailVerified()) {
            // User is not signed in or not verified, go back to login
            navigateToLogin();
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.navigation_home) {
                            if (!currentTag.equals(TAG_HOME)) {
                                loadFragment(new HomeFragment(), TAG_HOME);
                                toolbar.setTitle(R.string.title_home);
                            }
                            return true;
                        } else if (itemId == R.id.navigation_search) {
                            if (!currentTag.equals(TAG_SEARCH)) {
                                loadFragment(new SearchFragment(), TAG_SEARCH);
                                toolbar.setTitle(R.string.title_search);
                            }
                            return true;
                        } else if (itemId == R.id.navigation_add) {
                            // Launch Add Listing Activity instead of fragment
                            startActivity(new Intent(MainActivity.this, AddListingActivity.class));
                            return false; // Don't select the item
                        } else if (itemId == R.id.navigation_messages) {
                            if (!currentTag.equals(TAG_MESSAGES)) {
                                loadFragment(new MessagesFragment(), TAG_MESSAGES);
                                toolbar.setTitle(R.string.title_messages);
                            }
                            return true;
                        } else if (itemId == R.id.navigation_profile) {
                            if (!currentTag.equals(TAG_PROFILE)) {
                                loadFragment(new ProfileFragment(), TAG_PROFILE);
                                toolbar.setTitle(R.string.title_profile);
                            }
                            return true;
                        }

                        return false;
                    }
                });
    }

    private void loadFragment(Fragment fragment, String tag) {
        currentTag = tag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate toolbar menu if needed
        // getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle toolbar actions
        return super.onOptionsItemSelected(item);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Method to handle logout (FR-1.1.5)
    public void logout() {
        mAuth.signOut();
        navigateToLogin();
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
    }
}