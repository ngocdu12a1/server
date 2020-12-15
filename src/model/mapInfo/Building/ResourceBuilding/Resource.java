package model.mapInfo.Building.ResourceBuilding;

import model.mapInfo.Building.Building;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
public class Resource extends Building{
    private int quantity;
    private int maxQuantity;
    private int productivity;
    private long harvestTimeStamp;
    private String harvestType;

    public Resource(int id, String type, int level) {
        super(id, type, level);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getHarvestTimeStamp() {
        return harvestTimeStamp;
    }

    public void setHarvestTimeStamp(long harvestTimeStamp) {
        this.harvestTimeStamp = harvestTimeStamp;
    }

}
