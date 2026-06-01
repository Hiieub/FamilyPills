package com.example.familypills.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.model.StatsResponse;
import com.example.familypills.data.model.UserProfile;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.repository.MedicineRepository;
import com.example.familypills.data.repository.UserRepository;
import com.example.familypills.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;
    private final MutableLiveData<String> greeting = new MutableLiveData<>(getTimeBasedGreeting() + "!");

    private String getTimeBasedGreeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 11) {
            return "Chào buổi sáng";
        } else if (hour >= 11 && hour < 14) {
            return "Chào buổi trưa";
        } else if (hour >= 14 && hour < 18) {
            return "Chào buổi chiều";
        } else {
            return "Chào buổi tối";
        }
    }
    private final MutableLiveData<Integer> totalMeds = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> expiredSoonCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<Medicine>> recentMedicines = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        medicineRepository = new MedicineRepository(application);
        loadDashboardData();
        loadUserProfile();
    }

    public LiveData<String> getGreeting() { return greeting; }
    public LiveData<Integer> getTotalMeds() { return totalMeds; }
    public LiveData<Integer> getExpiredSoonCount() { return expiredSoonCount; }
    public LiveData<List<Medicine>> getRecentMedicines() { return recentMedicines; }

    private void loadUserProfile() {
        userRepository.getUserProfile(getApplication()).enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body().getData();
                    String name = profile.getFullName();
                    if (name != null && !name.isEmpty()) {
                        // Lấy tên cuối cùng để chào cho thân thiện
                        String[] parts = name.split(" ");
                        String firstName = parts[parts.length - 1];
                        greeting.setValue(getTimeBasedGreeting() + ", " + firstName + "!");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                // Giữ nguyên câu chào mặc định nếu lỗi
            }
        });
    }

    public void loadDashboardData() {
        // Load stats from API
        medicineRepository.getStats(getApplication()).enqueue(new Callback<ApiResponse<StatsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<StatsResponse>> call, Response<ApiResponse<StatsResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    StatsResponse stats = response.body().getData();
                    totalMeds.setValue(stats.getTotalMedicines());
                    expiredSoonCount.setValue(stats.getExpiredSoon());
                } else {
                    totalMeds.setValue(0);
                    expiredSoonCount.setValue(0);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<StatsResponse>> call, Throwable t) {
                totalMeds.setValue(0);
                expiredSoonCount.setValue(0);
            }
        });

        // Load recent medicines from API
        medicineRepository.getAllMedicines(getApplication(), Constants.PAGINATION_INITIAL_SKIP, 4, Constants.FILTER_ALL, "")
                .enqueue(new Callback<ApiResponse<ApiService.MedicineListResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ApiService.MedicineListResponse>> call,
                                           Response<ApiResponse<ApiService.MedicineListResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            List<Medicine> items = response.body().getData().items;
                            recentMedicines.setValue(items == null ? new ArrayList<>() : items);
                        } else {
                            recentMedicines.setValue(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ApiService.MedicineListResponse>> call, Throwable t) {
                        recentMedicines.setValue(new ArrayList<>());
                    }
                });
    }
}
