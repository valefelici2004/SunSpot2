package com.group3boot.sunspot.repository.spot;

import com.group3boot.sunspot.models.Spot;

import java.util.List;

public interface SpotCallback {
    void onSuccessFromRemote(List<Spot> spotList, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSyncComplete();
    void onMySpotsReady(List<Spot> spotList);
    void onFavoriteSpotsReady(List<Spot> spotList);
    void onFailureFromLocal(Exception exception);
    void onAddSpotSuccess(Spot spot);
    void onUpdateSpotSuccess(Spot spot);
    void onDeleteSpotSuccess(Spot spot);
}