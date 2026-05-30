package com.example.familypills.data.repository;

import android.content.Context;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.UserProfile;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;

import retrofit2.Call;

public class UserRepository {
    private final ApiService apiService;
    private final String token;

    public UserRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
        // Token is handled by AuthInterceptor in RetrofitClient, 
        // but some methods in ApiService still have @Header("Authorization")
        // We'll get it from SharedPreferences if needed, or rely on Interceptor
        this.token = "Bearer " + context.getSharedPreferences("FamilyPillsPreferences", Context.MODE_PRIVATE)
                .getString("token", "");
    }

    public Call<ApiResponse<UserProfile>> getUserProfile() {
        return apiService.getUserProfile(token);
    }

    public Call<ApiResponse<UserProfile>> updateUserProfile(String fullName) {
        ApiService.UpdateProfileRequest request = new ApiService.UpdateProfileRequest();
        request.fullName = fullName;
        return apiService.updateUserProfile(token, request);
    }

    public Call<ApiResponse<Void>> changePassword(String currentPassword, String newPassword) {
        ApiService.ChangePasswordRequest request = new ApiService.ChangePasswordRequest();
        request.currentPassword = currentPassword;
        request.newPassword = newPassword;
        return apiService.changePassword(token, request);
    }
}
