package model.attackMapInfo;

import config.DataLoader;
import model.PlayerInfo;
import model.attackMapInfo.campaignMapInfo.CampaignMapInfo;
import model.mapInfo.Building.Building;
import model.mapInfo.Building.DefensiveBuilding.Defence;
import model.mapInfo.Building.ResourceBuilding.Storage;
import model.mapInfo.Building.TownHall.TownHall;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import model.troopInfo.TroopInfo;
import util.database.DataModel;
import util.mapUtils.IsoPoint;
import util.mapUtils.IsometricUtils;
import util.mapUtils.MapUtils;
import util.server.ServerConstant;
import util.troopUtils.Target;
import util.troopUtils.TroopUtils;

import java.awt.*;
import java.util.*;

/**
 * Created by CPU60126_LOCAL on 2020-07-28.
 */
class Action {
    public String troopType;
    public int id;
    public int x;
    public int y;
    public int tick;

    public Action(String troopType, int id, int x, int y, int tick) {
        this.troopType = troopType;
        this.id = id;
        this.x = x;
        this.y = y;
        this.tick = tick;
    }
}

public class AttackManager extends DataModel {
    CampaignMapInfo campaignMapInfo;
    MapGrid mapGrid;

    int totalBuilding;
    int destroyedBuilding = 0;

    LinkedList<Bullet> bulletList = new LinkedList<>();

    int[] troopQuantity = new int[DataLoader.getInstance().getMaxTroop()];
    Map<Integer, Troop> troopMap = new HashMap<>();

    int level;
    int star = 0;
    int gold = 0;
    int elixir = 0;

    Map<Integer, Action> actionMap = new TreeMap<>();
    int tickCount = 0;

    public CampaignMapInfo getCampaignMapInfo() {
        return campaignMapInfo;
    }

    public int getLevel() {
        return level;
    }

    public int getStar() {
        return star;
    }

    public int getGold() {
        return gold;
    }

    public int getElixir() {
        return elixir;
    }

    public AttackManager(PlayerInfo playerInfo, int level) {
        this.level = level;
        int[] acquiredResource = playerInfo.getCampaignResource()[level];
        campaignMapInfo = new CampaignMapInfo(level, acquiredResource);
        this.mapGrid = new MapGrid(campaignMapInfo.createMapGrid(), campaignMapInfo.createBorderGrid());
        this.totalBuilding = campaignMapInfo.getTotalBuilding();
        this.initDefenceRange();

        actionMap.put(-1, new Action("debug", -1, 0, 0, 0));
    }

    public int setTroop(MapInfo mapInfo, int[] selectedTroop) {
        TroopInfo troopInfo = new TroopInfo(mapInfo);
        if (troopInfo.subTroop(selectedTroop) != -1) {
            troopQuantity = selectedTroop.clone();
            return 0;
        }
        return -1;
    }

    public void initDefenceRange() {
        ArrayList<Integer> listDefId = campaignMapInfo.getDefenceIdList();
        for(int id : listDefId) {
            Defence defence = (Defence) campaignMapInfo.getMapObjectInfo(id);
            Point pos = campaignMapInfo.getMapObjectPos(id);

            String type = defence.getType();
            int level = defence.getLevel();

            Map<String, String> mapHash = DataLoader.getInstance()
                    .getMap(type, String.valueOf(level));
            int width = Integer.parseInt(mapHash.get("width")) * 3;
            int height = Integer.parseInt(mapHash.get("height")) * 3;

            mapHash = DataLoader.getInstance()
                    .getMap(type, "base");
            int minRange = Integer.parseInt(mapHash.get("minRange")) * 3;
            int maxRange = Integer.parseInt(mapHash.get("maxRange")) * 3;

            mapGrid.addDefRange(pos.x * 3, pos.y * 3, width, height, id, maxRange);
            mapGrid.removeDefRange(pos.x * 3, pos.y * 3, width, height, id, minRange-1);
        }
    }

    public int dropTroop(String troopType, int id, int x, int y, int tick) {
        int troopEnum = DataLoader.getInstance().getEnumFromTroopType(troopType);
//        if (troopQuantity[troopEnum] < 1)
//            return -1;
//        if (!mapGrid.checkBorder(x, y))
//            return -1;

        Action action = new Action(troopType, id, x, y, tick);
        actionMap.put(id, action);

        long timestamp = System.currentTimeMillis() / 1000;

        while (actionMap.get(id-1) == null) {
            if (System.currentTimeMillis() / 1000 - timestamp >= 30)
                return -1;
        }

        while (tickCount < actionMap.get(id-1).tick) {
            if (System.currentTimeMillis() / 1000 - timestamp >= 30)
                return -1;
        }

        while (tickCount < tick) {
            updateGameState();
        }

        Troop troop = new Troop(troopType, new Point(x, y));
        addTroop(troop, id);

        troopQuantity[troopEnum] -= 1;

//      System.out.println("Drop troop: "+id+" "+x+" "+y+" "+tick);

        return 0;
    }

    public int endCampaign(PlayerInfo playerInfo, MapInfo mapInfo, int tick) {
        while (tickCount <= tick) {
            updateGameState();
        }

        int[] acquiredResource = new int[ServerConstant.RESOURCE.values().length];
        acquiredResource[ServerConstant.RESOURCE.GOLD.getValue()] = gold;
        acquiredResource[ServerConstant.RESOURCE.ELIXIR.getValue()] = elixir;

        playerInfo.setCampaignStar(level, Math.max(playerInfo.getCampaignStar()[level], star));
        playerInfo.updateCampaignResource(level, acquiredResource);
        mapInfo.updateResource(acquiredResource.clone());
        if (star > 0) {
            playerInfo.setCurrentCampaign(Math.max(playerInfo.getCurrentCampaign(), level+1));
        }

//        System.out.println("End campaign: "+tick);

        return 0;
    }

    private void attackTroop(int troopId, int damage) {
        Troop troop = troopMap.get(troopId);
        if (troop != null)
            troop.hitpoints -= damage;
    }

    public void attackBuilding(int buildingId, int damage) {
        Building building = (Building) campaignMapInfo.getMapObjectInfo(buildingId);
        if (building == null)
            return;
        int health = building.getHealth();
        building.setHealth(health - damage);

        if (campaignMapInfo.checkMapObjectName(building.getType(), "Storage")) {
            Map<String, String> mapHash = DataLoader.getInstance().getMap(
                    building.getType(), String.valueOf(building.getLevel())
            );

            Storage storage = (Storage) building;

            int takenResource;
            if (storage.getHealth() > 0) {
                int totalHealth = Integer.parseInt(
                        mapHash.get("hitpoints")
                );
                takenResource = (int) Math.ceil(
                        ((float) damage / totalHealth) * storage.getQuantity()
                );
                storage.setQuantity(storage.getQuantity() - takenResource);
            }
            else {
                takenResource = storage.getQuantity();
            }

            String harvestType = mapHash.get("type");
            switch (harvestType) {
                case "gold":
                    gold += takenResource;
                    break;
                case "elixir":
                    elixir += takenResource;
                    break;
                default:
                    break;
            }
        }

        if (campaignMapInfo.checkMapObjectName(building.getType(), "TownHall")) {
            Map<String, String> mapHash = DataLoader.getInstance().getMap(
                    building.getType(), String.valueOf(building.getLevel())
            );

            TownHall townHall = (TownHall) building;

            int takenGold;
            int takenElixir;
            if (townHall.getHealth() > 0) {
                int totalHealth = Integer.parseInt(
                        mapHash.get("hitpoints")
                );
                takenGold = (int) Math.ceil(
                        ((float) damage / totalHealth) * townHall.getGold()
                );
                takenElixir = (int) Math.ceil(
                        ((float) damage / totalHealth) * townHall.getElixir()
                );
                townHall.setGold(townHall.getGold() - takenGold);
                townHall.setElixir(townHall.getElixir() - takenElixir);
            }
            else {
                takenGold = townHall.getGold();
                takenElixir = townHall.getElixir();
            }

            gold += takenGold;
            elixir += takenElixir;
        }
    }

    private void updateBullet(int dt) {
        Iterator itr = bulletList.iterator();
        while (itr.hasNext()) {
            Bullet bullet = (Bullet) itr.next();
            String type = bullet.type;

            switch (type) {
                case "DEF_1":
                    bullet.remainingTick -= 1;
                    if (bullet.remainingTick == 0) {
                        attackTroop(bullet.targetId, bullet.damage);
                        itr.remove();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void addCannonBullet(int targetTroopId, IsoPoint centerIsoPos, Point endX3P, int damage) {
        Bullet bullet = new Bullet();
        bullet.type = "DEF_1";

        double distance = MapUtils.getInstance().getEuclideanDistance(centerIsoPos, IsometricUtils.getInstance().centerX3TilePosToIsoPos(endX3P));
        int bulletSpeed = DataLoader.getInstance().getBulletSpeed("DEF_1");
        int flyingTick = MapUtils.getInstance().getBulletFlyingTick(distance, bulletSpeed);


        bullet.targetId = targetTroopId;
        bullet.targetPos = endX3P;
        bullet.damage = damage;
        bullet.remainingTick = flyingTick;

        bulletList.add(bullet);
    }

    public Troop getTroopById(int troopId) {
        return troopMap.get(troopId);
    }

    private void addTroop(Troop troop, int troopId) {
        troopMap.put(troopId, troop);
    }

    private void updateTroop(int dt) {
        ArrayList<Integer> destroyList = new ArrayList<>();

        for (int troopId : troopMap.keySet()) {
            Troop troop = troopMap.get(troopId);

            if (troop.hitpoints <= 0) {
                destroyList.add(troopId);
                continue;
            }

            ArrayList<Integer> listDefId = mapGrid.getDefList(troop.getTroopPos());
            for (int defId : listDefId) {
                Defence defBuilding = (Defence) campaignMapInfo.getMapObjectInfo(defId);
                Point bdPos = campaignMapInfo.getMapObjectPos(defId);
                if (!defBuilding.isAttacking())
                    defBuilding.updateClosestTroop(troopId, troop.getTroopPos(), bdPos);
            }

            if (campaignMapInfo.getMapObjectInfo(troop.buildingId) == null) {
                Target target = TroopUtils.getInstance().findTarget(troop, troopId, campaignMapInfo);
                if (target.id == -1) {
                    continue;
                }
                troop.setAttackMapGrid(mapGrid.getMapGrid());
                troop.attackBuildingByGrid(troopId, target);
            }

            troop.move(ServerConstant.TICK_LENGTH);
            troop.attack(ServerConstant.TICK_LENGTH, this);

//            System.out.println("Troop pos: "+troop.x+" "+troop.y+" "+troop.gridX+" "+troop.gridY);
        }

        for (int troopId : destroyList) {
            troopMap.remove(troopId);
        }
    }

    private void destroyBuilding(int buildingId) {
        Building building = (Building) campaignMapInfo.getMapObjectInfo(buildingId);
        String bdType = building.getType();

        if (!campaignMapInfo.checkMapObjectName(bdType, "Wall") &&
                !campaignMapInfo.checkMapObjectName(bdType, "Obstacle")) {
            destroyedBuilding += 1;

            if ((float) destroyedBuilding / totalBuilding >= 0.5 && (float) (destroyedBuilding-1) / totalBuilding < 0.5) {
                star += 1;
            }

            if (destroyedBuilding / totalBuilding == 1) {
                star += 1;
            }

            if (campaignMapInfo.checkMapObjectName(bdType, "TownHall")) {
                star += 1;
            }
        }

        if (campaignMapInfo.checkMapObjectName(bdType, "Defence")) {
            Defence defence = (Defence) building;
            Point pos = campaignMapInfo.getMapObjectPos(buildingId);

            String type = defence.getType();
            int level = defence.getLevel();

            Map<String, String> mapHash = DataLoader.getInstance()
                    .getMap(type, String.valueOf(level));
            int width = Integer.parseInt(mapHash.get("width")) * 3;
            int height = Integer.parseInt(mapHash.get("height")) * 3;

            mapHash = DataLoader.getInstance()
                    .getMap(type, "base");
            int maxRange = Integer.parseInt(mapHash.get("maxRange")) * 3;

            mapGrid.removeDefRange(pos.x * 3, pos.y * 3, width, height, buildingId, maxRange);

            campaignMapInfo.getDefenceIdList().remove(new Integer(buildingId));
        }

        campaignMapInfo.removeMapObject(buildingId);

//        System.out.println("Destroy building: "+buildingId+" "+tickCount);
    }

    private void updateBuilding(int dt) {
        ArrayList<Integer> destroyList = new ArrayList<>();

        for (int objId : campaignMapInfo.getBuildingMap().keySet()) {
            MapObject mapObject = campaignMapInfo.getMapObjectInfo(objId);
            if (campaignMapInfo.checkMapObjectName(mapObject.getType(), "Defence")) {
                Defence building = (Defence) mapObject;
                Point bdPos = campaignMapInfo.getMapObjectPos(objId);
                building.updateTarget(this, bdPos);
                if (building.isAttacking() && !building.inCooldown()) {
                    Troop troop = troopMap.get(building.getTarget());
                    building.resetCooldown();
                    if (building.getType().equals("DEF_1")) {
                        addCannonBullet(building.getTarget(),
                                building.getCenterIsoPos(bdPos),
                                troop.getTroopPos(),
                                building.getDamagePerShot());
                    }
                }
                if (building.inCooldown())
                    building.updateCooldown(ServerConstant.TICK_LENGTH);
            }
            if (!campaignMapInfo.checkMapObjectName(mapObject.getType(), "Obstacle")) {
                Building building = (Building) mapObject;
                if (building.getHealth() <= 0) {
                    destroyList.add(objId);
                }
//                System.out.println("Building health: " + building.getHealth() + " " + tickCount);
            }
        }

        for (int objId : destroyList) {
            destroyBuilding(objId);
        }
    }

    public void updateGameState(int dt) {
//        System.out.println(tickCount);
        //Update game logic by dt ms
        updateBullet(dt);
        updateTroop(dt);
        updateBuilding(dt);
        tickCount++;
    }

    public void updateGameState() {
        updateGameState(ServerConstant.TICK_LENGTH);
    }
}
