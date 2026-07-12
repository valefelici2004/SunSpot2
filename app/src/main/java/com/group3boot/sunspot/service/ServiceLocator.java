package com.group3boot.sunspot.service;


import android.app.Application;

import com.group3boot.sunspot.database.SpotRoomDatabase;
import com.group3boot.sunspot.util.Constants;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder().build();

    public WeatherAPIService getWeatherAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEATHER_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(WeatherAPIService.class);
    }

    public SpotRoomDatabase getSpotsDB(Application application) {
        return SpotRoomDatabase.getDatabase(application);
    }
}