package com.example.familypills.ui.cabinet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.example.familypills.ui.add_medicine.AddMedicineActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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

public class CabinetFragment extends Fragment {

    private CabinetViewModel viewModel;
    private MedicineAdapter adapter;

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

        viewModel.getMedicines().observe(getViewLifecycleOwner(), medicines -> {
            adapter.setMedicineList(medicines);
        });

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

        TextView tvFilterUsing = view.findViewById(R.id.tvFilterUsing);
        TextView tvFilterExpired = view.findViewById(R.id.tvFilterExpired);

        tvFilterUsing.setOnClickListener(v -> {
            viewModel.setFilter("Đang dùng");
            tvFilterUsing.setBackgroundResource(R.drawable.bg_filter_selected);
            tvFilterUsing.setTextColor(getResources().getColor(R.color.progress_green, null));
            tvFilterExpired.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvFilterExpired.setTextColor(getResources().getColor(R.color.text_secondary, null));
        });

        tvFilterExpired.setOnClickListener(v -> {
            viewModel.setFilter("Hết hạn");
            tvFilterExpired.setBackgroundResource(R.drawable.bg_filter_selected);
            tvFilterExpired.setTextColor(getResources().getColor(R.color.progress_green, null));
            tvFilterUsing.setBackgroundResource(R.drawable.bg_filter_unselected);
            tvFilterUsing.setTextColor(getResources().getColor(R.color.text_secondary, null));
        });

        view.findViewById(R.id.fabAdd).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddMedicineActivity.class);
            startActivity(intent);
        });
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
