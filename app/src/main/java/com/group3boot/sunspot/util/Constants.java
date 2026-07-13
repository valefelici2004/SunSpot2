package com.group3boot.sunspot.util;

public class Constants {

    // Database
    public static final int DATABASE_VERSION = 1;
    public static final String SAVED_SPOTS_DATABASE = "sunspot_database";

    // Weather API (Open-Meteo)
    public static final String WEATHER_API_BASE_URL = "https://api.open-meteo.com/";
    public static final String WEATHER_FORECAST_ENDPOINT = "v1/forecast";
    public static final String WEATHER_LATITUDE_PARAMETER = "latitude";
    public static final String WEATHER_LONGITUDE_PARAMETER = "longitude";
    public static final String WEATHER_CURRENT_PARAMETER = "current";
    public static final String WEATHER_DAILY_PARAMETER = "daily";
    public static final String WEATHER_FORECAST_DAYS_PARAMETER = "forecast_days";
    public static final String WEATHER_TIMEZONE_PARAMETER = "timezone";

    public static final String WEATHER_CURRENT_VALUE = "temperature_2m,weather_code,is_day";
    public static final String WEATHER_DAILY_VALUE = "sunrise,sunset";

    // SharedPreferences
    public static final String SHARED_PREFERENCES_FILENAME = "com.group3boot.sunspot.preferences";
    public static final String SHARED_PREFERENCES_LAST_SPOT_UPDATE = "last_spot_update";

    // Mock data
    public static final String SAMPLE_SPOT_JSON_FILENAME = "sample_spot_response.json";

    // Navigation / Parcelable bundle key
    public static final String BUNDLE_KEY_CURRENT_SPOT = "current_spot";

    // Cache timeout
    public static final int SPOT_FRESH_TIMEOUT = 1000 * 60 * 5; // 5 minuti

    // Error messages
    public static final String RETROFIT_ERROR = "retrofit_error";
    public static final String MOCK_DATA_ERROR = "mock_data_error";
    public static final String UNEXPECTED_ERROR = "unexpected_error";
    public static final String WEATHER_API_ERROR = "weather_api_error";
    public static final String FIREBASE_ERROR = "firebase_error";

    public static final String FIREBASE_USERS_COLLECTION = "users";

    public static final String WEAK_PASSWORD_ERROR = "weak_password_error";
    public static final String INVALID_CREDENTIALS_ERROR = "invalid_credentials_error";
    public static final String INVALID_USER_ERROR = "invalid_user_error";
    public static final String USER_COLLISION_ERROR = "user_collision_error";

    public static final int MINIMUM_LENGTH_PASSWORD = 6;

    public static final String BUNDLE_KEY_LATITUDE = "latitude";
    public static final String BUNDLE_KEY_LONGITUDE = "longitude";

    public static final String SPOT_TYPE_SUNRISE = "sunrise";
    public static final String SPOT_TYPE_SUNSET = "sunset";
}