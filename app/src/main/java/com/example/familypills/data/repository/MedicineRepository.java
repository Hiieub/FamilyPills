package com.example.familypills.data.repository;

import android.content.Context;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;
import com.example.familypills.utils.Constants;

import java.util.List;

import retrofit2.Call;

public class MedicineRepository {
    private final ApiService apiService;
    private final String token;

    public MedicineRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
        this.token = null;
    }

    private String getToken(Context context) {
        return "Bearer " + context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
                .getString(Constants.PREF_AUTH_TOKEN, "");
    }

    public Call<ApiResponse<ApiService.MedicineListResponse>> getAllMedicines(Context context, int skip, int take, String filter, String search) {
        return apiService.getAllMedicines(getToken(context), skip, take, filter, search);
    }

    public Call<ApiResponse<Medicine>> addMedicine(Context context, Medicine medicine) {
        return apiService.addMedicine(getToken(context), medicine);
    }

    public Call<ApiResponse<ApiService.DeleteResponse>> deleteMedicine(Context context, int id) {
        return apiService.deleteMedicine(getToken(context), id);
    }
}
