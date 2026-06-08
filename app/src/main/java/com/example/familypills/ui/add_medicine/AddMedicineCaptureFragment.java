package com.example.familypills.ui.add_medicine;

import android.Manifest;
import android.net.Uri;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.familypills.R;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class AddMedicineCaptureFragment extends Fragment {

    private static final String TAG = "CameraXApp";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private boolean isFlashOn = false;
    private final ActivityResultLauncher<String> galleryPicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleGalleryImage);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_medicine_capture, container, false);

        viewFinder = view.findViewById(R.id.viewFinder);
        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);

        view.findViewById(R.id.btnClose).setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(0, false);
            }
        });

        view.findViewById(R.id.btnFlash).setOnClickListener(v -> toggleFlash());

        view.findViewById(R.id.btnCaptureContainer).setOnClickListener(v -> takePhoto());
        view.findViewById(R.id.btnGallery).setOnClickListener(v -> galleryPicker.launch("image/*"));

        view.findViewById(R.id.btnFlipCamera).setOnClickListener(v -> {
            lensFacing = (lensFacing == CameraSelector.LENS_FACING_BACK) ?
                    CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
            startCamera();
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        return view;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setFlashMode(isFlashOn ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF)
                        .build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(lensFacing)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        File photoFile = new File(requireContext().getExternalFilesDir(null),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Toast.makeText(requireContext(), "Đã chụp ảnh: " + photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        
                        sendImageResult(photoFile.getAbsolutePath());
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Photo capture failed: " + exception.getMessage(), exception);
                    }
                });
    }

    private void handleGalleryImage(Uri uri) {
        if (uri == null) return;

        try {
            File imageFile = copyImageToAppStorage(uri);
            // Toast.makeText(requireContext(), "Ảnh từ thư viện", Toast.LENGTH_SHORT).show();
            sendImageResult(imageFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy gallery image", e);
            // Toast.makeText(requireContext(), "Ko thể tải ảnh từ thư viện", Toast.LENGTH_SHORT).show();
        }
    }

    private File copyImageToAppStorage(Uri uri) throws IOException {
        File imageFile = new File(requireContext().getExternalFilesDir(null),
                "gallery_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            if (inputStream == null) {
                throw new IOException("Cannot open selected image");
            }

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return imageFile;
    }

    private void sendImageResult(String imagePath) {
        Bundle result = new Bundle();
        result.putString("imagePath", imagePath);
        getParentFragmentManager().setFragmentResult("photo_captured", result);

        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
        if (viewPager != null) {
            viewPager.setCurrentItem(0, false);
        }
    }

    private void toggleFlash() {
        isFlashOn = !isFlashOn;
        if (imageCapture != null) {
            imageCapture.setFlashMode(isFlashOn ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF);
        }
        View btnFlash = getView().findViewById(R.id.btnFlash);
        if (btnFlash instanceof android.widget.ImageView) {
            ((android.widget.ImageView) btnFlash).setImageResource(isFlashOn ? R.drawable.ic_flash_on : R.drawable.ic_flash_off);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(requireContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
