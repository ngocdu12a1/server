package util.troopUtils;

import config.DataLoader;
import model.attackMapInfo.Troop;
import model.mapInfo.Building.Building;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import util.mapUtils.MapUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by CPU60126_LOCAL on 2020-08-05.
 */
public class TroopUtils {
    public static TroopUtils getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final TroopUtils INSTANCE = new TroopUtils();
    }

    public ArrayList<Node> findPath(int[][] mapGrid, Point troopPos, Point targetPos, int id) {
        Astar astar = new Astar();
        return astar.search(mapGrid, troopPos.x, troopPos.y, targetPos.x, targetPos.y, id);
    }

    public Target findTarget(Troop troop, int troopId, MapInfo mapInfo) {
        Target result = new Target(-1, 0 ,0);

        Point troopPos = troop.getTroopPos();
        switch (troop.getType()) {
            case ("ARM_1"):
                int[] idList = mapInfo.getIdList();

                int minDistance = 999999999;

                for (int id : idList) {
                    MapObject mapObject = mapInfo.getMapObjectInfo(id);
                    if (!mapInfo.checkMapObjectName(mapObject.getType(), "Obstacle")) {
                        Building building = (Building) mapObject;
                        Point buildingPos = mapInfo.getMapObjectPos(id);
                        Point buildingGridPos = (Point) buildingPos.clone();
                        buildingGridPos.x *= 3;
                        buildingGridPos.y *= 3;

                        Map<String, String> mapHash = DataLoader.getInstance()
                                .getMap(building.getType(), String.valueOf(building.getLevel()));
                        int width = Integer.parseInt(mapHash.get("width"));
                        int height = Integer.parseInt(mapHash.get("height"));

                        int[] centerX = new int[] {1, 1, -1, -1};
                        int[] centerY = new int[] {1, -1, -1, 1};
                        int[] deltaX = new int[] {0, 0, width*3-1, width*3-1};
                        int[] deltaY = new int[] {0, height*3-1, height*3-1, 0};

                        for (int i=0; i<deltaX.length; i++) {
                            Point buildingGrid = new Point(buildingGridPos.x + deltaX[i], buildingGridPos.y + deltaY[i]);

                            int distance = (int) Math.floor(
                                    MapUtils.getInstance().getEuclideanDistance(troopPos, buildingGrid)
                            );

                            if (distance < minDistance) {
                                result.id = id;
                                result.x = buildingGrid.x + centerX[i];
                                result.y = buildingGrid.y + centerY[i];

                                minDistance = distance;

                                int horizontal = 0;
                                int edge = troopId % 2;
                                int edgeVectorX;
                                int edgeVectorY;

                                if (edge == horizontal) {
                                    if (i==0 || i==1) {
                                        edgeVectorX = 1;
                                        edgeVectorY = 0;
                                    }
                                    else {
                                        edgeVectorX = -1;
                                        edgeVectorY = 0;
                                    }
                                }
                                else {
                                    if (i==0 || i==3) {
                                        edgeVectorX = 0;
                                        edgeVectorY = 1;
                                    }
                                    else  {
                                        edgeVectorX = 0;
                                        edgeVectorY = -1;
                                    }
                                }

                                result.x += (troopId % (width*3) - 1) * edgeVectorX;
                                result.y += (troopId % (height*3) - 1) * edgeVectorY;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }

        return result;
    }
}
