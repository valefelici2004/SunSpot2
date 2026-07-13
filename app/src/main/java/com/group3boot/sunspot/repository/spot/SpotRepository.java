package com.group3boot.sunspot.repository.spot;

import androidx.lifecycle.MutableLiveData;

import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.SpotResult;
import com.group3boot.sunspot.source.spot.BaseSpotLocalDataSource;
import com.group3boot.sunspot.source.spot.BaseSpotRemoteDataSource;

import java.util.Collections;
import java.util.List;

public class SpotRepository implements SpotCallback {

    private final MutableLiveData<SpotResult> allSpotsMutableLiveData;
    private final MutableLiveData<SpotResult> favoriteSpotsMutableLiveData;
    private final MutableLiveData<SpotResult> mySpotsMutableLiveData;
    private final MutableLiveData<SpotResult> addSpotMutableLiveData;
    private final MutableLiveData<SpotResult> updateSpotMutableLiveData;

    private final BaseSpotRemoteDataSource spotRemoteDataSource;
    private final BaseSpotLocalDataSource spotLocalDataSource;

    public SpotRepository(BaseSpotRemoteDataSource spotRemoteDataSource,
                          BaseSpotLocalDataSource spotLocalDataSource) {

        allSpotsMutableLiveData = new MutableLiveData<>();
        favoriteSpotsMutableLiveData = new MutableLiveData<>();
        mySpotsMutableLiveData = new MutableLiveData<>();
        addSpotMutableLiveData = new MutableLiveData<>();
        updateSpotMutableLiveData = new MutableLiveData<>();
        this.spotRemoteDataSource = spotRemoteDataSource;
        this.spotLocalDataSource = spotLocalDataSource;
        this.spotRemoteDataSource.setSpotCallback(this);
        this.spotLocalDataSource.setSpotCallback(this);
    }

    public MutableLiveData<SpotResult> fetchSpots(long lastUpdate) {
        spotRemoteDataSource.getSpots();
        return allSpotsMutableLiveData;
    }

    public MutableLiveData<SpotResult> getFavoriteSpots(String userId) {
        spotLocalDataSource.getFavoriteSpots(userId);
        return favoriteSpotsMutableLiveData;
    }

    public MutableLiveData<SpotResult> getMySpots(String userId) {
        spotLocalDataSource.getMySpots(userId);
        return mySpotsMutableLiveData;
    }

    public MutableLiveData<SpotResult> addSpot(Spot spot) {
        spotRemoteDataSource.addSpot(spot);
        return addSpotMutableLiveData;
    }

    public MutableLiveData<SpotResult> toggleFavorite(Spot spot, String userId) {
        spot.toggleFavorite(userId);
        spotRemoteDataSource.updateSpot(spot);
        return updateSpotMutableLiveData;
    }

    public void deleteSpot(Spot spot) {
        spotRemoteDataSource.deleteSpot(spot);
    }

    // --- Metodi del callback ---

    @Override
    public void onSuccessFromRemote(List<Spot> spotList, long lastUpdate) {
        spotLocalDataSource.insertSpots(spotList);
        allSpotsMutableLiveData.postValue(new SpotResult.Success(spotList));
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        allSpotsMutableLiveData.postValue(new SpotResult.Error(exception.getMessage()));
    }

    @Override
    public void onSyncComplete() {
        // Non serve fare nulla qui: allSpotsMutableLiveData è già stato aggiornato in onSuccessFromRemote
    }

    @Override
    public void onMySpotsReady(List<Spot> spotList) {
        mySpotsMutableLiveData.postValue(new SpotResult.Success(spotList));
    }

    @Override
    public void onFavoriteSpotsReady(List<Spot> spotList) {
        favoriteSpotsMutableLiveData.postValue(new SpotResult.Success(spotList));
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        SpotResult.Error resultError = new SpotResult.Error(exception.getMessage());
        favoriteSpotsMutableLiveData.postValue(resultError);
        mySpotsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onAddSpotSuccess(Spot spot) {
        spotLocalDataSource.insertSpot(spot);
        addSpotMutableLiveData.postValue(new SpotResult.Success(Collections.singletonList(spot)));
    }

    @Override
    public void onUpdateSpotSuccess(Spot spot) {
        spotLocalDataSource.updateSpot(spot);
        updateSpotMutableLiveData.postValue(new SpotResult.Success(Collections.singletonList(spot)));
    }

    @Override
    public void onDeleteSpotSuccess(Spot spot) {
        spotLocalDataSource.deleteSpot(spot);
    }
}