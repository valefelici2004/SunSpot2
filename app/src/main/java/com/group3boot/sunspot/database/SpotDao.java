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

    //Ottengo tutti gli spot insieme
    @Query("SELECT * FROM Spot")
    List<Spot> getAll();

    //Lista preferiti
    @Query("SELECT * FROM Spot WHERE liked = 1")
    List<Spot> getLiked();

    //Lista spot inseriti da me
    @Query("SELECT * FROM Spot WHERE addedByMe = 1")
    List<Spot> getMySpots();

    //Salvo copia locale mio spot
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Spot spot);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertSpotList(List<Spot> spotList);

    @Update
    int updateSpot(Spot spot);

    //Eliminare uno spot
    @Delete
    void delete(Spot spot);

    @Query("DELETE FROM Spot")
    void deleteAll();
}