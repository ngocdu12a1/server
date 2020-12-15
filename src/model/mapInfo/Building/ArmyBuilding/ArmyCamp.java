package model.mapInfo.Building.ArmyBuilding;

import config.DataLoader;
import model.mapInfo.Building.Building;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
public class ArmyCamp extends Building{
    private int[] quantity;
    private int totalQuantity;

    public ArmyCamp(int id, String type, int level) {
        super(id, type, level);
        quantity = new int[DataLoader.getInstance().getMaxTroop()];
        totalQuantity = 0;
    }

    public int[] getQuantity() {
        return quantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void updateTroopQuantity(String troopType, int number) {
        int troopEnum = DataLoader.getInstance().getEnumFromTroopType(troopType);
        int housingSpace = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("housingSpace")
        );
        quantity[troopEnum] += number;
        totalQuantity += housingSpace*number;
    }
}
