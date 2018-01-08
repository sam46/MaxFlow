package com.bbot.maxflow;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FlowGraphEntityDao {
    @Query("SELECT * FROM flowgraph")
    List<FlowGraphEntity> getAll();

    @Query("SELECT * FROM flowgraph WHERE id IN (:fgeIds)")
    List<FlowGraphEntity> loadAllByIds(int... fgeIds);

    @Query("SELECT * FROM flowgraph WHERE id =:fgeId LIMIT 1")
    FlowGraphEntity findById(int fgeId);

    @Insert
    void insertAll(FlowGraphEntity... fge);

    @Delete
    void delete(FlowGraphEntity fge);
}
