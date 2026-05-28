package com.example.familypills.ui.cabinet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.familypills.data.model.Medicine;

import java.util.ArrayList;
import java.util.List;

public class CabinetViewModel extends ViewModel {
    private final MutableLiveData<List<Medicine>> medicines = new MutableLiveData<>();
    private final MutableLiveData<String> filter = new MutableLiveData<>("Đang dùng");
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private List<Medicine> allMedicines = new ArrayList<>();

    public CabinetViewModel() {
        loadMedicines();
    }

    public LiveData<List<Medicine>> getMedicines() {
        return medicines;
    }

    public void setFilter(String filterValue) {
        filter.setValue(filterValue);
        loadMedicines();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        applyFilterAndSearch();
    }

    private void applyFilterAndSearch() {
        String query = searchQuery.getValue().toLowerCase();
        List<Medicine> filteredList = new ArrayList<>();
        for (Medicine m : allMedicines) {
            if (m.getName().toLowerCase().contains(query)) {
                filteredList.add(m);
            }
        }
        medicines.setValue(filteredList);
    }

    public void deleteMedicine(Medicine medicine) {
        allMedicines.remove(medicine);
        applyFilterAndSearch();
    }

    private void loadMedicines() {
        List<Medicine> list = new ArrayList<>();
        if ("Đang dùng".equals(filter.getValue())) {
            list.add(new Medicine("1", "Vitamin C 1000mg", "Còn 28 viên", "20/05/2027", "20/10/2023", true, false));
            list.add(new Medicine("2", "Dầu cá Omega 3", "Còn 45 viên", "20/08/2027", "20/10/2023", false, false));
            list.add(new Medicine("3", "Siro ho Prospan", "Còn ~50ml", "20/01/2027", "20/10/2023", false, false));
        } else {
            list.add(new Medicine("4", "Panadol Extra", "0 viên", "20/01/2023", "10/01/2023", false, true));
        }
        allMedicines = list;
        applyFilterAndSearch();
    }
}
