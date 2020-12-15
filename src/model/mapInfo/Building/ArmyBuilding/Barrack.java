package model.mapInfo.Building.ArmyBuilding;

import config.DataLoader;
import model.mapInfo.Building.Building;

import java.util.LinkedList;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
public class Barrack extends Building{
    private long trainTimestamp;
    private LinkedList<String> trainQueue = new LinkedList<String>();
    private int[] quantity = new int[DataLoader.getInstance().getMaxTroop()];
    private int totalQuantity;

    public Barrack(int id, String type, int level) {
        super(id, type, level);
        totalQuantity = 0;
    }

    public LinkedList<String> getTrainQueue() {
        return trainQueue;
    }

    public int[] getQuantity() {
        return quantity;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public long getTrainTimestamp() {
        return trainTimestamp;
    }

    public void setTrainTimestamp(long trainTimestamp) {
        this.trainTimestamp = trainTimestamp;
    }

    public void trainTroop(String troopType) {
        int troopEnum = DataLoader.getInstance().getEnumFromTroopType(troopType);

        if (quantity[troopEnum] == 0) {
            trainQueue.addLast(troopType);
            if (trainQueue.peek().equals(troopType)) {
                trainTimestamp = System.currentTimeMillis() / 1000;
            }
        }
        
        quantity[troopEnum]++;

        int housingSpace = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("housingSpace")
        );
        totalQuantity += housingSpace;
    }

    public int cancelTroop(String troopType) {
        int troopEnum = DataLoader.getInstance().getEnumFromTroopType(troopType);
        int housingSpace = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("housingSpace")
        );
        if (quantity[troopEnum] > 0) {
            quantity[troopEnum] -= 1;
            totalQuantity -= housingSpace;
        }
        if (quantity[troopEnum] == 0) {
            int index = trainQueue.indexOf(troopType);
            if (index == -1)
                return -1;
            trainQueue.remove(index);
        }
        trainTimestamp = System.currentTimeMillis() / 1000;

        return 0;
    }

    public int finishTrainTroop(long nextTrainTimestamp) {
        String troopType = trainQueue.peekFirst();
        int troopEnum = DataLoader.getInstance().getEnumFromTroopType(troopType);
        int housingSpace = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("housingSpace")
        );
        if (quantity[troopEnum] > 0) {
            quantity[troopEnum] -= 1;
            totalQuantity -= housingSpace;
        }
        if (quantity[troopEnum] == 0) {
            if (trainQueue.peekFirst() == null)
                return -1;
            trainQueue.removeFirst();
        }
        trainTimestamp = nextTrainTimestamp;
        return 0;
    }

}
