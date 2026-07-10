package com.group3boot.sunspot.service;

import static com.group3boot.sunspot.util.Constants.*;

import com.group3boot.sunspot.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPIService {

    @GET(WEATHER_FORECAST_ENDPOINT)
    Call<WeatherResponse> getWeather(
    @Query(WEATHER_LATITUDE_PARAMETER) double latitude,
    @Query(WEATHER_LONGITUDE_PARAMETER) double longitude,
    @Query(WEATHER_CURRENT_PARAMETER) String current,
    @Query(WEATHER_DAILY_PARAMETER) String daily,
    @Query(WEATHER_FORECAST_DAYS_PARAMETER) int forecastDays,
    @Query(WEATHER_TIMEZONE_PARAMETER) String timezone);
}