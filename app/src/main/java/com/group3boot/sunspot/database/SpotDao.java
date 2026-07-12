package com.group3boot.sunspot.database;

<<<<<<< HEAD
package com.group3boot.sunspot.database;

=======
>>>>>>> 3c9a91715bcb86e46c3186ac409569b3da6545b3
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

<<<<<<< HEAD
    @Query("SELECT * FROM Spot")
    List<Spot> getAll();

    @Query("SELECT * FROM Spot WHERE liked = 1")
    List<Spot> getLiked();

    @Query("SELECT * FROM Spot WHERE addedByMe = 1")
    List<Spot> getMySpots();

    @Query("SELECT * FROM Spot WHERE firebaseId = :firebaseId LIMIT 1")
    Spot getSpotByFirebaseId(String firebaseId);
=======
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
>>>>>>> 3c9a91715bcb86e46c3186ac409569b3da6545b3

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertSpotList(List<Spot> spotList);

    @Update
    int updateSpot(Spot spot);

<<<<<<< HEAD
=======
    //Eliminare uno spot
>>>>>>> 3c9a91715bcb86e46c3186ac409569b3da6545b3
    @Delete
    void delete(Spot spot);

    @Query("DELETE FROM Spot")
    void deleteAll();
}