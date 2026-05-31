package com.example.familypills.ui.auth;

import android.app.Application;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.AuthResponse;
import com.example.familypills.data.repository.AuthRepository;
import com.example.familypills.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<AuthResponse> authSuccess = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<AuthResponse> getAuthSuccess() {
        return authSuccess;
    }

    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }

    public void login(String email, String password) {
        if (!validateEmail(email) || !validatePassword(password)) {
            return;
        }

        isLoading.setValue(true);
        authRepository.login(email.trim(), password).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                handleAuthResponse(response, "Dang nhap that bai");
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Loi ket noi: " + t.getMessage());
            }
        });
    }

    public void register(String fullName, String email, String password, String confirmPassword) {
        if (fullName == null || fullName.trim().isEmpty()) {
            errorMessage.setValue("Ho ten khong duoc de trong");
            return;
        }

        if (!validateEmail(email) || !validatePassword(password)) {
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Mat khau xac nhan khong khop");
            return;
        }

        isLoading.setValue(true);
        authRepository.register(email.trim(), password, fullName.trim()).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                handleAuthResponse(response, "Dang ky that bai");
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Loi ket noi: " + t.getMessage());
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("Email khong duoc de trong");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage.setValue("Email khong hop le");
            return false;
        }

        return true;
    }

    private boolean validatePassword(String password) {
        if (password == null || password.length() < Constants.MIN_PASSWORD_LENGTH) {
            errorMessage.setValue("Mat khau phai co it nhat 6 ky tu");
            return false;
        }

        return true;
    }

    private void handleAuthResponse(Response<ApiResponse<AuthResponse>> response, String fallbackMessage) {
        isLoading.setValue(false);
        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
            AuthResponse authResponse = response.body().getData();
            authRepository.saveSession(authResponse);
            authSuccess.setValue(authResponse);
            return;
        }

        if (response.body() != null && response.body().getMessage() != null) {
            errorMessage.setValue(response.body().getMessage());
        } else {
            errorMessage.setValue(fallbackMessage);
        }
    }
}
