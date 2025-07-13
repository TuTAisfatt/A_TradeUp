package com.example.tradeup_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tradeup_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000; // 2 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Make full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check authentication after delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAuthentication();
            }
        }, SPLASH_DELAY);
    }

    private void checkAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent;
        if (currentUser != null && currentUser.isEmailVerified()) {
            // User is signed in and verified, go to main activity
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            // No user is signed in, go to login
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}