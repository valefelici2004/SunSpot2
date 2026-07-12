package com.group3boot.sunspot.source;

import static com.group3boot.sunspot.util.Constants.UNEXPECTED_ERROR;

import com.group3boot.sunspot.database.SpotDao;
import com.group3boot.sunspot.database.SpotRoomDatabase;
import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.SharedPreferencesUtils;

import java.util.Collections;
import java.util.List;

/**
 * Classe per ottenere gli spot dal database locale usando Room.
 */
public class SpotLocalDataSource extends BaseSpotLocalDataSource {

    private final SpotDao spotDao;
    private final SharedPreferencesUtils sharedPreferencesUtil;

    public SpotLocalDataSource(SpotRoomDatabase spotRoomDatabase,
                               SharedPreferencesUtils sharedPreferencesUtil) {
        this.spotDao = spotRoomDatabase.spotDao();
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void getSpots() {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotCallback.onSuccessFromLocal(spotDao.getAll());
        });
    }

    @Override
    public void getFavoriteSpots() {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotCallback.onSuccessFromLocal(spotDao.getLiked());
        });
    }

    @Override
    public void getMySpots() {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotCallback.onSuccessFromLocal(spotDao.getMySpots());
        });
    }

    @Override
    public void updateSpot(Spot spot) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = spotDao.updateSpot(spot);

            if (rowUpdatedCounter == 1) {
                spotCallback.onSpotFavoriteStatusChanged(spot, spotDao.getLiked());
            } else {
                spotCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    @Override
    public void insertSpot(Spot spot) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Long> insertedIds = spotDao.insertSpotList(Collections.singletonList(spot));
            spot.setUid(insertedIds.get(0));
            spotCallback.onAddSpotSuccess(spot);
        });
    }

    @Override
    public void insertSpots(List<Spot> spotList) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Spot> allSpots = spotDao.getAll();

            if (spotList != null) {
                // Stesso meccanismo anti-perdita-preferiti visto in Article:
                // preserva uid/liked/addedByMe degli spot già noti in locale
                for (Spot spot : allSpots) {
                    if (spotList.contains(spot)) {
                        spotList.set(spotList.indexOf(spot), spot);
                    }
                }

                List<Long> insertedIds = spotDao.insertSpotList(spotList);
                for (int i = 0; i < spotList.size(); i++) {
                    spotList.get(i).setUid(insertedIds.get(i));
                }

                sharedPreferencesUtil.writeStringData(Constants.SHARED_PREFERENCES_FILENAME,
                        Constants.SHARED_PREFERENCES_LAST_SPOT_UPDATE, String.valueOf(System.currentTimeMillis()));

                spotCallback.onSuccessFromLocal(spotList);
            }
        });
    }

    @Override
    public void deleteSpot(Spot spot) {
        SpotRoomDatabase.databaseWriteExecutor.execute(() -> {
            spotDao.delete(spot);
            spotCallback.onDeleteSpotSuccess(spotDao.getMySpots());
        });
    }
}