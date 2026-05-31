package com.example.familypills.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.UserProfile;
import com.example.familypills.data.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    
    private final MutableLiveData<UserProfile> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        loadUserProfile();
    }

    public LiveData<UserProfile> getUserProfile() { return userProfile; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadUserProfile() {
        isLoading.setValue(true);
        userRepository.getUserProfile(getApplication()).enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    userProfile.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Không thể tải thông tin cá nhân");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void updateProfile(String fullName) {
        if (userProfile.getValue() == null) return;
        
        isLoading.setValue(true);
        userRepository.updateUserProfile(getApplication(), fullName).enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    userProfile.setValue(response.body().getData());
                    successMessage.setValue("Cập nhật thành công");
                } else {
                    errorMessage.setValue("Cập nhật thất bại");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void changePassword(String currentPassword, String newPassword) {
        isLoading.setValue(true);
        userRepository.changePassword(getApplication(), currentPassword, newPassword).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    successMessage.setValue("Đổi mật khẩu thành công");
                } else {
                    errorMessage.setValue("Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu cũ.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
