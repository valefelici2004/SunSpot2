package com.group3boot.sunspot.repository.spot;

import androidx.lifecycle.MutableLiveData;

import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.SpotResult;
import com.group3boot.sunspot.source.spot.BaseSpotLocalDataSource;
import com.group3boot.sunspot.source.spot.BaseSpotRemoteDataSource;

import java.util.Collections;
import java.util.List;

/**
 * Repository class per ottenere gli spot da fonte locale o remota.
 */
public class SpotRepository implements SpotCallback {

    private final MutableLiveData<SpotResult> allSpotsMutableLiveData;
    private final MutableLiveData<SpotResult> favoriteSpotsMutableLiveData;
    private final MutableLiveData<SpotResult> mySpotsMutableLiveData;
    private final MutableLiveData<SpotResult> addSpotMutableLiveData;

    private final BaseSpotRemoteDataSource spotRemoteDataSource;
    private final BaseSpotLocalDataSource spotLocalDataSource;

    public SpotRepository(BaseSpotRemoteDataSource spotRemoteDataSource,
                          BaseSpotLocalDataSource spotLocalDataSource) {

        allSpotsMutableLiveData = new MutableLiveData<>();
        favoriteSpotsMutableLiveData = new MutableLiveData<>();
        mySpotsMutableLiveData = new MutableLiveData<>();
        addSpotMutableLiveData = new MutableLiveData<>();
        this.spotRemoteDataSource = spotRemoteDataSource;
        this.spotLocalDataSource = spotLocalDataSource;
        this.spotRemoteDataSource.setSpotCallback(this);
        this.spotLocalDataSource.setSpotCallback(this);
    }

    public MutableLiveData<SpotResult> fetchSpots(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdate > Constants.SPOT_FRESH_TIMEOUT) {
            spotRemoteDataSource.getSpots();
        } else {
            spotLocalDataSource.getSpots();
        }

        return allSpotsMutableLiveData;
    }

    public MutableLiveData<SpotResult> getFavoriteSpots() {
        spotLocalDataSource.getFavoriteSpots();
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

    public void updateSpot(Spot spot) {
        spotLocalDataSource.updateSpot(spot);
    }

    public void deleteSpot(Spot spot) {
        spotRemoteDataSource.deleteSpot(spot);
    }

    // --- Metodi del callback ---

    @Override
    public void onSuccessFromRemote(List<Spot> spotList, long lastUpdate) {
        spotLocalDataSource.insertSpots(spotList);
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        SpotResult.Error result = new SpotResult.Error(exception.getMessage());
        allSpotsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(List<Spot> spotList) {
        SpotResult.Success result = new SpotResult.Success(spotList);
        allSpotsMutableLiveData.postValue(result);
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        SpotResult.Error resultError = new SpotResult.Error(exception.getMessage());
        allSpotsMutableLiveData.postValue(resultError);
        favoriteSpotsMutableLiveData.postValue(resultError);
        mySpotsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onSpotFavoriteStatusChanged(Spot spot, List<Spot> favoriteSpots) {
        SpotResult allSpotsResult = allSpotsMutableLiveData.getValue();

        if (allSpotsResult != null && allSpotsResult.isSuccess()) {
            List<Spot> oldAllSpots = ((SpotResult.Success) allSpotsResult).getData();
            if (oldAllSpots.contains(spot)) {
                oldAllSpots.set(oldAllSpots.indexOf(spot), spot);
                allSpotsMutableLiveData.postValue(allSpotsResult);
            }
        }
        favoriteSpotsMutableLiveData.postValue(new SpotResult.Success(favoriteSpots));
    }

    @Override
    public void onAddSpotSuccess(Spot spot) {
        spot.setAddedByMe(true);
        spotLocalDataSource.insertSpot(spot);
        addSpotMutableLiveData.postValue(new SpotResult.Success(Collections.singletonList(spot)));
    }

    @Override
    public void onDeleteSpotSuccess(Spot spot) {
        // Dopo che Firestore conferma la cancellazione, rimuoviamo anche la copia locale
        spotLocalDataSource.deleteSpot(spot);
    }
}