package com.example.familypills.ui.cabinet;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.repository.MedicineRepository;
import com.example.familypills.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CabinetViewModel extends AndroidViewModel {
    private final MedicineRepository medicineRepository;
    private final MutableLiveData<List<Medicine>> medicines = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private String filter = Constants.FILTER_ALL;
    private String searchQuery = "";

    public CabinetViewModel(@NonNull Application application) {
        super(application);
        medicineRepository = new MedicineRepository(application);
        loadMedicines();
    }

    public LiveData<List<Medicine>> getMedicines() {
        return medicines;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setFilter(String filterValue) {
        filter = filterValue == null ? Constants.FILTER_ALL : filterValue;
        loadMedicines();
    }

    public void setSearchQuery(String query) {
        searchQuery = query == null ? "" : query.trim();
        loadMedicines();
    }

    public void loadMedicines() {
        isLoading.setValue(true);
        medicineRepository
                .getAllMedicines(getApplication(), Constants.PAGINATION_INITIAL_SKIP, Constants.PAGINATION_PAGE_SIZE, filter, searchQuery)
                .enqueue(new Callback<ApiResponse<ApiService.MedicineListResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<ApiService.MedicineListResponse>> call,
                                           Response<ApiResponse<ApiService.MedicineListResponse>> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            List<Medicine> items = response.body().getData().items;
                            medicines.setValue(items == null ? new ArrayList<>() : items);
                        } else if (response.code() == 401) {
                            errorMessage.setValue("Phien dang nhap da het han");
                        } else {
                            errorMessage.setValue("Khong the tai danh sach thuoc");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<ApiService.MedicineListResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        errorMessage.setValue("Loi ket noi: " + t.getMessage());
                    }
                });
    }

    public void deleteMedicine(Medicine medicine) {
        if (medicine == null) {
            return;
        }

        isLoading.setValue(true);
        medicineRepository.deleteMedicine(getApplication(), medicine.getId()).enqueue(new Callback<ApiResponse<ApiService.DeleteResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiService.DeleteResponse>> call,
                                   Response<ApiResponse<ApiService.DeleteResponse>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()) {
                    loadMedicines();
                } else {
                    errorMessage.setValue("Xoa thuoc that bai");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiService.DeleteResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Loi ket noi: " + t.getMessage());
            }
        });
    }
}
