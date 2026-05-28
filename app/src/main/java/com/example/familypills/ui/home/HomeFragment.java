package com.example.familypills.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familypills.R;
import com.example.familypills.ui.cabinet.MedicineAdapter;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private MedicineAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        TextView tvTotalMeds = view.findViewById(R.id.tvTotalMeds);
        TextView tvExpiredSoon = view.findViewById(R.id.tvExpiredSoon);
        RecyclerView recyclerView = view.findViewById(R.id.rvRecentMeds);

        // UI Setup
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MedicineAdapter();
        adapter.setShowMenu(false); // Ẩn menu 3 chấm ở trang chủ
        recyclerView.setAdapter(adapter);

        // Observe Data
        viewModel.getGreeting().observe(getViewLifecycleOwner(), tvGreeting::setText);
        viewModel.getTotalMeds().observe(getViewLifecycleOwner(), count -> 
                tvTotalMeds.setText(String.valueOf(count)));
        viewModel.getExpiredSoonCount().observe(getViewLifecycleOwner(), count -> 
                tvExpiredSoon.setText(String.valueOf(count)));
        
        viewModel.getRecentMedicines().observe(getViewLifecycleOwner(), meds -> {
            adapter.setMedicineList(meds);
        });

        // Click listeners for quick actions
        view.findViewById(R.id.btnQuickAdd).setOnClickListener(v -> {
            // TODO: Navigate to Add Medicine
        });

        view.findViewById(R.id.btnScanCabinet).setOnClickListener(v -> {
            // TODO: Open Scanner
        });
    }
}
