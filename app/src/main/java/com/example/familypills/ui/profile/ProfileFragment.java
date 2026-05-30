package com.example.familypills.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.familypills.R;
import com.example.familypills.data.model.UserProfile;
import com.example.familypills.ui.auth.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private TextView tvName, tvEmail;
    private ProgressBar progressBar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        progressBar = view.findViewById(R.id.profileProgressBar);

        observeViewModel();

        // Logout
        view.findViewById(R.id.cvLogout).setOnClickListener(v -> logout());

        // Edit Profile
        view.findViewById(R.id.cvEditProfile).setOnClickListener(v -> showEditProfileDialog());

        // Change Password
        view.findViewById(R.id.cvChangePassword).setOnClickListener(v -> showChangePasswordDialog());
    }

    private void observeViewModel() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), profile -> {
            if (profile != null) {
                tvName.setText(profile.getFullName());
                tvEmail.setText(profile.getEmail());
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etFullName = dialogView.findViewById(R.id.etFullName);
        
        UserProfile currentProfile = viewModel.getUserProfile().getValue();
        if (currentProfile != null) {
            etFullName.setText(currentProfile.getFullName());
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String newName = etFullName.getText().toString().trim();
            if (!newName.isEmpty()) {
                viewModel.updateProfile(newName);
                dialog.dismiss();
            } else {
                etFullName.setError("Họ tên không được để trống");
            }
        });

        dialog.show();
    }

    private void showChangePasswordDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etCurrent = dialogView.findViewById(R.id.etCurrentPassword);
        TextInputEditText etNew = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirm = dialogView.findViewById(R.id.etConfirmPassword);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSave).setOnClickListener(v -> {
            String currentPass = etCurrent.getText().toString();
            String newPass = etNew.getText().toString();
            String confirmPass = etConfirm.getText().toString();

            if (currentPass.isEmpty() || newPass.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                etConfirm.setError("Mật khẩu xác nhận không khớp");
                return;
            }

            viewModel.changePassword(currentPass, newPass);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void logout() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
