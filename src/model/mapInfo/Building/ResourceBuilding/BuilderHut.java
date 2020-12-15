package model.mapInfo.Building.ResourceBuilding;

import model.mapInfo.Building.Building;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
public class BuilderHut extends Building{
    private int targetID;

    public BuilderHut(int id, String type, int level) {
        super(id, type, level);
        setTargetID(-1);
    }

    public int getTargetID() {
        return targetID;
    }

    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
}
