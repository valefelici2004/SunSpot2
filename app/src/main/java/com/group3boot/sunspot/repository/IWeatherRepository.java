package com.group3boot.sunspot.repository;

public interface IWeatherRepository {
    void fetchWeather(double latitude, double longitude);
}