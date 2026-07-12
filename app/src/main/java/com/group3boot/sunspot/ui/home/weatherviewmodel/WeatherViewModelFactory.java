package com.group3boot.sunspot.ui.home.weatherviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.group3boot.sunspot.repository.weather.WeatherRepository;

/**
 * Factory personalizzata per creare WeatherViewModel con un costruttore custom.
 */
public class WeatherViewModelFactory implements ViewModelProvider.Factory {

    private final WeatherRepository weatherRepository;

    public WeatherViewModelFactory(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WeatherViewModel(weatherRepository);
    }
}