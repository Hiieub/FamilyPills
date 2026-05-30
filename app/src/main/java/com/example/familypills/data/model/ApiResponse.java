package com.example.familypills.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Generic API Response wrapper for all endpoints
 * @param <T> Data type
 */
public class ApiResponse<T> {
    private String message;
    private T data;
    private ErrorInfo error;
    private String timestamp;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorInfo getError() {
        return error;
    }

    public void setError(ErrorInfo error) {
        this.error = error;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Inner class for error details
     */
    public static class ErrorInfo {
        private String code;
        private String details;

        public ErrorInfo(String code, String details) {
            this.code = code;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public String getDetails() {
            return details;
        }
    }

    public boolean isSuccess() {
        return error == null;
    }
}
