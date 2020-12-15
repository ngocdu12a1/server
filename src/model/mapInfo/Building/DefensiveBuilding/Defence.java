package model.mapInfo.Building.DefensiveBuilding;

import config.DataLoader;
import model.attackMapInfo.AttackManager;
import model.attackMapInfo.Troop;
import model.mapInfo.Building.Building;
import util.mapUtils.IsoPoint;
import util.mapUtils.IsometricUtils;
import util.mapUtils.MapUtils;

import java.awt.*;
import java.util.Map;

/**
 * Created by CPU60126_LOCAL on 2020-07-01.
 */
class ClosestTroop {
    public int id;
    public double distance;

    ClosestTroop(int id, double distance) {
        this.id = id;
        this.distance = distance;
    }
}

public class Defence extends Building{
    int targetId;
    int cooldownTime = 0;
    ClosestTroop closestTroop;

    int damage;

    public Defence(int id, String type, int level) {
        super(id, type, level);

        damage = Integer.parseInt(DataLoader.getInstance().getMap(type, String.valueOf(level)).get("damagePerShot"));
    }

    public int getTarget() {
        return targetId;
    }

    public boolean isAttacking() {
        return (this.targetId != -1);
    }

    public boolean inCooldown() {
        return (this.cooldownTime > 0);
    }

    private int getMinAttackRange() {
        return Integer.parseInt(
                DataLoader.getInstance().getMap(this.getType(), "base").get("minRange")
        ) * 3;
    }

    private int getMaxAttackRange() {
        return Integer.parseInt(
                DataLoader.getInstance().getMap(this.getType(), "base").get("maxRange")
        ) * 3;
    }

    public int getDamagePerShot() {
        String damage = DataLoader.getInstance().getMap(
                this.getType(), String.valueOf(this.getLevel())
        ).get("damagePerShot");

        return Integer.parseInt(damage);
    }

    public void resetCooldown() {
        float cooldown = Float.parseFloat(DataLoader.getInstance().getMap(this.getType(), "base").get("attackSpeed"));
        cooldown = cooldown*1000;
        cooldownTime = (int) cooldown;
    }

    public void updateCooldown(int dt) {
        cooldownTime -= dt;
    }

    private Point getCenterPos(Point pos) {
        Map<String, String> mapHash = DataLoader.getInstance()
                .getMap(this.getType(), String.valueOf(this.getLevel()));
        int width = Integer.parseInt(mapHash.get("width")) * 3;
        int height = Integer.parseInt(mapHash.get("height")) * 3;

        int centerX = pos.x * 3 + (int)Math.ceil(width / 2.0) - 1;
        int centerY = pos.y * 3 + (int)Math.ceil(height / 2.0) - 1;

        return new Point(centerX, centerY);
    }

    public IsoPoint getCenterIsoPos(Point bdPos) {
        Point centerPos = getCenterPos(bdPos);
        return IsometricUtils.getInstance().centerX3TilePosToIsoPos(centerPos);
    }

    public void updateClosestTroop (int troopId, Point troopPos, Point bdPos) {
        Point buildingPos = getCenterPos(bdPos);
        double distance = MapUtils.getInstance().getEuclideanDistance(buildingPos, troopPos);
        if (closestTroop == null || closestTroop.distance < distance) {
            closestTroop = new ClosestTroop(troopId, distance);
        }
    }

    public void updateTarget(AttackManager attackManager, Point bdPos) {
        if (targetId != -1) {
            Troop troop = attackManager.getTroopById(targetId);

            if (troop == null) {
                targetId = -1;
                return;
            }

            Point troopPos = troop.getTroopPos();
            Point buildingPos = getCenterPos(bdPos);
            int minRange = getMinAttackRange();
            int maxRange = getMaxAttackRange();
            double distance = MapUtils.getInstance().getEuclideanDistance(buildingPos, troopPos);
            if (distance < minRange || distance > maxRange) {
                targetId = -1;
            }
        }
        else {
            if (closestTroop == null)
                return;
            targetId = closestTroop.id;
            closestTroop = null;
        }
    }
}
