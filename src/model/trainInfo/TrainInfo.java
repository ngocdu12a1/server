package model.trainInfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import config.DataLoader;
import model.mapInfo.Building.ArmyBuilding.ArmyCamp;
import model.mapInfo.Building.ArmyBuilding.Barrack;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;

/**
 * Created by CPU60159_LOCAL on 7/14/2020.
 */
public class TrainInfo {
    MapInfo mInfo;

    ArrayList<Integer> barrackIdList = new ArrayList<Integer>();
    ArrayList<Integer> armyCampIdList = new ArrayList<Integer>();

    Gson gson = new Gson();

    public TrainInfo (MapInfo mInfo){
        this.mInfo = mInfo;
        barrackIdList = mInfo.getBarrackIdList();
        armyCampIdList = mInfo.getArmyCampIdList();
    }

    public boolean checkRemainingSpace(int id, String troopType) {
        MapObject mObj = mInfo.getMapObjectInfo(id);
        String type = mObj.getType();
        int level = mObj.getLevel();
        Map<String, String> map = DataLoader.getInstance().getMap(type, String.valueOf(level));
        int maxQuantity;
        int totalQuantity;
        if (DataLoader.getInstance().getMapObjectName(type).equals("Barrack")) {
            maxQuantity = Integer.parseInt(map.get("queueLength"));
            totalQuantity = ((Barrack) mObj).getTotalQuantity();
        }
        else {
            maxQuantity = Integer.parseInt(map.get("capacity"));
            totalQuantity = ((ArmyCamp) mObj).getTotalQuantity();
        }
        int housingSpace = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("housingSpace")
        );
        if (totalQuantity + housingSpace > maxQuantity)
            return false;
        return true;
    }

    public String getTrainInfo() {
        JsonArray jArr = new JsonArray();
        for (int id : barrackIdList) {
            Barrack barrack = (Barrack) mInfo.getMapObjectInfo(id);
            Deque<String> trainQueue = barrack.getTrainQueue();
            int[] quantity = barrack.getQuantity();

            JsonObject jObj = new JsonObject();

            jObj.addProperty("id", id);
            jObj.addProperty("timeStamp", barrack.getTrainTimestamp());
            for (String troopType: trainQueue) {
                int troopEnum = DataLoader.getInstance().getEnumFromTroopType(troopType);
                jObj.addProperty(troopType, quantity[troopEnum]);
            }

            jArr.add(jObj);
        }
        return gson.toJson(jArr);
    }

    public int trainTroop(String troopType, int id) {
        String type = mInfo.getMapObjectInfo(id).getType();
        int barrackLevel = mInfo.getMapObjectInfo(id).getLevel();

        if (!DataLoader.getInstance().getMapObjectName(type).equals("Barrack")){
            return -1;
        }
        
        int barracksLevelRequired = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("barracksLevelRequired")
        );

        if (barrackLevel < barracksLevelRequired) {
            return -1;
        }

        if (!checkRemainingSpace(id, troopType)){
            return -1;
        }

        Barrack barrack = (Barrack) mInfo.getMapObjectInfo(id);
        barrack.trainTroop(troopType);

        return id;
    }

    public long skipTroop(int barrackID, int armyCampID, long timestamp) {
        String barrackType = mInfo.getMapObjectInfo(barrackID).getType();
        if (!DataLoader.getInstance().getMapObjectName(barrackType).equals("Barrack"))
            return -1;

        Barrack barrack = (Barrack) mInfo.getMapObjectInfo(barrackID);
        barrack.setTrainTimestamp(-barrack.getTrainTimestamp());

        return finishTrainTroop(barrackID, armyCampID, timestamp);
    }

    public int cancelTroop(String troopType, int id) {
        String type = mInfo.getMapObjectInfo(id).getType();
        if (!DataLoader.getInstance().getMapObjectName(type).equals("Barrack"))
            return -1;
        
        Barrack barrack = (Barrack) mInfo.getMapObjectInfo(id);
        if  (barrack.cancelTroop(troopType) == -1)
            return -1;

        return id;
    }

    public long finishTrainTroop(int barrackID, int armyCampID, long timestamp) {
        String barrackType = mInfo.getMapObjectInfo(barrackID).getType();
        if (!DataLoader.getInstance().getMapObjectName(barrackType).equals("Barrack"))
            return -1;
        String armyCampType = mInfo.getMapObjectInfo(armyCampID).getType();
        if (!DataLoader.getInstance().getMapObjectName(armyCampType).equals("ArmyCamp"))
            return -1;

        Barrack barrack = (Barrack) mInfo.getMapObjectInfo(barrackID);
        ArmyCamp armyCamp = (ArmyCamp) mInfo.getMapObjectInfo(armyCampID);

        String troopType = barrack.getTrainQueue().peek();
        if (troopType == null) 
            return -1;

        long startTrainTimestamp = barrack.getTrainTimestamp();
        long thisTimestamp = System.currentTimeMillis()/1000;
        long timeLength = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("trainingTime")
        );

        if (startTrainTimestamp + timeLength > thisTimestamp + 5)
            return -1;

        if (startTrainTimestamp < 0)
            startTrainTimestamp = -startTrainTimestamp;

        if (checkRemainingSpace(armyCampID, troopType)) {
            if (barrack.finishTrainTroop(startTrainTimestamp + timeLength) == -1)
                return -1;
            armyCamp.updateTroopQuantity(troopType, 1);
        }
        else
            return -1;

        return barrack.getTrainTimestamp();
    }

    public void updateAll() {
        for (int barrackID : barrackIdList) {
            for (int armyCampID : armyCampIdList) {
                long timestamp;
                do {
                    timestamp = finishTrainTroop(barrackID, armyCampID, 0);
                }
                while (timestamp != -1);
            }
        }
    }
}
