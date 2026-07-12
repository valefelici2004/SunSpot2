package com.group3boot.sunspot.util;

import android.app.Application;

import com.group3boot.sunspot.database.SpotRoomDatabase;
import com.group3boot.sunspot.repository.WeatherRepository;
import com.group3boot.sunspot.service.WeatherAPIService;
import com.group3boot.sunspot.source.BaseSpotLocalDataSource;
import com.group3boot.sunspot.source.BaseSpotRemoteDataSource;
import com.group3boot.sunspot.source.SpotFirebaseDataSource;
import com.group3boot.sunspot.source.SpotLocalDataSource;
import com.group3boot.sunspot.source.SpotMockDataSource;
import com.group3boot.sunspot.source.WeatherRemoteDataSource;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Restituisce un'istanza di ServiceLocator.
     * @return Un'istanza di ServiceLocator.
     */
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

    /**
     * Restituisce un'istanza di WeatherAPIService usando Retrofit.
     * @return un'istanza di WeatherAPIService.
     */
    public WeatherAPIService getWeatherAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEATHER_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(WeatherAPIService.class);
    }

    /**
     * Restituisce un'istanza di SpotRoomDatabase per gestire il database Room.
     * @param application Parametro per accedere allo stato globale dell'applicazione.
     * @return Un'istanza di SpotRoomDatabase.
     */
    public SpotRoomDatabase getSpotDao(Application application) {
        return SpotRoomDatabase.getDatabase(application);
    }

    /**
     * Restituisce un'istanza di SpotRepository, già assemblata con le DataSource corrette.
     * @param application Parametro per accedere allo stato globale dell'applicazione.
     * @param debugMode Parametro per stabilire se l'app gira in modalità debug.
     * @return Un'istanza di SpotRepository.
     */
    public SpotRepository getSpotRepository(Application application, boolean debugMode) {
        BaseSpotRemoteDataSource spotRemoteDataSource;
        BaseSpotLocalDataSource spotLocalDataSource;
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);

        if (debugMode) {
            JSONParserUtils jsonParserUtil = new JSONParserUtils(application);
            spotRemoteDataSource = new SpotMockDataSource(jsonParserUtil);
        } else {
            spotRemoteDataSource = new SpotFirebaseDataSource();
        }

        spotLocalDataSource = new SpotLocalDataSource(getSpotDao(application), sharedPreferencesUtil);

        return new SpotRepository(spotRemoteDataSource, spotLocalDataSource);
    }

    /**
     * Restituisce un'istanza di WeatherRepository.
     * @return Un'istanza di WeatherRepository.
     */
    public WeatherRepository getWeatherRepository() {
        WeatherRemoteDataSource weatherRemoteDataSource = new WeatherRemoteDataSource(getWeatherAPIService());
        return new WeatherRepository(weatherRemoteDataSource);
    }
}