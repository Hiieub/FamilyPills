package com.example.familypills.data.remote;

import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.model.AuthResponse;
import com.example.familypills.data.model.StatsResponse;
import com.example.familypills.data.model.UserProfile;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // AUTHENTICATION ENDPOINTS (Feature 1)
    
    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("api/auth/logout")
    Call<ApiResponse<Void>> logout(@Header("Authorization") String token);

    @POST("api/auth/refresh")
    Call<ApiResponse<TokenResponse>> refreshToken(@Header("Authorization") String token);

    // MEDICINE ENDPOINTS (Feature 2 & 3)
    
    @GET("api/medicines")
    Call<ApiResponse<MedicineListResponse>> getAllMedicines(
            @Header("Authorization") String token,
            @Query("skip") int skip,
            @Query("take") int take,
            @Query("filter") String filter,
            @Query("search") String search
    );

    @GET("api/medicines/{id}")
    Call<ApiResponse<Medicine>> getMedicineById(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @POST("api/medicines")
    Call<ApiResponse<Medicine>> addMedicine(
            @Header("Authorization") String token,
            @Body Medicine medicine
    );

    @PUT("api/medicines/{id}")
    Call<ApiResponse<Medicine>> updateMedicine(
            @Header("Authorization") String token,
            @Path("id") int id,
            @Body Medicine medicine
    );

    @DELETE("api/medicines/{id}")
    Call<ApiResponse<DeleteResponse>> deleteMedicine(
            @Header("Authorization") String token,
            @Path("id") int id
    );

    @Multipart
    @POST("api/medicines/upload-image")
    Call<ApiResponse<ImageUploadResponse>> uploadMedicineImage(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file
    );

    @GET("api/medicines/validate-barcode/{barcode}")
    Call<ApiResponse<BarcodeValidationResponse>> validateBarcode(
            @Header("Authorization") String token,
            @Path("barcode") String barcode
    );

    // STATISTICS ENDPOINTS (Feature 4)
    
    @GET("api/medicines/stats")
    Call<ApiResponse<StatsResponse>> getMedicineStats(
            @Header("Authorization") String token
    );

    // USER PROFILE ENDPOINTS (Feature 5)
    
    @GET("api/users/profile")
    Call<ApiResponse<UserProfile>> getUserProfile(
            @Header("Authorization") String token
    );

    @PUT("api/users/profile")
    Call<ApiResponse<UserProfile>> updateUserProfile(
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );

    @POST("api/users/change-password")
    Call<ApiResponse<Void>> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest request
    );

    // INNER CLASSES FOR REQUEST/RESPONSE

    class RegisterRequest {
        public String email;
        public String password;
        public String fullName;
    }

    class LoginRequest {
        public String email;
        public String password;
    }

    class ChangePasswordRequest {
        public String currentPassword;
        public String newPassword;
    }

    class UpdateProfileRequest {
        public String newFullName;
    }

    class TokenResponse {
        public String token;
        public long tokenExpiry;
    }

    class MedicineListResponse {
        public List<Medicine> items;
        public int totalCount;
        public int pageNumber;
        public int pageSize;
        public int totalPages;
    }

    class DeleteResponse {
        public int deletedMedicineId;
    }

    class ImageUploadResponse {
        public String imagePath;
        public String fileName;
        public long fileSize;
        public String uploadedAt;
    }

    class BarcodeValidationResponse {
        public boolean exists;
        public Medicine medicine;
    }
}
