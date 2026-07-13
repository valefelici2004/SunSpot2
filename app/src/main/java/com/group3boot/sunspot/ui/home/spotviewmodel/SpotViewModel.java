package com.group3boot.sunspot.ui.home.spotviewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.SpotResult;
import com.group3boot.sunspot.repository.spot.SpotRepository;

public class SpotViewModel extends ViewModel {

    private final SpotRepository spotRepository;

    private MutableLiveData<SpotResult> allSpotsMutableLiveData;
    private MutableLiveData<SpotResult> favoriteSpotsMutableLiveData;
    private MutableLiveData<SpotResult> mySpotsMutableLiveData;

    public SpotViewModel(SpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    public MutableLiveData<SpotResult> getAllSpots(long lastUpdate) {
        if (allSpotsMutableLiveData == null) {
            allSpotsMutableLiveData = spotRepository.fetchSpots(lastUpdate);
        } else {
            spotRepository.fetchSpots(lastUpdate);
        }
        return allSpotsMutableLiveData;
    }

    public MutableLiveData<SpotResult> getFavoriteSpots() {
        if (favoriteSpotsMutableLiveData == null) {
            favoriteSpotsMutableLiveData = spotRepository.getFavoriteSpots();
        } else {
            spotRepository.getFavoriteSpots();
        }
        return favoriteSpotsMutableLiveData;
    }

    public MutableLiveData<SpotResult> getMySpots(String userId) {
        if (mySpotsMutableLiveData == null) {
            mySpotsMutableLiveData = spotRepository.getMySpots(userId);
        } else {
            spotRepository.getMySpots(userId);
        }
        return mySpotsMutableLiveData;
    }

    public MutableLiveData<SpotResult> addSpot(Spot spot) {
        return spotRepository.addSpot(spot);
    }

    public void updateSpot(Spot spot) {
        spotRepository.updateSpot(spot);
    }

    public void deleteSpot(Spot spot) {
        spotRepository.deleteSpot(spot);
    }
}