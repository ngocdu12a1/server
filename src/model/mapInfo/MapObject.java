package model.mapInfo;

import com.google.gson.annotations.Expose;
import config.DataLoader;

/**
 * Created by VinhTT on 2020-06-30.
 */
public class MapObject {
    private int id;
    private String type;
    private int level;
    private long startActionTimestamp;

    public MapObject() {}

    public MapObject(int id, String type, int level) {
        setId(id);
        setType(type);
        setLevel(level);
        setStartActionTimestamp(level == 0 ? System.currentTimeMillis()/1000 : 0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartActionTimestamp() {
        return startActionTimestamp;
    }

    public void setStartActionTimestamp(long startActionTimestamp) {
        this.startActionTimestamp = startActionTimestamp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
