package com.group3boot.sunspot.source.spot;

import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.repository.spot.SpotCallback;

public abstract class BaseSpotRemoteDataSource {
    protected SpotCallback spotCallback;

    public void setSpotCallback(SpotCallback spotCallback) {
        this.spotCallback = spotCallback;
    }

    public abstract void getSpots();
    public abstract void addSpot(Spot spot);
    public abstract void updateSpot(Spot spot);
    public abstract void deleteSpot(Spot spot);
}