package com.group3boot.sunspot.source.spot;

import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.repository.spot.SpotCallback;

import java.util.List;

public abstract class BaseSpotLocalDataSource {
    protected SpotCallback spotCallback;

    public void setSpotCallback(SpotCallback spotCallback) {
        this.spotCallback = spotCallback;
    }

    public abstract void getSpots();
    public abstract void getFavoriteSpots(String userId);
    public abstract void getMySpots(String userId);
    public abstract void updateSpot(Spot spot);
    public abstract void insertSpot(Spot spot);
    public abstract void insertSpots(List<Spot> spotList);
    public abstract void deleteSpot(Spot spot);
}