package com.example.familypills.data.repository;

import android.content.Context;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.UserProfile;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;
import com.example.familypills.utils.Constants;

import retrofit2.Call;

public class UserRepository {
    private final ApiService apiService;
    private final String token;

    public UserRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
        this.token = null;
    }

    private String getToken(Context context) {
        return "Bearer " + context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
                .getString(Constants.PREF_AUTH_TOKEN, "");
    }

    public Call<ApiResponse<UserProfile>> getUserProfile(Context context) {
        return apiService.getUserProfile(getToken(context));
    }

    public Call<ApiResponse<UserProfile>> updateUserProfile(Context context, String fullName) {
        ApiService.UpdateProfileRequest request = new ApiService.UpdateProfileRequest();
        request.newFullName = fullName;
        return apiService.updateUserProfile(getToken(context), request);
    }

    public Call<ApiResponse<Void>> changePassword(Context context, String currentPassword, String newPassword) {
        ApiService.ChangePasswordRequest request = new ApiService.ChangePasswordRequest();
        request.currentPassword = currentPassword;
        request.newPassword = newPassword;
        return apiService.changePassword(getToken(context), request);
    }
}
