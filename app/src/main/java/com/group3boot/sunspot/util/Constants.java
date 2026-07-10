package com.group3boot.sunspot.util;

public class Constants {

    public static final String WEATHER_API_BASE_URL = "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current=temperature_2m,weather_code,is_day&daily=sunrise,sunset&forecast_days=1&timezone=auto";
    public static final String WEATHER_FORECAST_ENDPOINT = "v1/forecast";
    public static final String WEATHER_LATITUDE_PARAMETER = "latitude";
    public static final String WEATHER_LONGITUDE_PARAMETER = "longitude";
    public static final String WEATHER_CURRENT_PARAMETER = "current";
    public static final String WEATHER_DAILY_PARAMETER = "daily";
    public static final String WEATHER_FORECAST_DAYS_PARAMETER = "forecast_days";
    public static final String WEATHER_TIMEZONE_PARAMETER = "timezone";


    public static final int DATABASE_VERSION = 1;
    public static final String SAVED_SPOTS_DATABASE = "sunspot_database";
}
