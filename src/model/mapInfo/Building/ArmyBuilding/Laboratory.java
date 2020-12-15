package model.mapInfo.Building.ArmyBuilding;

import model.mapInfo.Building.Building;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
public class Laboratory extends Building {
    private boolean isResearching;

    public Laboratory(int id, String type, int level) {
        super(id, type, level);
    }

    public boolean getIsResearching() {
        return isResearching;
    }

    public void setIsResearching(boolean isResearching) {
        this.isResearching = isResearching;
    }

}
