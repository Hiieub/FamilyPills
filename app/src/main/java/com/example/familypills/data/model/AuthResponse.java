package com.example.familypills.data.model;

/**
 * Authentication response from backend
 */
public class AuthResponse {
    private int userId;
    private String email;
    private String fullName;
    private String token;
    private long tokenExpiry;

    public AuthResponse() {
    }

    public AuthResponse(int userId, String email, String fullName, String token, long tokenExpiry) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.token = token;
        this.tokenExpiry = tokenExpiry;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getTokenExpiry() { return tokenExpiry; }
    public void setTokenExpiry(long tokenExpiry) { this.tokenExpiry = tokenExpiry; }
}
