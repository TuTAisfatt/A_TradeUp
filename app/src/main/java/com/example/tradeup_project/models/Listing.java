package com.example.tradeup_project.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Listing implements Serializable {
    private String listingId;
    private String userId;
    private String title;
    private String description;
    private double price;
    private boolean isNegotiable;
    private String category;
    private String condition;
    private Location location;
    private List<String> imageUrls;
    private String status; // Available, Sold, Paused
    private long createdAt;
    private long updatedAt;
    private int views;
    private int favorites;
    private List<String> tags;
    private String soldTo;
    private long soldAt;

    // Default constructor required for Firebase
    public Listing() {
        this.imageUrls = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.status = "Available";
        this.views = 0;
        this.favorites = 0;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Constructor for creating new listing
    public Listing(String listingId, String userId, String title, String description,
                   double price, String category, String condition) {
        this();
        this.listingId = listingId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.condition = condition;
    }

    // Convert to Map for Firebase
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("listingId", listingId);
        result.put("userId", userId);
        result.put("title", title);
        result.put("description", description);
        result.put("price", price);
        result.put("isNegotiable", isNegotiable);
        result.put("category", category);
        result.put("condition", condition);

        if (location != null) {
            result.put("location", location.toMap());
        }

        result.put("imageUrls", imageUrls);
        result.put("status", status);
        result.put("createdAt", createdAt);
        result.put("updatedAt", updatedAt);
        result.put("views", views);
        result.put("favorites", favorites);
        result.put("tags", tags);

        if (soldTo != null) {
            result.put("soldTo", soldTo);
            result.put("soldAt", soldAt);
        }

        return result;
    }

    // Getters and Setters
    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isNegotiable() {
        return isNegotiable;
    }

    public void setNegotiable(boolean negotiable) {
        isNegotiable = negotiable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getSoldTo() {
        return soldTo;
    }

    public void setSoldTo(String soldTo) {
        this.soldTo = soldTo;
    }

    public long getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(long soldAt) {
        this.soldAt = soldAt;
    }
}