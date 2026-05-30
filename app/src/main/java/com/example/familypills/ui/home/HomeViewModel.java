package com.example.familypills.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.model.UserProfile;
import com.example.familypills.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<String> greeting = new MutableLiveData<>("Chào buổi sáng!");
    private final MutableLiveData<Integer> totalMeds = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> expiredSoonCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<Medicine>> recentMedicines = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        loadDashboardData();
        loadUserProfile();
    }

    public LiveData<String> getGreeting() { return greeting; }
    public LiveData<Integer> getTotalMeds() { return totalMeds; }
    public LiveData<Integer> getExpiredSoonCount() { return expiredSoonCount; }
    public LiveData<List<Medicine>> getRecentMedicines() { return recentMedicines; }

    private void loadUserProfile() {
        userRepository.getUserProfile().enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfile profile = response.body().getData();
                    String name = profile.getFullName();
                    if (name != null && !name.isEmpty()) {
                        // Lấy tên cuối cùng để chào cho thân thiện
                        String[] parts = name.split(" ");
                        String firstName = parts[parts.length - 1];
                        greeting.setValue("Chào buổi sáng, " + firstName + "!");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                // Giữ nguyên câu chào mặc định nếu lỗi
            }
        });
    }

    private void loadDashboardData() {
        // TODO: Thay thế bằng gọi API thực tế từ MedicineRepository
        totalMeds.setValue(24);
        expiredSoonCount.setValue(3);
        
        List<Medicine> meds = new ArrayList<>();
        meds.add(new Medicine("123456", "Paracetamol 500mg", "Còn 20 viên", "01/12/2025", "20/10/2023", true, false));
        meds.add(new Medicine("654321", "Vitamin C", "Còn 10 viên", "15/06/2024", "20/10/2023", false, false));
        recentMedicines.setValue(meds);
    }
}
