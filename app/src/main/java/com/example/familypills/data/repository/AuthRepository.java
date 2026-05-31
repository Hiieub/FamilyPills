package com.example.familypills.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.AuthResponse;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;
import com.example.familypills.utils.Constants;

import retrofit2.Call;

public class AuthRepository {
    private final ApiService apiService;
    private final SharedPreferences preferences;

    public AuthRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
        this.preferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public Call<ApiResponse<AuthResponse>> login(String email, String password) {
        ApiService.LoginRequest request = new ApiService.LoginRequest();
        request.email = email;
        request.password = password;
        return apiService.login(request);
    }

    public Call<ApiResponse<AuthResponse>> register(String email, String password, String fullName) {
        ApiService.RegisterRequest request = new ApiService.RegisterRequest();
        request.email = email;
        request.password = password;
        request.fullName = fullName;
        return apiService.register(request);
    }

    public void saveSession(AuthResponse authResponse) {
        preferences.edit()
                .putString(Constants.PREF_AUTH_TOKEN, authResponse.getToken())
                .putInt(Constants.PREF_USER_ID, authResponse.getUserId())
                .putString(Constants.PREF_USER_EMAIL, authResponse.getEmail())
                .putString(Constants.PREF_USER_NAME, authResponse.getFullName())
                .putLong(Constants.PREF_TOKEN_EXPIRY, authResponse.getTokenExpiry())
                .apply();
    }

    public boolean isLoggedIn() {
        String token = preferences.getString(Constants.PREF_AUTH_TOKEN, null);
        long tokenExpiry = preferences.getLong(Constants.PREF_TOKEN_EXPIRY, 0);
        long now = System.currentTimeMillis() / 1000L;
        return token != null && !token.isEmpty() && tokenExpiry > now;
    }

    public void clearSession() {
        preferences.edit()
                .remove(Constants.PREF_AUTH_TOKEN)
                .remove(Constants.PREF_USER_ID)
                .remove(Constants.PREF_USER_EMAIL)
                .remove(Constants.PREF_USER_NAME)
                .remove(Constants.PREF_TOKEN_EXPIRY)
                .apply();
        RetrofitClient.reset();
    }
}
