package com.bbot.maxflow;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * a serialized compact representation of FlowGraph for persisting into the database.
 */
@Entity(tableName = "flowgraph")
public class FlowGraphEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "serialized")
    private String serialized;

    public FlowGraphEntity(String name, String serialized) {
        setName(name);
        setSerialized(serialized);
    }

    @Ignore
    public FlowGraphEntity(String serialized) {
        setName("unnamed");
        setSerialized(serialized);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialized() {
        return serialized;
    }

    public void setSerialized(String serialized) {
        this.serialized = serialized;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        System.out.println("dao: "+name+": "+this.id+" --> "+id);
        this.id = id;
    }

}