package com.group3boot.sunspot.ui.home.weatherviewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3boot.sunspot.models.WeatherResult;
import com.group3boot.sunspot.repository.weather.WeatherRepository;

public class WeatherViewModel extends ViewModel {

    private final WeatherRepository weatherRepository;
    private MutableLiveData<WeatherResult> weatherMutableLiveData;

    public WeatherViewModel(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    public MutableLiveData<WeatherResult> getWeather(double latitude, double longitude) {
        weatherMutableLiveData = weatherRepository.fetchWeather(latitude, longitude);
        return weatherMutableLiveData;
    }
}