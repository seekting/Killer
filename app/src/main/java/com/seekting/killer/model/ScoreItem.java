package com.seekting.killer.model;

import java.util.ArrayList;
import java.util.List;

public class ScoreItem {
    public String id;
    public String name;


    public int level;
    public List<ScoreItem> mItems = new ArrayList<>();
    public boolean selected;

    public ScoreItem(String id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    @Override
    public String toString() {
        return "ScoreItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", level=" + level +
                ", mItems=" + mItems +
                ", selected=" + selected +
                '}';
    }
}
