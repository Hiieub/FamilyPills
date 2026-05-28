package com.example.familypills.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.familypills.data.model.Medicine;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> greeting = new MutableLiveData<>("Chào buổi sáng, H");
    private final MutableLiveData<Integer> totalMeds = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> expiredSoonCount = new MutableLiveData<>(0);
    private final MutableLiveData<List<Medicine>> recentMedicines = new MutableLiveData<>();

    public HomeViewModel() {
        loadDashboardData();
    }

    public LiveData<String> getGreeting() { return greeting; }
    public LiveData<Integer> getTotalMeds() { return totalMeds; }
    public LiveData<Integer> getExpiredSoonCount() { return expiredSoonCount; }
    public LiveData<List<Medicine>> getRecentMedicines() { return recentMedicines; }

    private void loadDashboardData() {
        // Mock data matching the style of CabinetViewModel
        totalMeds.setValue(24);
        expiredSoonCount.setValue(3);
        
        List<Medicine> meds = new ArrayList<>();
        // Using the comprehensive constructor to ensure consistency
        meds.add(new Medicine("123456", "Paracetamol 500mg", "Còn 20 viên", "01/12/2025", "20/10/2023", true, false));
        meds.add(new Medicine("654321", "Vitamin C", "Còn 10 viên", "15/06/2024", "20/10/2023", false, false));
        recentMedicines.setValue(meds);
    }
}
