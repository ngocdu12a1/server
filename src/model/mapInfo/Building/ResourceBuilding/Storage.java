package model.mapInfo.Building.ResourceBuilding;

import model.mapInfo.Building.Building;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
public class Storage extends Building {
    private int quantity;
    private int maxQuantity;
    private String harvestType;

    public Storage(int id, String type, int level) {
        super(id, type, level);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
