package com.group3boot.sunspot.source.spot;

import com.group3boot.sunspot.database.SpotDao;
import com.group3boot.sunspot.database.SpotRoomDatabase;
import com.group3boot.sunspot.models.Spot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpotLocalDataSource extends BaseSpotLocalDataSource {

    private final SpotDao spotDao;

    public SpotLocalDataSource(SpotRoomDatabase spotRoomDatabase) {
        this.spotDao = spotRoomDatabase.spotDao();
    }

    @Override
    public void getSpots() {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotCallback.onSyncComplete();
        });
    }

    @Override
    public void getFavoriteSpots(String userId) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Spot> allSpots = spotDao.getAll();
            List<Spot> favorites = new ArrayList<>();
            for (Spot spot : allSpots) {
                if (spot.isFavoritedBy(userId)) {
                    favorites.add(spot);
                }
            }
            spotCallback.onFavoriteSpotsReady(favorites);
        });
    }

    @Override
    public void getMySpots(String userId) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotCallback.onMySpotsReady(spotDao.getMySpots(userId));
        });
    }

    @Override
    public void updateSpot(Spot spot) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotDao.updateSpot(spot);
        });
    }

    @Override
    public void insertSpot(Spot spot) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Long> insertedIds = spotDao.insertSpotList(Collections.singletonList(spot));
            spot.setUid(insertedIds.get(0));
        });
    }

    @Override
    public void insertSpots(List<Spot> spotList) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Sync "cancella e riscrivi": niente merge, Firestore è sempre la fonte di verità
            spotDao.deleteAll();
            if (spotList != null) {
                spotDao.insertSpotList(spotList);
            }
            spotCallback.onSyncComplete();
        });
    }

    @Override
    public void deleteSpot(Spot spot) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotDao.delete(spot);
        });
    }
}