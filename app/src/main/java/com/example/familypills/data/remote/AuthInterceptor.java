package com.example.familypills.data.remote;

import android.content.SharedPreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import com.example.familypills.utils.Constants;

import java.io.IOException;

/**
 * OkHttp Interceptor for adding JWT token to requests
 */
public class AuthInterceptor implements Interceptor {
    private final SharedPreferences preferences;

    public AuthInterceptor(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Skip auth for login/register endpoints
        if (originalRequest.url().encodedPath().contains("/api/auth/register") ||
            originalRequest.url().encodedPath().contains("/api/auth/login")) {
            return chain.proceed(originalRequest);
        }

        // Add token to other requests
        String token = preferences.getString(Constants.PREF_AUTH_TOKEN, null);
        if (token != null && !token.isEmpty()) {
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authenticatedRequest);
        }

        return chain.proceed(originalRequest);
    }
}
