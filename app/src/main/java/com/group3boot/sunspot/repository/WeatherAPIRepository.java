package com.group3boot.sunspot.repository;

import android.app.Application;

import androidx.annotation.NonNull;

import com.group3boot.sunspot.models.WeatherResponse;
import com.group3boot.sunspot.service.ServiceLocator;
import com.group3boot.sunspot.service.WeatherAPIService;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.WeatherResponseCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherAPIRepository implements IWeatherRepository {

    private final Application application;
    private final WeatherAPIService weatherAPIService;
    private final WeatherResponseCallback responseCallback;

    public WeatherAPIRepository(Application application, WeatherResponseCallback responseCallback) {
        this.application = application;
        this.weatherAPIService = ServiceLocator.getInstance().getWeatherAPIService();
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchWeather(double latitude, double longitude) {
        Call<WeatherResponse> call = weatherAPIService.getWeather(
                latitude,
                longitude,
                Constants.WEATHER_CURRENT_VALUE,
                Constants.WEATHER_DAILY_VALUE,
                1,
                "auto"
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call,
                                   @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    responseCallback.onSuccess(response.body());
                } else {
                    responseCallback.onFailure(application.getString(R.string.error_retrieving_weather));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                responseCallback.onFailure(application.getString(R.string.error_retrieving_weather));
            }
        });
    }
}