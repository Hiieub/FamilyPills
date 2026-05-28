package com.example.familypills.ui.add_medicine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.familypills.R;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.familypills.R;
import com.google.common.util.concurrent.ListenableFuture;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import android.annotation.SuppressLint;

public class AddMedicineScanFragment extends Fragment {

    private static final String TAG = "BarcodeScan";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private PreviewView cardViewFinder;
    private Camera camera;
    private ImageAnalysis imageAnalysis;
    private BarcodeScanner scanner;
    private boolean isFlashOn = false;
    private boolean isScanning = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_medicine_scan, container, false);

        cardViewFinder = view.findViewById(R.id.cardViewFinder);
        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);

        view.findViewById(R.id.btn_close_scan).setOnClickListener(v -> {
            if (viewPager != null) {
                viewPager.setCurrentItem(0, false);
            }
        });

        view.findViewById(R.id.btn_flashlight).setOnClickListener(v -> toggleFlash());

        scanner = BarcodeScanning.getClient();

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

                Preview cardPreview = new Preview.Builder().build();
                cardPreview.setSurfaceProvider(cardViewFinder.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), image -> {
                    if (isScanning) {
                        processImageProxy(image);
                    } else {
                        image.close();
                    }
                });

                cameraProvider.unbindAll();
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, cardPreview, imageAnalysis);

            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void processImageProxy(ImageProxy imageProxy) {
        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null && !rawValue.isEmpty()) {
                            onBarcodeDetected(rawValue);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Barcode scanning failed", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void onBarcodeDetected(String barcode) {
        if (!isScanning) return;
        isScanning = false;
        
        requireActivity().runOnUiThread(() -> {
            Toast.makeText(requireContext(), "Barcode detected: " + barcode, Toast.LENGTH_LONG).show();
            
            // Return to info fragment
            Bundle result = new Bundle();
            result.putString("barcode", barcode);
            getParentFragmentManager().setFragmentResult("barcode_scanned", result);
            
            ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
            if (viewPager != null) {
                viewPager.setCurrentItem(0, false);
            }
        });
    }

    private void toggleFlash() {
        if (camera != null && camera.getCameraInfo().hasFlashUnit()) {
            isFlashOn = !isFlashOn;
            camera.getCameraControl().enableTorch(isFlashOn);
            
            ImageView btnFlash = getView().findViewById(R.id.btn_flashlight);
            if (btnFlash != null) {
                btnFlash.setImageResource(isFlashOn ? R.drawable.ic_flash_on : R.drawable.ic_flash_off);
            }
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
