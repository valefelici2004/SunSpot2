package com.group3boot.sunspot.models;

import java.util.List;

public class SpotAPIResponse {
    private List<Spot> spots;

    public SpotAPIResponse() {}

    public List<Spot> getSpots() {
        return spots;
    }

    public void setSpots(List<Spot> spots) {
        this.spots = spots;
    }
}