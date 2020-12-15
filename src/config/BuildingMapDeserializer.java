package config;

import com.google.gson.*;
import model.mapInfo.Building.ArmyBuilding.ArmyCamp;
import model.mapInfo.Building.ArmyBuilding.Barrack;
import model.mapInfo.Building.ArmyBuilding.Laboratory;
import model.mapInfo.Building.Building;
import model.mapInfo.Building.DefensiveBuilding.Defence;
import model.mapInfo.Building.DefensiveBuilding.Wall;
import model.mapInfo.Building.ResourceBuilding.BuilderHut;
import model.mapInfo.Building.ResourceBuilding.Resource;
import model.mapInfo.Building.ResourceBuilding.Storage;
import model.mapInfo.Building.TownHall.TownHall;
import model.mapInfo.MapObject;
import model.mapInfo.Obstacle.Obstacle;

import java.lang.reflect.Type;
import java.util.*;

public class BuildingMapDeserializer implements JsonDeserializer<HashMap<Integer, MapObject>> {

    private static Map<String, Class> map = new HashMap<String, Class>();

    static {
        map.put("MapObject", MapObject.class);
        map.put("Obstacle", Obstacle.class);
        map.put("Building", Building.class);
        map.put("ArmyCamp", ArmyCamp.class);
        map.put("Barrack", Barrack.class);
        map.put("Laboratory", Laboratory.class);
        map.put("Defence", Defence.class);
        map.put("Wall", Wall.class);
        map.put("BuilderHut", BuilderHut.class);
        map.put("Resource", Resource.class);
        map.put("Storage", Storage.class);
        map.put("TownHall", TownHall.class);
    }

    public HashMap<Integer, MapObject> deserialize(JsonElement json, Type typeOfT,
                                       JsonDeserializationContext context) throws JsonParseException {

        HashMap<Integer,MapObject> hashMap = new HashMap<>();

        JsonObject jObj = json.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = jObj.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry: entries) {
            JsonElement je = entry.getValue();
            String type = je.getAsJsonObject().get("type").getAsString();
            String mapObjectType = DataLoader.getInstance().getMapObjectName(type);
            Class c = map.get(mapObjectType);
            if (c == null)
                throw new RuntimeException("Unknow class: " + type);
            hashMap.put(Integer.parseInt(entry.getKey().toString()), context.deserialize(je, c));
        }
        return hashMap;

    }

}
