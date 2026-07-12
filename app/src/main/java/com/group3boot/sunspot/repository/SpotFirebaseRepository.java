package com.group3boot.sunspot.repository;

import android.app.Application;

import com.group3boot.sunspot.database.SpotDao;
import com.group3boot.sunspot.database.SpotRoomDatabase;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.service.ServiceLocator;
import com.group3boot.sunspot.util.SpotResponseCallback;

public class SpotFirebaseRepository implements ISpotRepository {

    private final Application application;
    private final SpotDao spotDao;
    private final SpotResponseCallback responseCallback;

    public SpotFirebaseRepository(Application application, SpotResponseCallback responseCallback) {
        this.application = application;
        this.spotDao = ServiceLocator.getInstance().getSpotsDB(application).spotDao();
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchSpots(long lastUpdate) {
        // TODO: quando impari Firebase, qui farai la query a Firestore
        // per ora, come placeholder, potresti leggere solo da Room
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(spotDao.getAll(), System.currentTimeMillis());
        });
    }

    @Override
    public void addSpot(Spot spot) {
        // TODO: salva su Firebase, poi anche in Room (come cache)
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotDao.insert(spot);
        });
    }

    @Override
    public void updateSpot(Spot spot) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotDao.updateSpot(spot);
        });
    }

    @Override
    public void getFavoriteSpots() {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(spotDao.getLiked(), System.currentTimeMillis());
        });
    }

    @Override
    public void getMySpots() {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(spotDao.getMySpots(), System.currentTimeMillis());
        });
    }
}