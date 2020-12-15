package util.database;

import com.google.gson.Gson;

import java.lang.reflect.Field;

import java.util.*;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import config.BuildingMapDeserializer;
import config.MapInfoDeserializer;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;

import util.server.ServerUtil;

public class DataModel {
//    static TreeMap<Integer, MapObject> buildingMap = new TreeMap<>();
//    static MapInfo mapInfo = new MapInfo(0);
//    static GsonBuilder gb = new GsonBuilder()
//            .registerTypeAdapter(buildingMap.getClass(), new BuildingMapDeserializer())
//            .registerTypeAdapter(mapInfo.getClass(), new MapInfoDeserializer());
    static GsonBuilder gb = new GsonBuilder()
        .registerTypeAdapter(new TypeToken<HashMap<Integer, MapObject>>() {}.getType(), new BuildingMapDeserializer())
        .registerTypeAdapter(new TypeToken<MapInfo>() {
        }.getType(), new MapInfoDeserializer());
    static final Gson gson = gb.create();

    private static final Map<String, List<Field>> cachedMapField = new HashMap<String, List<Field>>();

    public DataModel() {
        super();
    }

    public void saveModel(String key) throws Exception {
        String globalKey = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), key);
        String sobj = gson.toJson(this);
        DataHandler.set(globalKey, sobj);
    }

    public void saveModel(int uId) throws Exception {
        String key = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), uId);
        String sobj = gson.toJson(this);
        DataHandler.set(key, sobj);
        //DataHandler.set(key, this);
    }

    public void saveModel(long uId) throws Exception {
        String key = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), uId);
        String sobj = gson.toJson(this);
        DataHandler.set(key, sobj);
        //DataHandler.set(key, this);
    }

    public static Object getModel(String key, Class c) throws Exception {
        String globalKey = ServerUtil.getModelKeyName(c.getSimpleName(), key);
        return gson.fromJson((String) DataHandler.get(globalKey), c);
        //return DataHandler.get(key);
    }

    public static Object getModel(int uId, Class c) throws Exception {
        String key = ServerUtil.getModelKeyName(c.getSimpleName(), uId);
        return gson.fromJson((String) DataHandler.get(key), c);
        //return DataHandler.get(key);
    }

    public static Object getModel(long uId, Class c) throws Exception {
        String key = ServerUtil.getModelKeyName(c.getSimpleName(), uId);
        return gson.fromJson((String) DataHandler.get(key), c);
        //return DataHandler.get(key);
    }

    public static Object getSocialModel(long uId, Class c) throws Exception {
        String key = ServerUtil.getSocialModelKeyName(c.getSimpleName(), uId);
        return DataHandler.get(key);
    }

    public void saveSocialModel(long uId) throws Exception {
        String key = ServerUtil.getSocialModelKeyName(this.getClass().getSimpleName(), uId);
        DataHandler.set(key, this);
    }

    public static CASValue getS(int uId, Class c) throws Exception {
        String key = ServerUtil.getModelKeyName(c.getSimpleName(), uId);
        return DataHandler.getS(key);
    }

    public boolean checkAndSet(int uId, long valCAS) throws Exception {
        String key = ServerUtil.getModelKeyName(this.getClass().getSimpleName(), uId);
        CASResponse casRes = DataHandler.checkAndSet(key, valCAS, this);
        if (casRes.equals(CASResponse.OK))
            return true;
        else
            return false;
    }

}
