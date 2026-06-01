package com.example.familypills.data.repository;

import android.content.Context;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.model.StatsResponse;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;
import com.example.familypills.utils.Constants;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class MedicineRepository {
    private final ApiService apiService;

    public MedicineRepository(Context context) {
        this.apiService = RetrofitClient.getApiService(context);
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

    public Call<ApiResponse<Medicine>> getMedicineById(Context context, int id) {
        return apiService.getMedicineById(getToken(context), id);
    }

    public Call<ApiResponse<Medicine>> updateMedicine(Context context, int id, Medicine medicine) {
        return apiService.updateMedicine(getToken(context), id, medicine);
    }

    public Call<ApiResponse<ApiService.DeleteResponse>> deleteMedicine(Context context, int id) {
        return apiService.deleteMedicine(getToken(context), id);
    }

    public Call<ApiResponse<StatsResponse>> getStats(Context context) {
        return apiService.getMedicineStats(getToken(context));
    }

    public Call<ApiResponse<ApiService.BarcodeValidationResponse>> validateBarcode(Context context, String barcode) {
        return apiService.validateBarcode(getToken(context), barcode);
    }

    public Call<ApiResponse<ApiService.ImageUploadResponse>> uploadMedicineImage(Context context, File imageFile) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
        return apiService.uploadMedicineImage(getToken(context), body);
    }
}
