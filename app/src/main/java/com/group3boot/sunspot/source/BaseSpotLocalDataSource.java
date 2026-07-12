package com.group3boot.sunspot.source;

import com.group3boot.sunspot.models.Spot;

import java.util.List;

/**
 * Classe base per ottenere gli spot dal database locale (Room).
 */
public abstract class BaseSpotLocalDataSource {
    protected SpotCallback spotCallback;

    public void setSpotCallback(SpotCallback spotCallback) {
        this.spotCallback = spotCallback;
    }

    public abstract void getSpots();
    public abstract void getFavoriteSpots();
    public abstract void getMySpots();
    public abstract void updateSpot(Spot spot);
    public abstract void insertSpot(Spot spot);
    public abstract void insertSpots(List<Spot> spotList);
    public abstract void deleteSpot(Spot spot);
}