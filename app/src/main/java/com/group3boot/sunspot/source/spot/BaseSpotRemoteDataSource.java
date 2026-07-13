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
    @Override
    public void deleteSpot(Spot spot) {
        db.collection(COLLECTION_SPOTS)
                .document(spot.getFirebaseId())
                .delete()
                .addOnSuccessListener(unused -> spotCallback.onDeleteSpotSuccess(spot))
                .addOnFailureListener(e -> spotCallback.onFailureFromRemote(e));
    }
}