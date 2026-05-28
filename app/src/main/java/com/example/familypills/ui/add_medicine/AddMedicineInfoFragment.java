package com.example.familypills.ui.add_medicine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.familypills.R;

import androidx.viewpager2.widget.ViewPager2;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;


public class AddMedicineInfoFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_medicine_info, container, false);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);

        // Check if editing
        int medicineId = -1;
        if (getActivity() != null && getActivity().getIntent() != null) {
            medicineId = getActivity().getIntent().getIntExtra("medicine_id", -1);
        }

        if (medicineId != -1) {
            if (tvTitle != null) tvTitle.setText("Chỉnh Sửa Thuốc");
            android.widget.Button btnSave = view.findViewById(R.id.btn_save_medicine);
            btnSave.setText("LƯU THAY ĐỔI");
            btnSave.setTag(medicineId);
            loadMedicineData(view, medicineId);
        }

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        view.findViewById(R.id.btn_save_medicine).setOnClickListener(v -> {
            boolean isEdit = v.getTag() != null;
            String message = isEdit ? "Cập nhật thuốc thành công!" : "Thêm thuốc thành công!";
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        view.findViewById(R.id.btn_scan_barcode).setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(1, false);
            }
        });

        view.findViewById(R.id.btn_capture_image).setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(2, false);
            }
        });

        // Unit Selection
        View btnSelectUnit = view.findViewById(R.id.btn_select_unit);
        TextView tvUnit = view.findViewById(R.id.tv_unit);
        btnSelectUnit.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), btnSelectUnit);
            popup.getMenu().add("Viên");
            popup.getMenu().add("Hộp");
            popup.getMenu().add("Vỉ");
            popup.getMenu().add("Chai");
            popup.getMenu().add("Ống");
            
            popup.setOnMenuItemClickListener(item -> {
                tvUnit.setText(item.getTitle());
                return true;
            });
            popup.show();
        });

        // Expiry Date Selection
        View btnExpiryDate = view.findViewById(R.id.btn_expiry_date);
        TextView tvExpiryDate = view.findViewById(R.id.tv_expiry_date);
        btnExpiryDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                        tvExpiryDate.setText(formattedDate);
                        tvExpiryDate.setTextColor(getResources().getColor(R.color.text_main));
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Listen for image results (Mocking with Fragment Result API if available or just using a simple check)
        getParentFragmentManager().setFragmentResultListener("photo_captured", this, (requestKey, bundle) -> {
            String imagePath = bundle.getString("imagePath");
            if (imagePath != null) {
                ImageView ivMedicineImage = view.findViewById(R.id.iv_medicine_image);
                View placeholder = view.findViewById(R.id.ll_capture_placeholder);
                ivMedicineImage.setVisibility(View.VISIBLE);
                placeholder.setVisibility(View.GONE);
                ivMedicineImage.setImageURI(Uri.fromFile(new java.io.File(imagePath)));
            }
        });

        getParentFragmentManager().setFragmentResultListener("barcode_scanned", this, (requestKey, bundle) -> {
            String barcode = bundle.getString("barcode");
            if (barcode != null) {
                EditText etBarcode = view.findViewById(R.id.et_barcode);
                if (etBarcode != null) {
                    etBarcode.setText(barcode);
                }
            }
        });

        return view;
    }

    private void loadMedicineData(View view, int medicineId) {
        // Trong thực tế, bạn sẽ lấy dữ liệu từ database thông qua ViewModel/Repository
        // Ở đây tôi demo với dữ liệu mẫu dựa trên medicineId
        EditText etName = view.findViewById(R.id.et_medicine_name);
        EditText etBarcode = view.findViewById(R.id.et_barcode);
        EditText etTotalQuantity = view.findViewById(R.id.et_total_quantity);
        TextView tvUnit = view.findViewById(R.id.tv_unit);
        TextView tvExpiryDate = view.findViewById(R.id.tv_expiry_date);
        ImageView ivMedicineImage = view.findViewById(R.id.iv_medicine_image);
        View placeholder = view.findViewById(R.id.ll_capture_placeholder);

        // Giả lập dữ liệu thuốc cần sửa
        etName.setText("Vitamin C 1000mg");
        etBarcode.setText("8934567890123");
        etTotalQuantity.setText("30");
        tvUnit.setText("Viên");
        tvExpiryDate.setText("05/20/2027");
        tvExpiryDate.setTextColor(getResources().getColor(R.color.text_main));
        
        // Nếu có ảnh thì hiển thị (giả lập)
        // ivMedicineImage.setVisibility(View.VISIBLE);
        // placeholder.setVisibility(View.GONE);
    }
}
