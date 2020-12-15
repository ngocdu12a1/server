package util.mapUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import util.server.ServerConstant;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapUtils {
    public static MapUtils getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final MapUtils INSTANCE = new MapUtils();
    }

    public double getEuclideanDistance(Point p1, Point p2) {
        int diffX = p1.x - p2.x;
        int diffY = p1.y - p2.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public double getEuclideanDistance(IsoPoint p1, IsoPoint p2) {
        double diffX = p1.x - p2.x;
        double diffY = p1.y - p2.y;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    public int getManhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public double getDiagonalDistance(Point p1, Point p2) {
        double D = 1;
        double D2 = Math.sqrt(2);
        int d1 = Math.abs(p1.x - p2.x);
        int d2 = Math.abs(p1.y - p2.y);
        return (D * (d1 + d2)) + ((D2 - (2 * D)) * Math.min(d1, d2));
    }

    public int getBulletFlyingTick(double distance, int speed) {
        return (int) Math.ceil(distance / speed / ServerConstant.TICK_LENGTH);
    }
}
