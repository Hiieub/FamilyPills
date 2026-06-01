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

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.repository.MedicineRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddMedicineInfoFragment extends Fragment {

    private MedicineRepository medicineRepository;
    private String capturedImagePath = null;
    private int editMedicineId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_medicine_info, container, false);

        medicineRepository = new MedicineRepository(requireContext());

        TextView tvTitle = view.findViewById(R.id.tv_title);
        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
        Button btnSave = view.findViewById(R.id.btn_save_medicine);

        // Check if editing
        if (getActivity() != null && getActivity().getIntent() != null) {
            editMedicineId = getActivity().getIntent().getIntExtra("medicine_id", -1);
        }

        if (editMedicineId != -1) {
            if (tvTitle != null) tvTitle.setText("Chỉnh Sửa Thuốc");
            btnSave.setText("LƯU THAY ĐỔI");
            loadMedicineData(view, editMedicineId);
        }

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        btnSave.setOnClickListener(v -> saveMedicine(view, btnSave));

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

        // Listen for image results
        getParentFragmentManager().setFragmentResultListener("photo_captured", this, (requestKey, bundle) -> {
            String imagePath = bundle.getString("imagePath");
            if (imagePath != null) {
                capturedImagePath = imagePath;
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

    private void saveMedicine(View view, Button btnSave) {
        EditText etName = view.findViewById(R.id.et_medicine_name);
        EditText etBarcode = view.findViewById(R.id.et_barcode);
        EditText etTotalQuantity = view.findViewById(R.id.et_total_quantity);
        TextView tvUnit = view.findViewById(R.id.tv_unit);
        TextView tvExpiryDate = view.findViewById(R.id.tv_expiry_date);

        String name = etName.getText().toString().trim();
        String barcode = etBarcode.getText().toString().trim();
        String quantityStr = etTotalQuantity.getText().toString().trim();
        String unit = tvUnit.getText().toString();
        String expiryDate = tvExpiryDate.getText().toString();

        // Validation
        if (name.isEmpty()) {
            etName.setError("Tên thuốc không được để trống");
            etName.requestFocus();
            return;
        }

        int totalQuantity = 0;
        if (!quantityStr.isEmpty()) {
            try {
                totalQuantity = Integer.parseInt(quantityStr);
                if (totalQuantity < 0) {
                    etTotalQuantity.setError("Số lượng không được âm");
                    etTotalQuantity.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etTotalQuantity.setError("Số lượng không hợp lệ");
                etTotalQuantity.requestFocus();
                return;
            }
        }

        if (unit.equals("Chọn đơn vị")) {
            unit = null;
        }
        if (expiryDate.equals("Chọn ngày") || expiryDate.equals("Chọn ngày hết hạn")) {
            expiryDate = null;
        }

        Medicine medicine = new Medicine(name, barcode.isEmpty() ? null : barcode, totalQuantity, unit, expiryDate, capturedImagePath);

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        if (editMedicineId != -1) {
            // Update existing medicine
            medicineRepository.updateMedicine(requireContext(), editMedicineId, medicine).enqueue(new Callback<ApiResponse<Medicine>>() {
                @Override
                public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                    btnSave.setEnabled(true);
                    btnSave.setText("LƯU THAY ĐỔI");
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Cập nhật thuốc thành công!", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("LƯU THAY ĐỔI");
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Add new medicine
            medicineRepository.addMedicine(requireContext(), medicine).enqueue(new Callback<ApiResponse<Medicine>>() {
                @Override
                public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                    btnSave.setEnabled(true);
                    btnSave.setText("THÊM THUỐC");
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Thêm thuốc thành công!", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Thêm thuốc thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                    btnSave.setEnabled(true);
                    btnSave.setText("THÊM THUỐC");
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadMedicineData(View view, int medicineId) {
        medicineRepository.getMedicineById(requireContext(), medicineId).enqueue(new Callback<ApiResponse<Medicine>>() {
            @Override
            public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Medicine med = response.body().getData();
                    EditText etName = view.findViewById(R.id.et_medicine_name);
                    EditText etBarcode = view.findViewById(R.id.et_barcode);
                    EditText etTotalQuantity = view.findViewById(R.id.et_total_quantity);
                    TextView tvUnit = view.findViewById(R.id.tv_unit);
                    TextView tvExpiryDate = view.findViewById(R.id.tv_expiry_date);

                    etName.setText(med.getName());
                    if (med.getBarcode() != null) etBarcode.setText(med.getBarcode());
                    etTotalQuantity.setText(String.valueOf(med.getTotalQuantity()));
                    if (med.getUnit() != null) tvUnit.setText(med.getUnit());
                    if (med.getExpiryDate() != null) {
                        tvExpiryDate.setText(med.getExpiryDate());
                        tvExpiryDate.setTextColor(getResources().getColor(R.color.text_main));
                    }
                    if (med.getImagePath() != null) {
                        capturedImagePath = med.getImagePath();
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải thông tin thuốc", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
