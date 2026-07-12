package com.group3boot.sunspot.database;

import static com.group3boot.sunspot.util.Constants.DATABASE_VERSION;

import android.content.Context;

import com.group3boot.sunspot.util.Constants;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.group3boot.sunspot.models.Spot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Spot.class}, version = DATABASE_VERSION, exportSchema = true)
public abstract class SpotRoomDatabase extends RoomDatabase {

    public abstract SpotDao spotDao();

    private static volatile SpotRoomDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static SpotRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SpotRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SpotRoomDatabase.class, Constants.SAVED_SPOTS_DATABASE)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}