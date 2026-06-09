package com.example.familypills.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * User profile response from backend
 */
public class UserProfile {
    @SerializedName("userId")
    private int userId;
    private String email;
    private String fullName;
    private String createdAt;
    private String lastLogin;
    private int medicineCount;

    public UserProfile() {
    }

    public UserProfile(int userId, String email, String fullName, String createdAt, String lastLogin, int medicineCount) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.medicineCount = medicineCount;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public int getMedicineCount() { return medicineCount; }
    public void setMedicineCount(int medicineCount) { this.medicineCount = medicineCount; }
}
