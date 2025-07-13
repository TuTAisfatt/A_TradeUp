package com.example.tradeup_project.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String userId;
    private String email;
    private String displayName;
    private String profilePictureUrl;
    private String bio;
    private String phoneNumber;
    private float rating;
    private int totalTransactions;
    private boolean isEmailVerified;
    private long createdAt;
    private boolean isActive;

    // Default constructor required for Firebase
    public User() {
    }

    // Constructor for creating new user
    public User(String userId, String email, String displayName) {
        this.userId = userId;
        this.email = email;
        this.displayName = displayName;
        this.rating = 0.0f;
        this.totalTransactions = 0;
        this.isEmailVerified = false;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
        this.profilePictureUrl = "";
        this.bio = "";
        this.phoneNumber = "";
    }

    // Convert to Map for Firebase
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("email", email);
        result.put("displayName", displayName);
        result.put("profilePictureUrl", profilePictureUrl);
        result.put("bio", bio);
        result.put("phoneNumber", phoneNumber);
        result.put("rating", rating);
        result.put("totalTransactions", totalTransactions);
        result.put("isEmailVerified", isEmailVerified);
        result.put("createdAt", createdAt);
        result.put("isActive", isActive);
        return result;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(int totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}