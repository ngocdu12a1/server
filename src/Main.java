import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import config.BuildingMapDeserializer;
import config.DataLoader;
import config.MapInfoDeserializer;
import model.PlayerInfo;
import model.attackMapInfo.AttackManager;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import model.trainInfo.TrainInfo;
import util.mapUtils.IsoPoint;
import util.mapUtils.IsometricUtils;
import util.server.ServerConstant;

import java.awt.*;
import java.util.HashMap;

public class Main {
    
    private static boolean checkResource(String type, int level, PlayerInfo pInfo, MapInfo mInfo) {
        int[] resource = mInfo.getResource();
        resource[ServerConstant.RESOURCE.COIN.getValue()] = pInfo.getCoin();

        int[] requiredResource = DataLoader.getInstance().getRequiredResource(type, level);

        for (int i=0; i<ServerConstant.RESOURCE.values().length; i++)
            if (resource[i] < requiredResource[i])
                return false;

        return true;
    }

    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();

        MapInfo mInfo = new MapInfo();
        PlayerInfo pInfo = new PlayerInfo(123, "Haha");

        GsonBuilder gb = new GsonBuilder();
        gb.registerTypeAdapter(new TypeToken<HashMap<Integer, MapObject>>() {}.getType(), new BuildingMapDeserializer());
        gb.registerTypeAdapter(new TypeToken<MapInfo>() {}.getType(), new MapInfoDeserializer());
        Gson gson = gb.create();

        mInfo.updateResource(new int[]{10000, 10000, 0, 0});
        pInfo.setCoin(10000);

        int barrackId = mInfo.buildBuilding("BAR_1", 23, 31);
        mInfo.skipUpgradeBuilding(barrackId);
        mInfo.getMapObjectInfo(barrackId).setLevel(8);

        int armyCampId = mInfo.getArmyCampIdList().get(0);

        TrainInfo trainInfo = new TrainInfo(mInfo);
        for (int i =0; i<5; i++) {
            trainInfo.trainTroop("ARM_1", barrackId);
            trainInfo.skipTroop(barrackId, armyCampId, 0);
        }
        for (int i =0; i<5; i++) {
            trainInfo.trainTroop("ARM_2", barrackId);
            trainInfo.skipTroop(barrackId, armyCampId, 0);
        }
        for (int i =0; i<2; i++) {
            trainInfo.trainTroop("ARM_4", barrackId);
            trainInfo.skipTroop(barrackId, armyCampId, 0);
        }

        AttackManager attackManager = new AttackManager(pInfo, 10);
        int[] selectedTroop = new int[DataLoader.getInstance().getMaxTroop()];
        selectedTroop[DataLoader.getInstance().getEnumFromTroopType("ARM_1")] = 3;
        selectedTroop[DataLoader.getInstance().getEnumFromTroopType("ARM_2")] = 2;
        selectedTroop[DataLoader.getInstance().getEnumFromTroopType("ARM_4")] = 1;
        attackManager.setTroop(mInfo, selectedTroop);

        MapObject test = mInfo.getMapObjectInfo(66);

//        for (int level=1; level<=10; level++) {
//            attackManager = new AttackManager(pInfo, level);
//
//            long timestamp = System.currentTimeMillis();
//
//            for (int i = 0; i<200; i++) {
//                int x = 3 + (int)(Math.random() * 129);
//                int y = 3 + (int)(Math.random() * 129);
//                int tick = i*10 + (int)(Math.random() * 9);
//                attackManager.dropTroop("ARM_1", i, x, y, tick);
//            }
//
//            attackManager.endCampaign(pInfo, mInfo, 7200);
//
//            System.out.println(System.currentTimeMillis() - timestamp);
//        }

        IsoPoint isoPoint = IsometricUtils.getInstance().x3TilePosToIso(new Point(82, 55));
        System.out.println();
    }
}
