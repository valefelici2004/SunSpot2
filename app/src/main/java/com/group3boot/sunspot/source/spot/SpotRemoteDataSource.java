package com.group3boot.sunspot.source.spot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group3boot.sunspot.models.Spot;

import java.util.List;

public class SpotRemoteDataSource extends BaseSpotRemoteDataSource {

    private static final String COLLECTION_SPOTS = "spots";

    private final FirebaseFirestore db;

    public SpotRemoteDataSource() {
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void getSpots() {
        db.collection(COLLECTION_SPOTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Spot> spotList = queryDocumentSnapshots.toObjects(Spot.class);

                    for (int i = 0; i < spotList.size(); i++) {
                        spotList.get(i).setFirebaseId(queryDocumentSnapshots.getDocuments().get(i).getId());
                    }

                    spotCallback.onSuccessFromRemote(spotList, System.currentTimeMillis());
                })
                .addOnFailureListener(e -> spotCallback.onFailureFromRemote(e));
    }

    @Override
    public void addSpot(Spot spot) {
        db.collection(COLLECTION_SPOTS)
                .add(spot)
                .addOnSuccessListener(documentReference -> {
                    spot.setFirebaseId(documentReference.getId());
                    spotCallback.onAddSpotSuccess(spot);
                })
                .addOnFailureListener(e -> spotCallback.onFailureFromRemote(e));
    }

    @Override
    public void updateSpot(Spot spot) {
        db.collection(COLLECTION_SPOTS)
                .document(spot.getFirebaseId())
                .set(spot)
                .addOnSuccessListener(unused -> spotCallback.onUpdateSpotSuccess(spot))
                .addOnFailureListener(e -> spotCallback.onFailureFromRemote(e));
    }

    @Override
    public void deleteSpot(Spot spot) {
        db.collection(COLLECTION_SPOTS)
                .document(spot.getFirebaseId())
                .delete()
                .addOnSuccessListener(unused -> spotCallback.onDeleteSpotSuccess(spot))
                .addOnFailureListener(e -> spotCallback.onFailureFromRemote(e));
    }
}