package com.example.familypills.data.repository;

import android.content.Context;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;

public class MedicineRepository {
    private final ApiService apiService;
    private final String token;

    public MedicineRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
        this.token = "Bearer " + context.getSharedPreferences("FamilyPillsPreferences", Context.MODE_PRIVATE)
                .getString("auth_token", "");
    }

    public Call<ApiResponse<ApiService.MedicineListResponse>> getAllMedicines(int skip, int take, String filter, String search) {
        return apiService.getAllMedicines(token, skip, take, filter, search);
    }

    public Call<ApiResponse<Medicine>> addMedicine(Medicine medicine) {
        return apiService.addMedicine(token, medicine);
    }

    public Call<ApiResponse<ApiService.DeleteResponse>> deleteMedicine(int id) {
        return apiService.deleteMedicine(token, id);
    }
}
