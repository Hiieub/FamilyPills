package com.example.familypills.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.familypills.R;
import com.example.familypills.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private AuthViewModel viewModel;
    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText() == null ? "" : etFullName.getText().toString();
            String email = etEmail.getText() == null ? "" : etEmail.getText().toString();
            String password = etPassword.getText() == null ? "" : etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText() == null ? "" : etConfirmPassword.getText().toString();
            viewModel.register(fullName, email, password, confirmPassword);
        });

        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(this, isLoading -> {
            btnRegister.setEnabled(!isLoading);
            btnRegister.setText(isLoading ? "DANG KY..." : "DANG KY");
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getAuthSuccess().observe(this, authResponse -> {
            if (authResponse != null) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }
}
