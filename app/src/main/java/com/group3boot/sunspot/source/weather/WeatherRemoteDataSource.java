package com.group3boot.sunspot.source.weather;

import androidx.annotation.NonNull;

import com.group3boot.sunspot.models.WeatherResponse;
import com.group3boot.sunspot.repository.weather.WeatherCallback;
import com.group3boot.sunspot.service.WeatherAPIService;
import com.group3boot.sunspot.util.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Classe per ottenere il meteo da Open-Meteo usando Retrofit.
 */
public class WeatherRemoteDataSource {

    private final WeatherAPIService weatherAPIService;
    private WeatherCallback weatherCallback;

    public WeatherRemoteDataSource(WeatherAPIService weatherAPIService) {
        this.weatherAPIService = weatherAPIService;
    }

    public void setWeatherCallback(WeatherCallback weatherCallback) {
        this.weatherCallback = weatherCallback;
    }

    public void getWeather(double latitude, double longitude) {
        Call<WeatherResponse> call = weatherAPIService.getWeather(
                latitude,
                longitude,
                Constants.WEATHER_CURRENT_VALUE,
                Constants.WEATHER_DAILY_VALUE,
                1,
                "auto");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call,
                                   @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherCallback.onSuccess(response.body());
                } else {
                    weatherCallback.onFailure(new Exception(Constants.WEATHER_API_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                weatherCallback.onFailure(new Exception(t));
            }
        });
    }
}