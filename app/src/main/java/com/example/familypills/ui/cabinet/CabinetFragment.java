package com.example.familypills.ui.cabinet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.example.familypills.ui.add_medicine.AddMedicineActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.familypills.R;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.utils.Constants;

public class CabinetFragment extends Fragment {

    private CabinetViewModel viewModel;
    private MedicineAdapter adapter;
    private TextView tvFilterUsing, tvFilterExpired, tvFilterRunningLow;

    public CabinetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cabinet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CabinetViewModel.class);
        
        RecyclerView recyclerView = view.findViewById(R.id.rvMedicines);
        adapter = new MedicineAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnMedicineActionListener(new MedicineAdapter.OnMedicineActionListener() {
            @Override
            public void onEdit(Medicine medicine) {
                Intent intent = new Intent(requireContext(), AddMedicineActivity.class);
                intent.putExtra("medicine_id", medicine.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(Medicine medicine) {
                showDeleteConfirmationDialog(medicine);
            }
        });

        // Observe medicines list
        viewModel.getMedicines().observe(getViewLifecycleOwner(), medicines -> {
            adapter.setMedicineList(medicines);
            // Show empty state if needed
            TextView tvEmpty = view.findViewById(R.id.tvEmptyState);
            if (tvEmpty != null) {
                tvEmpty.setVisibility(medicines == null || medicines.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            ProgressBar progressBar = view.findViewById(R.id.progressBar);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // Search
        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filters
        tvFilterUsing = view.findViewById(R.id.tvFilterUsing);
        tvFilterExpired = view.findViewById(R.id.tvFilterExpired);
        tvFilterRunningLow = view.findViewById(R.id.tvFilterRunningLow);

        tvFilterUsing.setOnClickListener(v -> {
            viewModel.setFilter(Constants.FILTER_ALL);
            setActiveFilter(tvFilterUsing);
        });

        tvFilterExpired.setOnClickListener(v -> {
            viewModel.setFilter(Constants.FILTER_EXPIRED);
            setActiveFilter(tvFilterExpired);
        });

        if (tvFilterRunningLow != null) {
            tvFilterRunningLow.setOnClickListener(v -> {
                viewModel.setFilter(Constants.FILTER_EXPIRING_SOON);
                setActiveFilter(tvFilterRunningLow);
            });
        }

        // FAB
        view.findViewById(R.id.fabAdd).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddMedicineActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.loadMedicines();
        }
    }

    private void setActiveFilter(TextView active) {
        // Reset all filters
        resetFilterStyle(tvFilterUsing);
        resetFilterStyle(tvFilterExpired);
        if (tvFilterRunningLow != null) resetFilterStyle(tvFilterRunningLow);

        // Highlight active
        active.setBackgroundResource(R.drawable.bg_filter_selected);
        active.setTextColor(getResources().getColor(R.color.progress_green, null));
    }

    private void resetFilterStyle(TextView tv) {
        if (tv != null) {
            tv.setBackgroundResource(R.drawable.bg_filter_unselected);
            tv.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }
    }

    private void showDeleteConfirmationDialog(Medicine medicine) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa thuốc \"" + medicine.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteMedicine(medicine);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
