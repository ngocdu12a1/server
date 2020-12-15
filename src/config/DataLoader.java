package config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import util.server.ServerConstant;

public class DataLoader {
    private Map<String, Map<String, Map<String, String>>> hashMap = new HashMap<>();
    private Map<String, String> hashMapType_Name = new HashMap<>();
    private Map<String, Integer> hashMapTroop_Enum = new HashMap<>();
    private Map<Integer, String> hashMapEnum_Troop = new HashMap<>();

    public static DataLoader getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final DataLoader INSTANCE = new DataLoader();
    }

    private void addMap (String type, String filename) {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();

        try {
            String path = "conf/json/"+filename+".json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            json = gson.fromJson(bufferedReader, JsonObject.class);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
            e.printStackTrace();
        }

        int count = 0;
        Set<Map.Entry<String, JsonElement>> entries1 = json.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry1: entries1) {
            JsonObject jObject1 = entry1.getValue().getAsJsonObject();

            Map<String, Map<String, String>> tempMap = new HashMap<>();

            if (filename.contains("Base"))
            {
                Map<String, String> retMap = new Gson().fromJson(
                        jObject1, new TypeToken<HashMap<String, String>>() {}.getType()
                );
                tempMap.put("base", retMap);

                if (filename.equals("TroopBase")) {
                    hashMapEnum_Troop.put(count, entry1.getKey());
                    hashMapTroop_Enum.put(entry1.getKey(), count++);
                }
            }
            else
            {
                Set<Map.Entry<String, JsonElement>> entries2 = jObject1.entrySet();//will return members of your object
                for (Map.Entry<String, JsonElement> entry2: entries2) {
                    JsonObject jObject2 = entry2.getValue().getAsJsonObject();
                    Map<String, String> retMap = new Gson().fromJson(
                            jObject2, new TypeToken<HashMap<String, String>>() {}.getType()
                    );
                    tempMap.put(entry2.getKey(), retMap);
                }
            }

            if (hashMap.get(entry1.getKey()) != null)
            {
                Map<String, Map<String, String>> existedMap = hashMap.get(entry1.getKey());
                tempMap.putAll(existedMap);
            }
            hashMap.put(entry1.getKey(), tempMap);

            if (!filename.contains("Base"))
                hashMapType_Name.put(entry1.getKey(), filename);
        }
    }

    public DataLoader () {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();

        try {
            String path = "conf/json/config.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            json = gson.fromJson(bufferedReader, JsonObject.class);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
            e.printStackTrace();
        }

        Set<Map.Entry<String, JsonElement>> entries = json.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry: entries) {
            if (entry.getValue().isJsonArray())
            {
                List<String> filenames = gson.fromJson(
                        entry.getValue(), new TypeToken<List<String>>() {}.getType()
                );
                for (String filename : filenames)
                {
                    addMap(entry.getKey(), filename);
                }
            }
            else
                addMap(entry.getKey(), entry.getValue().getAsString());
        }
    }

    public String initMap() {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();
        JsonArray jArray = new JsonArray();

        int currentId = 0;

        try {
            String path = "conf/json/InitGame.json";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));

            json = gson.fromJson(bufferedReader, JsonObject.class);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
            e.printStackTrace();
        }

        Set<Map.Entry<String, JsonElement>> entries = json.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry1: entries) {
            if (entry1.getKey().equals("map"))
            {
                JsonObject jObject1 = entry1.getValue().getAsJsonObject();

                Set<Map.Entry<String, JsonElement>> entries2 = jObject1.entrySet();//will return members of your object
                for (Map.Entry<String, JsonElement> entry2: entries2) {
                    JsonObject jObject2 = entry2.getValue().getAsJsonObject();

                    JsonObject tempObj = new JsonObject();
                    tempObj.addProperty("id", currentId++);
                    tempObj.addProperty("type", entry2.getKey());
                    tempObj.add("posX", jObject2.get("posX"));
                    tempObj.add("posY", jObject2.get("posY"));
                    tempObj.addProperty("startActionTimestamp", 0);
                    tempObj.addProperty("level", 1);
                    jArray.add(tempObj);
                }
            }
            else if (entry1.getKey().equals("obs"))
            {
                JsonObject jObject1 = entry1.getValue().getAsJsonObject();

                Set<Map.Entry<String, JsonElement>> entries2 = jObject1.entrySet();//will return members of your object
                for (Map.Entry<String, JsonElement> entry2: entries2) {
                    JsonObject jObject2 = entry2.getValue().getAsJsonObject();

                    JsonObject tempObj = new JsonObject();
                    tempObj.addProperty("id", currentId++);
                    tempObj.add("type", jObject2.get("type"));
                    tempObj.add("posX", jObject2.get("posX"));
                    tempObj.add("posY", jObject2.get("posY"));
                    tempObj.addProperty("startActionTimestamp", 0);
                    tempObj.addProperty("level", 1);
                    jArray.add(tempObj);
                }
            }
        }

        return gson.toJson(jArray);
    }

    public JsonObject getCampaignMap(int level) {
        Gson gson = new Gson();
        JsonObject json = new JsonObject();

        try {
            String path = "conf/campaign/"+String.valueOf(level)+".map";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            json = gson.fromJson(bufferedReader, JsonObject.class);
        }
        catch (FileNotFoundException e)
        {
            System.out.println("File not found!");
            e.printStackTrace();
        }

        return json;
    }

    public Map<String, String> getMap(String key, String iterator) {
        Map<String, String> retMap = hashMap.get(key).get(iterator);
        return retMap;
    }

    public String getMapObjectName(String type) {
        String name = hashMapType_Name.get(type);
        if (name != null)
            return hashMapType_Name.get(type);
        else
            return "";
    }

    public int getEnumFromTroopType(String type) {
        return hashMapTroop_Enum.get(type);
    }

    public String getTroopTypeFromEnum(int index) {
        return hashMapEnum_Troop.get(index);
    }

    public int getMaxTroop() {
        return hashMapTroop_Enum.size();
    }

    public int getBuildingLimit(String type, int townHallLevel) {
        if (hashMapType_Name.get(type).equals("BuilderHut"))
        {
            return hashMap.get(type).size();
        }
        String count = hashMap.get("TOW_1").get(String.valueOf(townHallLevel)).get(type);
        return (Integer.parseInt(count));
    }

    public int getTownHallLevelLimit(String type, int level) {
        return convertToInt(hashMap.get(type).get(String.valueOf(level)).get("townHallLevelRequired"));
    }

    private Integer convertToInt(String str) {
        int n=0;
        if(str != null) {
            n = Integer.parseInt(str);
        }
        return n;
    }

    public int[] getRequiredResource(String type, int level) {
        int[] requiredResource = new int[ServerConstant.RESOURCE.values().length];

        if (!getMapObjectName(type).equals("Troop")) {
            Map<String, String> map = hashMap.get(type).get(String.valueOf(level));
            requiredResource[ServerConstant.RESOURCE.GOLD.getValue()] = convertToInt(map.get("gold"));
            requiredResource[ServerConstant.RESOURCE.ELIXIR.getValue()] = convertToInt(map.get("elixir"));
            requiredResource[ServerConstant.RESOURCE.DARK_ELIXIR.getValue()] = convertToInt(map.get("darkElixir"));
            requiredResource[ServerConstant.RESOURCE.COIN.getValue()] = convertToInt(map.get("coin"));
        }
        else {
            Map<String, String> map = hashMap.get(type).get(String.valueOf(level));
            requiredResource[ServerConstant.RESOURCE.GOLD.getValue()] = convertToInt(map.get("trainingGold"));
            requiredResource[ServerConstant.RESOURCE.ELIXIR.getValue()] = convertToInt(map.get("trainingElixir"));
            requiredResource[ServerConstant.RESOURCE.DARK_ELIXIR.getValue()] = convertToInt(map.get("trainingDarkElixir"));
            requiredResource[ServerConstant.RESOURCE.COIN.getValue()] = convertToInt(map.get("trainingCoin"));
        }
        
        return requiredResource;
    }

    public int getBulletSpeed(String type) {
        if (type == null)
            return 0;
        switch (type) {
            case ("DEF_1"):
                return 900;
            case ("DEF_2"):
                return 600;
            case ("DEF_3"):
                return 250;
            case ("DEF_4"):
                return 600;
            case ("ARM_2"):
                return 600;
            default:
                return 0;
        }
    }
}
