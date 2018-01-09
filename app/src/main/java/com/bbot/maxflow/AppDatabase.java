package com.bbot.maxflow;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {FlowGraphEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase sInstance;
    public static final String DATABASE_NAME = "maxflow";
    public abstract FlowGraphEntityDao flowGraphEntityDao();

    public static AppDatabase getInstance(Context appContext) {
        boolean doPopluate = ! appContext.getDatabasePath(DATABASE_NAME).exists();
        
        if (sInstance == null) {
            sInstance = Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME).addCallback(new Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    System.out.println("Created db !!");

                }
            }).build();
            if (doPopluate)
                populate(appContext);
        }
        return sInstance;
    }

    /**
     * Populate app's database with pre-built graphs.
     * Will only run once upon first app run or if the database is manually deleted from filesystem
     *
     * @param appContext
     */
    private static void populate(Context appContext) {
        System.out.println("Populating database...");
        final int n = 1;
        FlowGraphEntity [] fges = new FlowGraphEntity[n];
        final String sample_graph_str = appContext.getResources().getString(R.string.sample_graph);
        for (int i = 0; i < n; i++)
            fges[i] = new FlowGraphEntity("sample graph " + (i+1), sample_graph_str);
        sInstance.flowGraphEntityDao().insertAll(fges);
    }

}