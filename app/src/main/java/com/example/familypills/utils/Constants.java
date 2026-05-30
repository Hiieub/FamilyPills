package com.example.familypills.utils;

/**
 * Application-wide constants
 */
public class Constants {

    // API Configuration
    public static final String API_BASE_URL_EMULATOR = "http://10.0.2.2:5000/";
    public static final String API_BASE_URL_LOCAL = "http://localhost:5000/";
    public static final String API_BASE_URL_DEVICE = "http://192.168.1.100:5000/"; // Change IP as needed
    public static final String API_BASE_URL_PRODUCTION = "https://api.familypills.com/";

    // API Endpoints
    public static final String ENDPOINT_AUTH_REGISTER = "api/auth/register";
    public static final String ENDPOINT_AUTH_LOGIN = "api/auth/login";
    public static final String ENDPOINT_AUTH_LOGOUT = "api/auth/logout";
    public static final String ENDPOINT_AUTH_REFRESH = "api/auth/refresh";

    public static final String ENDPOINT_MEDICINES_LIST = "api/medicines";
    public static final String ENDPOINT_MEDICINES_ADD = "api/medicines";
    public static final String ENDPOINT_MEDICINES_UPLOAD_IMAGE = "api/medicines/upload-image";
    public static final String ENDPOINT_MEDICINES_VALIDATE_BARCODE = "api/medicines/validate-barcode/";
    public static final String ENDPOINT_MEDICINES_STATS = "api/medicines/stats";

    public static final String ENDPOINT_USER_PROFILE = "api/users/profile";
    public static final String ENDPOINT_USER_CHANGE_PASSWORD = "api/users/change-password";

    // SharedPreferences Keys
    public static final String PREF_NAME = "FamilyPillsPreferences";
    public static final String PREF_AUTH_TOKEN = "auth_token";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_TOKEN_EXPIRY = "token_expiry";

    // HTTP Headers
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TYPE_JSON = "application/json";

    // Date & Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT_DISPLAY = "dd/MM/yyyy HH:mm";

    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_MEDICINE_NAME_LENGTH = 255;
    public static final int MAX_BARCODE_LENGTH = 100;
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final long MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB

    // UI Configuration
    public static final int PAGINATION_PAGE_SIZE = 10;
    public static final int PAGINATION_INITIAL_SKIP = 0;

    // Medicine Filter Types
    public static final String FILTER_ALL = "all";
    public static final String FILTER_RUNNING_LOW = "runningLow";
    public static final String FILTER_EXPIRED = "expired";

    // Status Labels
    public static final String STATUS_RUNNING_LOW = "Sắp hết";
    public static final String STATUS_EXPIRED = "Hết hạn";
    public static final String STATUS_OK = "Bình thường";

    // Units
    public static final String UNIT_TABLET = "viên";
    public static final String UNIT_ML = "ml";
    public static final String UNIT_GRAM = "g";
    public static final String UNIT_BOTTLE = "chai";
    public static final String UNIT_BOX = "hộp";

    // Retrofit Configuration
    public static final long RETROFIT_TIMEOUT_SECONDS = 30;
    public static final int RETROFIT_CACHE_SIZE_MB = 10;

    // Error Codes
    public static final String ERROR_UNAUTHORIZED = "UNAUTHORIZED";
    public static final String ERROR_NOT_FOUND = "NOT_FOUND";
    public static final String ERROR_VALIDATION = "VALIDATION_ERROR";
    public static final String ERROR_NETWORK = "NETWORK_ERROR";
    public static final String ERROR_UNKNOWN = "UNKNOWN_ERROR";

    // Notification IDs
    public static final int NOTIFICATION_ID_RUNNING_LOW = 1001;
    public static final int NOTIFICATION_ID_EXPIRED = 1002;

    // Request Codes
    public static final int REQUEST_CODE_CAMERA = 101;
    public static final int REQUEST_CODE_GALLERY = 102;
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 103;

    // Permission Request Codes
    public static final int PERMISSION_REQUEST_CAMERA = 201;
    public static final int PERMISSION_REQUEST_LOCATION = 202;
    public static final int PERMISSION_REQUEST_READ_STORAGE = 203;
    public static final int PERMISSION_REQUEST_WRITE_STORAGE = 204;
}
