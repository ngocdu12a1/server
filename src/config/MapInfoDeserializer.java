package config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;

import java.awt.*;
import java.lang.reflect.Type;
import java.util.*;

public class MapInfoDeserializer implements JsonDeserializer<MapInfo> {

    public MapInfo deserialize(JsonElement json, Type typeOfT,
                                       JsonDeserializationContext context) throws JsonParseException {

        MapInfo mInfo = new MapInfo(0);

        JsonObject jObj = json.getAsJsonObject();

        mInfo.setBuildingMap(context.deserialize(jObj.get("buildingMap"),
                new TypeToken<HashMap<Integer, MapObject>>() {}.getType()));
        mInfo.setBuildingPos(context.deserialize(jObj.get("buildingPos"),
                new TypeToken<Map<Integer, Point>>() {}.getType()));
        mInfo.setFreeIdQueue(context.deserialize(jObj.get("freeIdQueue"),
                new TypeToken<PriorityQueue<Integer>>() {}.getType()));
        mInfo.setBuilderIdList(context.deserialize(jObj.get("builderIdList"),
                new TypeToken<ArrayList<Integer>>() {}.getType()));
        mInfo.setStorageIdList(context.deserialize(jObj.get("storageIdList"),
                new TypeToken<ArrayList<Integer>>() {}.getType()));
        mInfo.setBarrackIdList(context.deserialize(jObj.get("barrackIdList"),
                new TypeToken<ArrayList<Integer>>() {}.getType()));
        mInfo.setArmyCampIdList(context.deserialize(jObj.get("armyCampIdList"),
                new TypeToken<ArrayList<Integer>>() {}.getType()));
        mInfo.setDefenceIdList(context.deserialize(jObj.get("defenceIdList"),
                new TypeToken<ArrayList<Integer>>() {}.getType()));
        mInfo.setTownHallId(context.deserialize(jObj.get("townHallId"),
                Integer.class));

        return mInfo;
    }

}
