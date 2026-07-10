package com.group3boot.sunspot.repository;

import com.group3boot.sunspot.models.Spot;

public interface ISpotRepository {
    void fetchSpots(long lastUpdate);
    void addSpot(Spot spot);
    void updateSpot(Spot spot);
    void getFavoriteSpots();
    void getMySpots();
}
