package com.example.familypills.data.repository;

import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class MedicineRepository {
    private final ApiService apiService;

    public MedicineRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public Call<List<Medicine>> getAllMedicines() {
        return apiService.getAllMedicines();
    }

    public Call<Medicine> addMedicine(Medicine medicine) {
        return apiService.addMedicine(medicine);
    }
    
    public Call<Void> deleteMedicine(int id) {
        return apiService.deleteMedicine(id);
    }
}
