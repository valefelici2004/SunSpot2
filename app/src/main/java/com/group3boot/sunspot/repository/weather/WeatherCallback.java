package com.group3boot.sunspot.repository.weather;

import com.group3boot.sunspot.models.WeatherResponse;

public interface WeatherCallback {
    void onSuccess(WeatherResponse weatherResponse);
    void onFailure(Exception exception);
}