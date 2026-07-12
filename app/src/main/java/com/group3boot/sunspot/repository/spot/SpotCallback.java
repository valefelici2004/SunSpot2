package com.group3boot.sunspot.repository.spot;

import com.group3boot.sunspot.models.Spot;

import java.util.List;

public interface SpotCallback {
    void onSuccessFromRemote(List<Spot> spotList, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Spot> spotList);
    void onFailureFromLocal(Exception exception);
    void onSpotFavoriteStatusChanged(Spot spot, List<Spot> favoriteSpots);
    void onAddSpotSuccess(Spot spot);
    void onDeleteSpotSuccess(List<Spot> mySpots);
}