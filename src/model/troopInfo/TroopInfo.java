package model.troopInfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import config.DataLoader;
import model.mapInfo.Building.ArmyBuilding.ArmyCamp;
import model.mapInfo.MapInfo;

import java.util.ArrayList;

/**
 * Created by CPU60159_LOCAL on 7/14/2020.
 */
public class TroopInfo {
    MapInfo mInfo;

    ArrayList<Integer> armyCampIdList = new ArrayList<Integer>();

    Gson gson = new Gson();

    public TroopInfo (MapInfo mInfo){
        this.mInfo = mInfo;
        armyCampIdList = mInfo.getArmyCampIdList();
    }


    //Dung ham DataLoader.getInstance().getTroopTypeFromEnum(i) de chuyen
    //tu enum sang type
    public String getTroop() {
        JsonArray jArr = new JsonArray();
        for (int id : armyCampIdList) {
            ArmyCamp armyCamp = (ArmyCamp) mInfo.getMapObjectInfo(id);
            int[] quantity = armyCamp.getQuantity();

            JsonObject jObj = new JsonObject();

            jObj.addProperty("id", id);

            for (int i=0; i<quantity.length; i++) {
                if (quantity[i] == 0)
                    continue;
                String type = DataLoader.getInstance().getTroopTypeFromEnum(i);
                jObj.addProperty(type, quantity[i]);
            }

            jArr.add(jObj);
        }
        return gson.toJson(jArr);
    }

    public int[] getTotalTroop() {
        int[] totalTroop = new int[DataLoader.getInstance().getMaxTroop()];
        for (int id : armyCampIdList) {
            ArmyCamp armyCamp = (ArmyCamp) mInfo.getMapObjectInfo(id);
            int[] quantity = armyCamp.getQuantity();

            for (int i=0; i<quantity.length; i++) {
                totalTroop[i] += quantity[i];
            }
        }
        return totalTroop;
    }

    public int subTroop(int[] selectedTroop) {
        int[] tempTroop = selectedTroop.clone();
        int[] totalTroop = getTotalTroop();
        for (int i=0; i<totalTroop.length; i++)
            if (tempTroop[i] > totalTroop[i])
                return -1;

        for (int id : armyCampIdList) {
            ArmyCamp armyCamp = (ArmyCamp) mInfo.getMapObjectInfo(id);
            int[] quantity = armyCamp.getQuantity();

            for (int i=0; i<quantity.length; i++)
                if (quantity[i] != 0 && tempTroop[i] != 0) {
                    int amount = Math.min(quantity[i], tempTroop[i]);
                    armyCamp.updateTroopQuantity(DataLoader.getInstance().getTroopTypeFromEnum(i), -amount);
                    tempTroop[i] -= amount;
            }
        }

        return 0;
    }
}
