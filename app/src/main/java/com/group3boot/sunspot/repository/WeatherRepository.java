package com.group3boot.sunspot.repository;

import androidx.lifecycle.MutableLiveData;

import com.group3boot.sunspot.models.WeatherResult;
import com.group3boot.sunspot.source.WeatherCallback;
import com.group3boot.sunspot.source.WeatherRemoteDataSource;

/**
 * Repository class per ottenere il meteo da Open-Meteo.
 */
public class WeatherRepository implements WeatherCallback {

    private final MutableLiveData<WeatherResult> weatherMutableLiveData;
    private final WeatherRemoteDataSource weatherRemoteDataSource;

    public WeatherRepository(WeatherRemoteDataSource weatherRemoteDataSource) {
        weatherMutableLiveData = new MutableLiveData<>();
        this.weatherRemoteDataSource = weatherRemoteDataSource;
        this.weatherRemoteDataSource.setWeatherCallback(this);
    }

    public MutableLiveData<WeatherResult> fetchWeather(double latitude, double longitude) {
        weatherRemoteDataSource.getWeather(latitude, longitude);
        return weatherMutableLiveData;
    }

    @Override
    public void onSuccess(com.group3boot.sunspot.models.WeatherResponse weatherResponse) {
        weatherMutableLiveData.postValue(new WeatherResult.Success(weatherResponse));
    }

    @Override
    public void onFailure(Exception exception) {
        weatherMutableLiveData.postValue(new WeatherResult.Error(exception.getMessage()));
    }
}}