package com.example.familypills.ui.add_medicine;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AddMedicinePagerAdapter extends FragmentStateAdapter {

    public AddMedicinePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AddMedicineInfoFragment();
            case 1:
                return new AddMedicineScanFragment();
            case 2:
                return new AddMedicineCaptureFragment();
            default:
                return new AddMedicineInfoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
