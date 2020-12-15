package model.mapInfo.Building;

import model.mapInfo.MapObject;

/**
 * Created by CPU60126_LOCAL on 2020-06-30.
 */
public class Building extends MapObject {
    private int health;

    public Building(int id, String type, int level) {
        super(id, type, level);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
