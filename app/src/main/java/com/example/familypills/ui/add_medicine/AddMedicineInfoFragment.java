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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.remote.ApiService;
import com.example.familypills.data.remote.RetrofitClient;
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
                showMedicineImage(view, imagePath);
            }
        });

        getParentFragmentManager().setFragmentResultListener("barcode_scanned", this, (requestKey, bundle) -> {
            String barcode = bundle.getString("barcode");
            if (barcode != null) {
                EditText etBarcode = view.findViewById(R.id.et_barcode);
                if (etBarcode != null) {
                    etBarcode.setText(barcode);
                }
                autofillFromBarcode(view, barcode);
            }
        });

        return view;
    }

    private void autofillFromBarcode(View view, String barcode) {
        if (editMedicineId != -1 || barcode.trim().isEmpty()) {
            return;
        }

        medicineRepository.validateBarcode(requireContext(), barcode.trim()).enqueue(new Callback<ApiResponse<ApiService.BarcodeValidationResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiService.BarcodeValidationResponse>> call, Response<ApiResponse<ApiService.BarcodeValidationResponse>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getData() == null) {
                    return;
                }

                ApiService.BarcodeValidationResponse barcodeResponse = response.body().getData();
                if (!barcodeResponse.exists || barcodeResponse.medicine == null) {
                    return;
                }

                applyMedicineTemplate(view, barcodeResponse.medicine);
                Toast.makeText(getContext(), "Đã tự điền thông tin thuốc, Hãy chọn HSD mới", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiService.BarcodeValidationResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Ko thể quét mã " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyMedicineTemplate(View view, Medicine med) {
        EditText etName = view.findViewById(R.id.et_medicine_name);
        EditText etTotalQuantity = view.findViewById(R.id.et_total_quantity);
        TextView tvUnit = view.findViewById(R.id.tv_unit);

        if (med.getName() != null) {
            etName.setText(med.getName());
        }
        etTotalQuantity.setText(String.valueOf(med.getTotalQuantity()));
        if (med.getUnit() != null) {
            tvUnit.setText(med.getUnit());
        }
        if (med.getImagePath() != null) {
            showMedicineImage(view, med.getImagePath());
        }
    }

    private void showMedicineImage(View view, String imagePath) {
        capturedImagePath = imagePath;

        ImageView ivMedicineImage = view.findViewById(R.id.iv_medicine_image);
        View placeholder = view.findViewById(R.id.ll_capture_placeholder);
        ivMedicineImage.setVisibility(View.VISIBLE);
        placeholder.setVisibility(View.GONE);

        File localFile = new File(imagePath);
        if (localFile.exists()) {
            ivMedicineImage.setImageURI(Uri.fromFile(localFile));
            return;
        }

        String imageUrl = RetrofitClient.getAbsoluteUrl(imagePath);
        new Thread(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> ivMedicineImage.setImageBitmap(bitmap));
                }
            } catch (Exception ignored) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        ivMedicineImage.setVisibility(View.GONE);
                        placeholder.setVisibility(View.VISIBLE);
                    });
                }
            }
        }).start();
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
        saveMedicineWithImageUpload(medicine, btnSave);
    }

    private void saveMedicineWithImageUpload(Medicine medicine, Button btnSave) {
        if (!isLocalImagePath(capturedImagePath)) {
            saveMedicineToApi(medicine, btnSave);
            return;
        }

        btnSave.setText("Đang tải ảnh...");
        medicineRepository.uploadMedicineImage(requireContext(), new File(capturedImagePath)).enqueue(new Callback<ApiResponse<ApiService.ImageUploadResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ApiService.ImageUploadResponse>> call, Response<ApiResponse<ApiService.ImageUploadResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    capturedImagePath = response.body().getData().imagePath;
                    medicine.setImagePath(capturedImagePath);
                    saveMedicineToApi(medicine, btnSave);
                } else {
                    resetSaveButton(btnSave);
                    Toast.makeText(getContext(), "Tải ảnh thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ApiService.ImageUploadResponse>> call, Throwable t) {
                resetSaveButton(btnSave);
                Toast.makeText(getContext(), "Lỗi tải ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isLocalImagePath(String imagePath) {
        return imagePath != null && new File(imagePath).exists();
    }

    private void saveMedicineToApi(Medicine medicine, Button btnSave) {
        if (editMedicineId != -1) {
            // Update existing medicine
            medicineRepository.updateMedicine(requireContext(), editMedicineId, medicine).enqueue(new Callback<ApiResponse<Medicine>>() {
                @Override
                public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                    resetSaveButton(btnSave);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Cập nhật thuốc thành công!", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                    resetSaveButton(btnSave);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Add new medicine
            medicineRepository.addMedicine(requireContext(), medicine).enqueue(new Callback<ApiResponse<Medicine>>() {
                @Override
                public void onResponse(Call<ApiResponse<Medicine>> call, Response<ApiResponse<Medicine>> response) {
                    resetSaveButton(btnSave);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Thêm thuốc thành công!", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) getActivity().finish();
                    } else {
                        Toast.makeText(getContext(), "Thêm thuốc thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Medicine>> call, Throwable t) {
                    resetSaveButton(btnSave);
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void resetSaveButton(Button btnSave) {
        btnSave.setEnabled(true);
        btnSave.setText(editMedicineId != -1 ? "LƯU THAY ĐỔI" : "THÊM THUỐC");
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
                        showMedicineImage(view, med.getImagePath());
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
