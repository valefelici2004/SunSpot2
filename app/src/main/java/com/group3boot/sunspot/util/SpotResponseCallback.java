package com.group3boot.sunspot.util;

import com.group3boot.sunspot.models.Spot;

import java.util.List;

public interface SpotResponseCallback {
    void onSuccess(List<Spot> spotList, long lastUpdate);
    void onFailure(String errorMessage);
}