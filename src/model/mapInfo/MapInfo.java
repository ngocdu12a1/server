package model.mapInfo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import config.*;
import model.mapInfo.Building.ArmyBuilding.ArmyCamp;
import model.mapInfo.Building.ArmyBuilding.Barrack;
import model.mapInfo.Building.ArmyBuilding.Laboratory;
import model.mapInfo.Building.DefensiveBuilding.Defence;
import model.mapInfo.Building.DefensiveBuilding.Wall;
import model.mapInfo.Building.ResourceBuilding.BuilderHut;
import model.mapInfo.Building.ResourceBuilding.Resource;
import model.mapInfo.Building.ResourceBuilding.Storage;
import model.mapInfo.Building.TownHall.TownHall;
import model.mapInfo.Obstacle.Obstacle;
import util.database.DataModel;
import util.server.ServerConstant;

import java.awt.*;
import java.util.*;

public class MapInfo extends DataModel{

    protected HashMap<Integer, MapObject> buildingMap = new HashMap<>();

    protected Map<Integer, Point> buildingPos = new HashMap<>();

    private PriorityQueue<Integer> freeIdQueue = new PriorityQueue<Integer>();

    private ArrayList<Integer> builderIdList = new ArrayList<Integer>();

    private ArrayList<Integer> storageIdList = new ArrayList<Integer>();

    private ArrayList<Integer> barrackIdList = new ArrayList<Integer>();

    private ArrayList<Integer> armyCampIdList = new ArrayList<Integer>();

    private ArrayList<Integer> defenceIdList = new ArrayList<Integer>();

    private int townHallId;

    public MapInfo(int i) {
        super();
    }

    public boolean checkMapObjectName(String type, String name) {
        return DataLoader.getInstance().getMapObjectName(type).equals(name);
    }

    public ArrayList<Integer> getListFromType(String type) {
        String mapObjectName = DataLoader.getInstance()
                .getMapObjectName(type);

        switch (mapObjectName)
        {
            case "Obstacle":
                break;
            case "ArmyCamp":
                return armyCampIdList;
            case "Barrack":
                return barrackIdList;
            case "Laboratory":
                break;
            case "Defence":
                return defenceIdList;
            case "Wall":
                break;
            case "BuilderHut":
                return builderIdList;
            case "Resource":
                break;
            case "Storage":
                return storageIdList;
            case "TownHall":
                break;
            default:
                break;
        }

        return null;
    }

    public MapInfo() {
        super();
        String jobj = DataLoader.getInstance()
                .initMap();

        Gson gson = new Gson();
        JsonArray json = gson.fromJson(jobj, JsonArray.class);

        Iterator itr = json.iterator();

        while (itr.hasNext()) {
            JsonObject jObj = (JsonObject) itr.next();
            int id = jObj.get("id").getAsInt();
            String type = jObj.get("type").getAsString();
            int level = jObj.get("level").getAsInt();
            int posX = jObj.get("posX").getAsInt();
            int posY = jObj.get("posY").getAsInt();

            MapObject mapObject = createMapObject(id, type, level);

            if (mapObject == null)
                continue;

//            if (DataLoader.getInstance().getMapObjectName(type).equals("TownHall"))
//                townHallId = id;
//
//            if (DataLoader.getInstance().getMapObjectName(type).equals("BuilderHut"))
//                builderIdList.add(id);
//
//            if (DataLoader.getInstance().getMapObjectName(type).equals("Storage"))
//                storageIdList.add(id);
//
//            if (DataLoader.getInstance().getMapObjectName(type).equals("ArmyCamp"))
//                armyCampIdList.add(id);
//
//            if (DataLoader.getInstance().getMapObjectName(type).equals("Barrack"))
//                barrackIdList.add(id);

            Point pos = new Point(posX, posY);

            buildingMap.put(id, mapObject);
            buildingPos.put(id, pos);
        }
    }

    protected MapObject createMapObject(int id, String type, int level) {
        String mapObjectName = DataLoader.getInstance()
                .getMapObjectName(type);

        MapObject mapObject = null;

        switch (mapObjectName)
        {
            case "Obstacle":
                mapObject = new Obstacle(id, type, level);
                break;
            case "ArmyCamp":
                mapObject = new ArmyCamp(id, type, level);
                break;
            case "Barrack":
                mapObject = new Barrack(id, type, level);
                break;
            case "Laboratory":
                mapObject = new Laboratory(id, type, level);
                break;
            case "Defence":
                mapObject = new Defence(id, type, level);
                break;
            case "Wall":
                mapObject = new Wall(id, type, level);
                break;
            case "BuilderHut":
                mapObject = new BuilderHut(id, type, level);
                break;
            case "Resource":
                mapObject = new Resource(id, type, level);
                break;
            case "Storage":
                mapObject = new Storage(id, type, level);
                break;
            case "TownHall":
                mapObject= new TownHall(id, type, level);
                townHallId = id;
                break;
            default:
                break;
        }

        ArrayList<Integer> list = getListFromType(type);
        if (list != null)
        {
            list.add(id);
        }

        return mapObject;
    }

    public int [][] getMapGrid() {
        int[][] mapGrid = new int[ServerConstant.MAP_X][ServerConstant.MAP_Y];
        for (int i = 0; i < ServerConstant.MAP_X; i++) {
            Arrays.fill(mapGrid[i], ServerConstant.MAP_GRASS);
        }

        Set<Integer> keySet = buildingMap.keySet();
        for (Integer key : keySet) {
            Object mapObject = buildingMap.get(key);

            int id = ((MapObject) mapObject).getId();
            String type = ((MapObject) mapObject).getType();
            int level = ((MapObject) mapObject).getLevel();
            int posX = buildingPos.get(id).x;
            int posY = buildingPos.get(id).y;

            if (level == 0)
                level = 1;

            Map<String, String> mapHash = DataLoader.getInstance()
                    .getMap(type, String.valueOf(level));
            int width = Integer.parseInt(mapHash.get("width"));
            int height = Integer.parseInt(mapHash.get("height"));
            for (int i = posX; i < posX + width; i++) {
                for (int j = posY; j < posY + height; j++) {
                    mapGrid[i][j] = id;
                }
            }
        }

//        for (int i = 0; i < 42; i++) {
//            for (int j = 0; j < 42; j++) {
//                System.out.format("%3d", mapGrid[i][j]);
//            }
//            System.out.println();
//        }

        return mapGrid;
    }

    public Map<Integer, MapObject> getBuildingMap() {
        return buildingMap;
    }

    public void setBuildingMap(HashMap<Integer, MapObject> map) {
        buildingMap = map;
    }

    public Map<Integer, Point> getBuildingPos() {
        return buildingPos;
    }

    public void setBuildingPos(Map<Integer, Point> map) {
        buildingPos = map;
    }

    public void setFreeIdQueue(PriorityQueue<Integer> pQueue) {
        this.freeIdQueue = pQueue;
    }

    public void setBuilderIdList(ArrayList<Integer> builderIdList) {
        this.builderIdList = builderIdList;
    }

    public void setStorageIdList(ArrayList<Integer> storageIdList) {
        this.storageIdList = storageIdList;
    }

    public void setBarrackIdList(ArrayList<Integer> barackIdList) {
        this.barrackIdList = barackIdList;
    }

    public void setArmyCampIdList(ArrayList<Integer> armyCampIdList) {
        this.armyCampIdList = armyCampIdList;
    }

    public void setDefenceIdList(ArrayList<Integer> defenceIdList) {
        this.defenceIdList = defenceIdList;
    }

    public void setTownHallId(int townHallId) {
        this.townHallId = townHallId;
    }

    public PriorityQueue<Integer> getFreeIdQueue() {
        return freeIdQueue;
    }

    public ArrayList<Integer> getBuilderIdList() {
        return builderIdList;
    }

    public ArrayList<Integer> getStorageIdList() {
        return storageIdList;
    }

    public ArrayList<Integer> getBarrackIdList() {
        return barrackIdList;
    }

    public ArrayList<Integer> getArmyCampIdList() {
        return armyCampIdList;
    }

    public ArrayList<Integer> getDefenceIdList() {
        return defenceIdList;
    }

    public int getTownHallId() {
        return townHallId;
    }

    public int[] getResource(){
        int[] res = new int[ServerConstant.RESOURCE.values().length];
        for (int storageID : storageIdList) {
            Storage storage = (Storage) buildingMap.get(storageID);

            String type = storage.getType();
            int level = storage.getLevel();

            if (level>0)
            {
                int quantity = storage.getQuantity();
                String harvestType = DataLoader.getInstance()
                        .getMap(type, String.valueOf(level)).get("type");
                switch (harvestType){
                    case "gold":
                        res[ServerConstant.RESOURCE.GOLD.getValue()] += quantity;
                        break;
                    case "elixir":
                        res[ServerConstant.RESOURCE.ELIXIR.getValue()] += quantity;
                        break;
                    case "darkElixir":
                        res[ServerConstant.RESOURCE.DARK_ELIXIR.getValue()] += quantity;
                        break;
                    default:
                        break;
                }
            }
        }
        TownHall townHall = (TownHall) buildingMap.get(townHallId);
        res[ServerConstant.RESOURCE.GOLD.getValue()] += townHall.getGold();
        res[ServerConstant.RESOURCE.ELIXIR.getValue()] += townHall.getElixir();
        res[ServerConstant.RESOURCE.DARK_ELIXIR.getValue()] += townHall.getDarkElixir();
        return res;
    }

    public int[] updateResource(int[] resource) {
        for (int storageID : storageIdList) {
            Storage storage = (Storage) buildingMap.get(storageID);

            String type = storage.getType();
            int level = storage.getLevel();

            if (level>0)
            {
                Map<String, String> mapHash = DataLoader.getInstance()
                        .getMap(type, String.valueOf(level));

                String harvestType = mapHash.get("type");

                int maxQuantity = Integer.parseInt(mapHash.get("capacity"));

                int quantity = storage.getQuantity();

                int index = 0;

                switch (harvestType) {
                    case "gold":
                        index = ServerConstant.RESOURCE.GOLD.getValue();
                        break;
                    case "elixir":
                        index = ServerConstant.RESOURCE.ELIXIR.getValue();
                        break;
                    case "darkElixir":
                        index = ServerConstant.RESOURCE.DARK_ELIXIR.getValue();
                        break;
                    default:
                        break;
                }

                quantity += resource[index];
                if (resource[index] > 0)
                {
                    resource[index] = 0;
                    if (quantity>maxQuantity) {
                        resource[index] = quantity - maxQuantity;
                        quantity = maxQuantity;
                    }
                }
                else if (resource[index] < 0)
                {
                    resource[index] = 0;
                    if (quantity<0) {
                        resource[index] = quantity;
                        quantity = 0;
                    }
                }

                storage.setQuantity(quantity);
            }
        }

        TownHall townHall = (TownHall) buildingMap.get(townHallId);
        Map<String, String> map = DataLoader.getInstance().getMap(
                townHall.getType(), String.valueOf(townHall.getLevel())
        );

        int [] maxTownRes = {
                Integer.parseInt(map.get("capacityGold")),
                Integer.parseInt(map.get("capacityElixir")),
                Integer.parseInt(map.get("capacityDarkElixir")),
                0
        };

        int [] townRes = {
                townHall.getGold(),
                townHall.getElixir(),
                townHall.getDarkElixir(),
                0
        };

        for (int index=0; index<ServerConstant.RESOURCE.values().length; index++)
        {
            townRes[index] += resource[index];
            if (townRes[index]>maxTownRes[index]) {
                townRes[index] = maxTownRes[index];
            }
            if (townRes[index]<0) {
                townRes[index] = 0;
            }
        }

        townHall.setGold(townRes[ServerConstant.RESOURCE.GOLD.getValue()]);
        townHall.setElixir(townRes[ServerConstant.RESOURCE.ELIXIR.getValue()]);
        townHall.setDarkElixir(townRes[ServerConstant.RESOURCE.DARK_ELIXIR.getValue()]);

        return resource;
    }

    public int[] getIdList() {
        Set<Integer> keySet = buildingMap.keySet();
        int i = 0;
        int[] idList = new int[buildingMap.size()];
        for (Integer key : keySet) {
            idList[i++] = key;
        }
        return idList;
    }

    public MapObject getMapObjectInfo(int id) {
        return buildingMap.get(id);
    }

    public Point getMapObjectPos(int id) {
        return buildingPos.get(id);
    }

    public String getMapInfoJson() {
        Gson gson = new Gson();
        String json = null;
        JsonArray jArr = new JsonArray();
        for (int id : buildingMap.keySet())
        {
            JsonElement jObj = gson.toJsonTree(buildingMap.get(id));
            Point point = buildingPos.get(id);
            jObj.getAsJsonObject().addProperty("x", point.x);
            jObj.getAsJsonObject().addProperty("y", point.y);
            jArr.add(jObj);
        }
        json = gson.toJson(jArr);
        return json;
    }

    private boolean checkBuildingPlacement(int id, int xNew, int yNew, int...shape) {
        if (xNew<1 || xNew>ServerConstant.MAP_X-2 || yNew<1 || yNew>ServerConstant.MAP_X-2)
            return false;

        int width = shape.length > 0 ? shape[0] : 0;
        int height = shape.length > 1 ? shape[1] : 0;

        if (id != -1)
        {
            MapObject mapObject = buildingMap.get(id);
            Map<String, String> mapHash = DataLoader.getInstance()
                    .getMap(mapObject.getType(), "1");

            width = Integer.parseInt(mapHash.get("width"));
            height = Integer.parseInt(mapHash.get("height"));
        }

        if ((xNew+width-1 > ServerConstant.MAP_X-2) || (xNew+height-1 > ServerConstant.MAP_Y-2))
            return false;

        Rectangle r1 = new Rectangle(xNew, yNew, width, height);

        for (int key : buildingMap.keySet()) {
            if (key == id)
                continue;

            MapObject mapObject = buildingMap.get(key);

            int posX = buildingPos.get(key).x;
            int posY = buildingPos.get(key).y;

            Map<String, String> mapHash = DataLoader.getInstance()
                    .getMap(mapObject.getType(), "1");

            width = Integer.parseInt(mapHash.get("width"));
            height = Integer.parseInt(mapHash.get("height"));

            Rectangle r2 = new Rectangle(posX, posY, width, height);

            if (r1.intersects(r2))
                return false;
        }

        return true;
    }

    public boolean moveBuilding(int id, int xNew, int yNew) {
        boolean check = checkBuildingPlacement(id, xNew, yNew);

        if (check)
        {
            Point pos = new Point(xNew, yNew);
            buildingPos.put(id, pos);
        }

        return check;
    }

    public int getNumberOfBuilding(String buildingType) {
        if (DataLoader.getInstance().getMapObjectName(buildingType).equals("BuilderHut"))
            return builderIdList.size();
        int count=0;
        for (int key : buildingMap.keySet()) {
            Object mapObject = buildingMap.get(key);

            String type = ((MapObject) mapObject).getType();

            if (type.equals(buildingType))
                count++;
        }
        return  count;
    }

    private boolean checkBuildingLimit(String buildingType) {
        int count = getNumberOfBuilding(buildingType);

        TownHall townHall = (TownHall) buildingMap.get(townHallId);
        if (count == DataLoader.getInstance().
                getBuildingLimit(buildingType, townHall.getLevel()))
            return false;

        return true;
    }

    private boolean checkTownHallLevelLimit(String buildingType, int level) {
        int currentTownHallLevel = buildingMap.get(townHallId).getLevel();
        int requiredTownHallLevel = DataLoader.getInstance().getTownHallLevelLimit(buildingType, level);
        if (requiredTownHallLevel == 0)
            return true;
        if (currentTownHallLevel < requiredTownHallLevel)
            return false;
        return true;
    }

    private int getBuilderHutWithTargetID(int id) {
        for (int builderHutID : builderIdList) {
            BuilderHut builderHut = (BuilderHut) buildingMap.get(builderHutID);
            if (builderHut.getTargetID() == id)
                return builderHutID;
        }
        return -1;
    }

    public int buildBuilding(String type, int posX, int posY) {
        Integer id = -1;

        if (DataLoader.getInstance().getMapObjectName(type).equals("Obstacle"))
            return id;

        Map<String, String> mapHash = DataLoader.getInstance()
                .getMap(type, "1");
        int width = Integer.parseInt(mapHash.get("width"));
        int height = Integer.parseInt(mapHash.get("height"));

        if (!checkBuildingPlacement(id, posX, posY, width, height))
            return id;

        if (!checkBuildingLimit(type))
            return id;

        if (DataLoader.getInstance().getMapObjectName(type).equals("BuilderHut")) {
            return buildBuilderHut(type, posX, posY);
        }

        int builderHutID = getBuilderHutWithTargetID(-1);
        if (builderHutID == -1)
            return id;

        id = freeIdQueue.poll();
        if (id == null)
            id = buildingMap.size();

        MapObject mapObject = createMapObject(id, type, 0);

//        if (DataLoader.getInstance().getMapObjectName(type).equals("Storage"))
//            storageIdList.add(id);
//        if (DataLoader.getInstance().getMapObjectName(type).equals("ArmyCamp"))
//            armyCampIdList.add(id);
//        if (DataLoader.getInstance().getMapObjectName(type).equals("Barrack"))
//            barrackIdList.add(id);

        Point pos = new Point(posX, posY);

        buildingMap.put(id, mapObject);
        buildingPos.put(id, pos);

        setBuilderHutTarget(builderHutID, id);

        return id;
    }

    public int buildBuilderHut(String type, int posX, int posY) {
        Integer id = -1;

        id = freeIdQueue.poll();
        if (id == null)
            id = buildingMap.size();

        MapObject mapObject = createMapObject(id, type, 1);

//        builderIdList.add(id);

        Point pos = new Point(posX, posY);

        buildingMap.put(id, mapObject);
        buildingPos.put(id, pos);

        return id;
    }

    private void setBuilderHutTarget(int builderHutID, int targetID) {
        BuilderHut builderHut = (BuilderHut) buildingMap.get(builderHutID);
        builderHut.setTargetID(targetID);
        if (targetID == -1)
            return;
        buildingMap.get(targetID).
                setStartActionTimestamp(
                        System.currentTimeMillis() / 1000
                );
    }

    public int deleteObstacle(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        int builderHutID = getBuilderHutWithTargetID(-1);
        if (builderHutID == -1)
            return -1;

        String type = buildingMap.get(id).getType();
        int level = buildingMap.get(id).getLevel();

        if (!DataLoader.getInstance().getMapObjectName(type).equals("Obstacle")) {
            return -1;
        }

        setBuilderHutTarget(builderHutID, id);
        return id;
    }

    public int finishDeleteObstacle(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        int builderHutID = getBuilderHutWithTargetID(id);
        if (builderHutID == -1)
            return -1;

        String type = buildingMap.get(id).getType();
        int level = buildingMap.get(id).getLevel();

        if (!DataLoader.getInstance().getMapObjectName(type).equals("Obstacle")) {
            return -1;
        }

        long startActionTimestamp = buildingMap.get(id).getStartActionTimestamp();
        long thisTimestamp = System.currentTimeMillis()/1000;
        long timeLength = Integer.parseInt(
                DataLoader.getInstance().getMap(type, String.valueOf(level)).get("buildTime")
        );

        if (startActionTimestamp + timeLength > thisTimestamp)
            return -1;

        buildingMap.remove(id);
        buildingPos.remove(id);

        freeIdQueue.add(id);

        setBuilderHutTarget(builderHutID, -1);

        return id;
    }

    public int skipDeleteObstacle(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        if (!DataLoader.getInstance().getMapObjectName(
                buildingMap.get(id).getType()).equals("Obstacle")
                ) {
            return -1;
        }

        buildingMap.get(id).setStartActionTimestamp(0);
        return finishDeleteObstacle(id);
    }

    public int upgradeBuilding(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        int builderHutID = getBuilderHutWithTargetID(-1);
        if (builderHutID == -1)
            return -1;

        String type = buildingMap.get(id).getType();
        int level = buildingMap.get(id).getLevel();

        if (DataLoader.getInstance().getMapObjectName(type).equals("BuilderHut")) {
            return -1;
        }

        if (DataLoader.getInstance().getMapObjectName(type).equals("Obstacle")) {
            return -1;
        }

        if (DataLoader.getInstance().getMap(type, String.valueOf(level + 1)) == null)
            return -1;

        if (!checkTownHallLevelLimit(type, level+1))
            return -1;

        setBuilderHutTarget(builderHutID, id);
        return id;
    }

    public int finishUpgradeBuilding(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        int builderHutID = getBuilderHutWithTargetID(id);
        if (builderHutID == -1)
            return -1;

        String type = buildingMap.get(id).getType();
        int level = buildingMap.get(id).getLevel();

        if (DataLoader.getInstance().getMapObjectName(type).equals("BuilderHut")) {
            return -1;
        }

        if (DataLoader.getInstance().getMapObjectName(type).equals("Obstacle")) {
            return -1;
        }

        long startActionTimestamp = buildingMap.get(id).getStartActionTimestamp();
        long thisTimestamp = System.currentTimeMillis()/1000;
        long timeLength = Integer.parseInt(
                DataLoader.getInstance().getMap(type, String.valueOf(level + 1)).get("buildTime")
        );

        if (startActionTimestamp + timeLength > thisTimestamp)
            return -1;

        buildingMap.get(id).setLevel(level + 1);
        buildingMap.get(id).setStartActionTimestamp(0);

        setBuilderHutTarget(builderHutID, -1);

        return id;
    }

    public int skipUpgradeBuilding(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        if (DataLoader.getInstance().getMapObjectName(
                buildingMap.get(id).getType()).equals("Obstacle")
                ) {
            return -1;
        }

        buildingMap.get(id).setStartActionTimestamp(0);
        return finishUpgradeBuilding(id);
    }
   
    public int cancelBuildBuilding(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        int builderHutID = getBuilderHutWithTargetID(id);
        if (builderHutID == -1)
            return -1;

        String type = buildingMap.get(id).getType();

        if (DataLoader.getInstance().getMapObjectName(type).equals("Obstacle"))
            return -1;

        if (DataLoader.getInstance().getMapObjectName(type).equals("BuilderHut")) {
            return -1;
        }

        if (buildingMap.get(id).getLevel() != 0)
            return -1;

        if (buildingMap.get(id).getStartActionTimestamp() == 0)
            return -1;

//        if (DataLoader.getInstance().getMapObjectName(type).equals("Storage")) {
//            storageIdList.remove((Integer) id);
//        }
//        if (DataLoader.getInstance().getMapObjectName(type).equals("ArmyCamp")) {
//            armyCampIdList.remove((Integer) id);
//        }
//        if (DataLoader.getInstance().getMapObjectName(type).equals("Barrack")) {
//            barrackIdList.remove((Integer) id);
//        }

        ArrayList<Integer> list = getListFromType(type);
        if (list != null)
        {
            list.remove(new Integer(id));
        }

        buildingMap.remove(id);
        buildingPos.remove(id);

        freeIdQueue.add(id);

        setBuilderHutTarget(builderHutID, -1);
        return id;
    }

    public int cancelUpgradeBuilding(int id) {
        if (buildingMap.get(id) == null)
            return -1;

        int builderHutID = getBuilderHutWithTargetID(id);
        if (builderHutID == -1)
            return -1;

        String type = buildingMap.get(id).getType();

        if (DataLoader.getInstance().getMapObjectName(type).equals("Obstacle"))
            return -1;

        if (DataLoader.getInstance().getMapObjectName(type).equals("BuilderHut")) {
            return -1;
        }

        if (buildingMap.get(id).getStartActionTimestamp() == 0)
            return -1;

        buildingMap.get(id).setStartActionTimestamp(0);
        
        setBuilderHutTarget(builderHutID, -1);
        return id;
    }

    public int[] updateAllAction() {
        int[] offlineFinishIdList = new int[builderIdList.size()];
        int count = 0;

        for (int builderID : builderIdList) {
            BuilderHut builderHut = (BuilderHut) buildingMap.get(builderID);
            int id = builderHut.getTargetID();
            if (id != -1) {
                String type = buildingMap.get(id).getType();
                if (DataLoader.getInstance().getMapObjectName(type).equals("Obstacle")) {
                    offlineFinishIdList[count] = finishDeleteObstacle(id);
                }
                else {
                    offlineFinishIdList[count] = finishUpgradeBuilding(id);
                }
            }
            count++;
        }
        return offlineFinishIdList;
    }
}
