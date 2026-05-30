package com.example.familypills.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Retrofit client singleton for API communication
 * Handles base URL, interceptors, and JSON conversion
 */
public class RetrofitClient {
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:5000/";
    private static final String BASE_URL_PHYSICAL = "http://localhost:5000/";

    private static String getBaseUrl() {
        return isEmulator() ? BASE_URL_EMULATOR : BASE_URL_PHYSICAL;
    }

    private static boolean isEmulator() {
        return (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.HARDWARE.contains("goldfish")
                || android.os.Build.HARDWARE.contains("ranchu")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || android.os.Build.PRODUCT.contains("sdk_google")
                || android.os.Build.PRODUCT.contains("google_sdk")
                || android.os.Build.PRODUCT.contains("sdk")
                || android.os.Build.PRODUCT.contains("sdk_x86")
                || android.os.Build.PRODUCT.contains("vbox86p")
                || android.os.Build.PRODUCT.contains("emulator")
                || android.os.Build.PRODUCT.contains("simulator");
    }

    private static Retrofit retrofit = null;
    private static final long TIMEOUT_SECONDS = 30;

    /**
     * Get or create Retrofit instance
     * @param context Android context for SharedPreferences
     * @return ApiService instance
     */
    public static ApiService getApiService(Context context) {
        if (retrofit == null) {
            retrofit = createRetrofitInstance(context);
        }
        return retrofit.create(ApiService.class);
    }

    /**
     * Create new Retrofit instance with all configurations
     */
    private static Retrofit createRetrofitInstance(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("FamilyPillsPreferences", Context.MODE_PRIVATE);

        // HTTP Logging Interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Authentication Interceptor
        AuthInterceptor authInterceptor = new AuthInterceptor(preferences);

        // OkHttp Client
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        // Retrofit instance
        return new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Reset retrofit instance (for logout or base URL change)
     */
    public static void reset() {
        retrofit = null;
    }
}
