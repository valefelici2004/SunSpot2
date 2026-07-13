package com.group3boot.sunspot.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.group3boot.sunspot.models.Spot;

import java.util.List;

@Dao
public interface SpotDao {

    @Query("SELECT * FROM Spot")
    List<Spot> getAll();

    @Query("SELECT * FROM Spot WHERE addedByUserId = :userId")
    List<Spot> getMySpots(String userId);

    @Query("SELECT * FROM Spot WHERE firebaseId = :firebaseId LIMIT 1")
    Spot getSpotByFirebaseId(String firebaseId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Spot spot);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertSpotList(List<Spot> spotList);

    @Update
    int updateSpot(Spot spot);

    @Delete
    void delete(Spot spot);

    @Query("DELETE FROM Spot")
    void deleteAll();
}