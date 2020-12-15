package model.attackMapInfo.campaignMapInfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import config.DataLoader;
import model.mapInfo.Building.Building;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import util.server.ServerConstant;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by CPU60126_LOCAL on 2020-07-28.
 */
public class CampaignMapInfo extends MapInfo{
    Gson gson = new Gson();

    int totalBuilding = 0;

    int[] totalResource = new int[ServerConstant.RESOURCE.values().length];

    public CampaignMapInfo(int level, int[] acquiredResource) {
        super(0);

        JsonObject json = DataLoader.getInstance().getCampaignMap(level);
        JsonArray jArr = json.get("house").getAsJsonArray();

        for (JsonElement je : jArr) {
            JsonObject jo = je.getAsJsonObject();

            int bdId = jo.get("objId").getAsInt();
            String bdType = jo.get("objType").getAsString();
            int bdLevel = jo.get("level").getAsInt();
            int bdCell = jo.get("cell").getAsInt();

            int posX = bdCell / (ServerConstant.MAP_X + ServerConstant.BORDER_X);
            int posY = bdCell % (ServerConstant.MAP_Y + ServerConstant.BORDER_Y);
            Point bdPos = new Point(posX, posY);

            MapObject mapObject = createMapObject(bdId, bdType, bdLevel);

            if (mapObject == null)
                continue;

            if (!checkMapObjectName(bdType,"Obstacle")) {
                Building building = (Building) mapObject;

                int maxHealth = Integer.parseInt(
                        DataLoader.getInstance().getMap(bdType, String.valueOf(bdLevel)).get("hitpoints")
                );

                building.setHealth(maxHealth);

                if (!checkMapObjectName(bdType,"Wall")) {
                    totalBuilding++;
                }
            }

            buildingMap.put(bdId, mapObject);
            buildingPos.put(bdId, bdPos);
        }

        JsonObject jObjRes = json.get("resourse").getAsJsonObject();
        totalResource[ServerConstant.RESOURCE.GOLD.getValue()] = jObjRes.get("gold").getAsInt()
                - acquiredResource[ServerConstant.RESOURCE.GOLD.getValue()];
        totalResource[ServerConstant.RESOURCE.ELIXIR.getValue()] = jObjRes.get("elixir").getAsInt()
                - acquiredResource[ServerConstant.RESOURCE.ELIXIR.getValue()];

        updateResource(totalResource);
    }

    public int getTotalBuilding() {
        return totalBuilding;
    }

    public int [][] createMapGrid() {
        int[][] mapGrid = new int[(ServerConstant.MAP_X+ServerConstant.BORDER_X)*3]
                [(ServerConstant.MAP_Y+ServerConstant.BORDER_Y)*3];
        for (int i = 0; i < (ServerConstant.MAP_X+ServerConstant.BORDER_X)*3; i++) {
            Arrays.fill(mapGrid[i], ServerConstant.MAP_GRASS);
        }

        for (Integer key : buildingMap.keySet()) {
            Object mapObject = buildingMap.get(key);

            int id = ((MapObject) mapObject).getId();
            String type = ((MapObject) mapObject).getType();
            int level = ((MapObject) mapObject).getLevel();
            int posX = buildingPos.get(id).x * 3;
            int posY = buildingPos.get(id).y * 3;

            if (level == 0)
                level = 1;

            Map<String, String> mapHash = DataLoader.getInstance()
                    .getMap(type, String.valueOf(level));
            int width = Integer.parseInt(mapHash.get("width")) * 3;
            int height = Integer.parseInt(mapHash.get("height")) * 3;
            for (int i = posX; i < posX+width; i++) {
                for (int j = posY; j < posY+height; j++) {
                    if (!checkMapObjectName(type,"Wall"))
                        if (i == posX || i == posX+width-1
                                || j == posY || j == posY+height-1)
                            continue;
                    mapGrid[i][j] = id;
                }
            }
        }

//        for (int i = 0; i < 132; i++) {
//            for (int j = 0; j < 132; j++) {
//                System.out.format("%3d", mapGrid[i][j]);
//            }
//            System.out.println();
//        }

        return mapGrid;
    }

    public int [][] createBorderGrid() {
        int[][] borderGrid = new int[(ServerConstant.MAP_X+ServerConstant.BORDER_X)*3]
                [(ServerConstant.MAP_Y+ServerConstant.BORDER_Y)*3];
        for (int i = 0; i < (ServerConstant.MAP_X+ServerConstant.BORDER_X)*3; i++) {
            Arrays.fill(borderGrid[i], 1);
        }

        for (Integer key : buildingMap.keySet()) {
            Object mapObject = buildingMap.get(key);

            int id = ((MapObject) mapObject).getId();
            String type = ((MapObject) mapObject).getType();
            int level = ((MapObject) mapObject).getLevel();
            int posX = buildingPos.get(id).x * 3;
            int posY = buildingPos.get(id).y * 3;

            if (level == 0)
                level = 1;

            Map<String, String> mapHash = DataLoader.getInstance()
                    .getMap(type, String.valueOf(level));
            int width = Integer.parseInt(mapHash.get("width")) * 3;
            int height = Integer.parseInt(mapHash.get("height")) * 3;
            for (int i = posX-3; i < posX+width+3; i++) {
                for (int j = posY-3; j < posY+height+3; j++) {
                    borderGrid[i][j] = 0;
                }
            }
        }

//        for (int i = 0; i < 132; i++) {
//            for (int j = 0; j < 132; j++) {
//                System.out.format("%3d", borderGrid[i][j]);
//            }
//            System.out.println();
//        }

        return borderGrid;
    }

    public void removeMapObject(int id) {
        buildingMap.remove(id);
        buildingPos.remove(id);
    }
}
