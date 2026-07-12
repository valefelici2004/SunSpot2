package com.group3boot.sunspot.util;

import android.content.Context;

import com.google.gson.Gson;
import com.group3boot.sunspot.models.WeatherResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JSONParserUtils {

    public Context context;

    public JSONParserUtils(Context context) {
        this.context = context;
    }

    // Parsing specifico per il meteo (Open-Meteo)
    public WeatherResponse parseWeatherJSONWithGson(String filename) throws IOException {
        InputStream inputStream = context.getAssets().open(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return new Gson().fromJson(bufferedReader, WeatherResponse.class);
    }

    // Parsing generico: funziona con QUALSIASI classe modello
    public <T> T parseJSONWithGson(String filename, Class<T> classOfT) throws IOException {
        InputStream inputStream = context.getAssets().open(filename);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return new Gson().fromJson(bufferedReader, classOfT);
    }
}