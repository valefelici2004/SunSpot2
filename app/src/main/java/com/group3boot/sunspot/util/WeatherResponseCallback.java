package com.group3boot.sunspot.util;

import com.group3boot.sunspot.models.WeatherResponse;

public interface WeatherResponseCallback {
    void onSuccess(WeatherResponse weatherResponse);
    void onFailure(String errorMessage);
}