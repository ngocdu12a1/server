package model.attackMapInfo;

import config.DataLoader;
import util.mapUtils.IsoPoint;
import util.mapUtils.IsometricUtils;
import util.server.ServerConstant;
import util.troopUtils.Node;
import util.troopUtils.Target;
import util.troopUtils.TroopUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by CPU60126_LOCAL on 2020-08-05.
 */
public class Troop {
    double x;
    double y;

    int gridX;
    int gridY;

    double prevPositionX;
    double prevPositionY;

    String type;

    int hitpoints;
    int damagePerAttack;
    int attackSpeed;
    double attackRange;
    int attackType;
    int moveSpeed;

    int velocityScale = 7;

    int buildingId;
    int[][] attackMapGrid;

    ArrayList<IsoPoint> path;

    public Troop(String troopType, Point troopPos) {
        Map<String, String> mapBase = DataLoader.getInstance().getMap(troopType, "base");
        Map<String, String> mapLevel = DataLoader.getInstance().getMap(troopType, "1");

        type = troopType;

        hitpoints = Integer.parseInt(mapLevel.get("hitpoints"));
        damagePerAttack = Integer.parseInt(mapLevel.get("damagePerAttack"));
        attackSpeed = Integer.parseInt(mapBase.get("attackSpeed"));
        attackRange = Double.parseDouble(mapBase.get("attackRange"));
        attackType = Integer.parseInt(mapBase.get("attackType"));
        moveSpeed = Integer.parseInt(mapBase.get("moveSpeed")) * velocityScale;

        gridX = troopPos.x;
        gridY = troopPos.y;
        IsoPoint troopIsoPos = IsometricUtils.getInstance().x3TilePosToIso(troopPos);
//        System.out.println("init x y " + troopIsoPos.x + " " + troopIsoPos.y);
        x = troopIsoPos.x;
        y = troopIsoPos.y;

        buildingId = -1;
    }

    public Point getTroopPos() {
        return new Point(gridX, gridY);
    }

    public String getType() {
        return type;
    }

    public void setAttackMapGrid(int[][] mapGrid) {
        attackMapGrid = mapGrid;
    }

    public void moveToBuilding(int troopId, Point targetPos) {
        path = new ArrayList<>();

        Point troopPos = new Point(gridX, gridY);

        ArrayList<Node> pathToBuildingTile = TroopUtils.getInstance().findPath(attackMapGrid, troopPos, targetPos, troopId);

        for (Node node : pathToBuildingTile) {
            Point step = new Point(node.x, node.y);
            IsoPoint stepIso = IsometricUtils.getInstance().x3TilePosToIso(step);
            path.add(stepIso);
//            System.out.println(node.x + " " + node.y);
        }
    }

    public void attackBuildingByGrid(int troopId, Target target) {
        this.buildingId = target.id;
        Point targetPos = new Point(target.x, target.y);
        moveToBuilding(troopId, targetPos);
    }


    public boolean exactCompare(double d1, double d2) {
        return (Math.abs(d1 - d2) < ServerConstant.EPSILON);
    }


    public void move(int dt) {
        double deltaTime = (double) dt / 1000;

        //lấy chiều dài của ô cỏ
        final double grassTileEdge = Math.sqrt(
                ((double) ServerConstant.GRASS_TILE_HEIGHT/2) * ((double) ServerConstant.GRASS_TILE_HEIGHT/2) +
                ((double) ServerConstant.GRASS_TILE_WIDTH/2) * ((double) ServerConstant.GRASS_TILE_WIDTH/2)
        );
//        System.out.println(grassTileEdge);
        //tính sin cos của góc ô cỏ
        final double sinAlpha = (ServerConstant.GRASS_TILE_HEIGHT/2)/grassTileEdge;
        final double cosAlpha = (ServerConstant.GRASS_TILE_WIDTH/2)/grassTileEdge;

//        //remain để cộng bù vào bước đi sau nếu bước đi hiện tại + tốc độ vượt quá ô tiếp theo
//        double remainX = 0;
//        double remainY = 0;


        //if vẫn còn đường đi (mảng kết quả tìm đường có length > 0)
        if (path.size() > 0)
        {
            //lấy điểm đầu tiên trong mảng, anh nhớ đổi nó sang pixel nhé
            //Point current = path[0];
            IsoPoint current = path.get(0);
            //direction vector để cộng vào hướng đi
            int directionVectorX = 0;
            int directionVectorY = 0;

            //tổng đoạn đường sẽ đi
//            double totalLength = Math.floor(Math.sqrt(remainX * remainX + remainY * remainY))  + this.moveSpeed * deltaTime;
            double totalLength = this.moveSpeed * deltaTime;
//            System.out.println("Total length: "+totalLength);
            //chiếu theo trục ox, oy
            double dx = Math.round(totalLength * cosAlpha);
            double dy = Math.round(totalLength * sinAlpha);
//            System.out.println("dx, dy " + dx + " " + dy);

            double stepInTheFutureX;
            double stepInTheFutureY;

            prevPositionX = exactCompare(prevPositionX, 0) ? x : prevPositionX;
            prevPositionY = exactCompare(prevPositionY, 0) ? y : prevPositionY;
//            int direction = this.getDirection(prevPositionX, prevPositionY, current.x, current.y);
            int direction = this.getDirection(current.x, current.y, prevPositionX, prevPositionY);

            //flag xem bước tiếp theo có quá ô ko
            boolean flag = false;
            switch (direction){
                //south
                case 0:
                    directionVectorX = 0;
                    directionVectorY = -1;
                    //this._troop.x += dx * directionVector.x
                    stepInTheFutureY = y + dy * directionVectorY;
                    if (stepInTheFutureY < current.y){
//                        remainY = currentGrid.y - stepInTheFutureY;
                        y = current.y;
                        flag = true;
                    }
                    break;
                //south west
                case 1:
                    directionVectorX = -1;
                    directionVectorY = -1;
                    stepInTheFutureX = x + dx * directionVectorX;
                    stepInTheFutureY = y + dy * directionVectorY;
                    if (stepInTheFutureX < current.x && stepInTheFutureY < current.y){
                        x = current.x;
                        y = current.y;
//                        this._remainX = currentGrid.x - stepInTheFutureX;
//                        this._remainY = currentGrid.y - stepInTheFutureY;
                        flag = true;
                    }
                    break;
                //west
                case 2:
                    directionVectorX = -1;
                    directionVectorY = 0;
                    stepInTheFutureX = x + dx * directionVectorX;
                    if (stepInTheFutureX < current.x){
                        x = current.x;
//                        this._remainX = currentGrid.x - stepInTheFutureX;
                        flag = true;
                    }
                    break;
                //north west
                case 3:
                    directionVectorX = -1;
                    directionVectorY = 1;
                    stepInTheFutureX = x + dx * directionVectorX;
                    stepInTheFutureY = y + dy * directionVectorY;
                    if (stepInTheFutureX < current.x && stepInTheFutureY > current.y){
                        x = current.x;
                        y = current.y;
//                        this._remainX = currentGrid.x - stepInTheFutureX;
//                        this._remainY = stepInTheFutureY - currentGrid.y;
                        flag = true;
                    }
                    break;
                //north
                case 4:
                    directionVectorX = 0;
                    directionVectorY = 1;
                    stepInTheFutureY = y + dy * directionVectorY;
                    if (stepInTheFutureY > current.y){
                        y = current.y;
//                        this._remainY = stepInTheFutureY - currentGrid.y;
                        flag = true;
                    }
                    break;
                //north east
                case 5:
                    directionVectorX = 1;
                    directionVectorY = 1;
                    stepInTheFutureX = x + dx * directionVectorX;
                    stepInTheFutureY = y + dy * directionVectorY;
                    if (stepInTheFutureX > current.x && stepInTheFutureY > current.y){
                        x = current.x;
                        y = current.y;
//                        this._remainX = stepInTheFutureX - currentGrid.x;
//                        this._remainY = stepInTheFutureY - currentGrid.y;
                        flag = true;
                    }
                    break;
                //east
                case 6:
                    directionVectorX = 1;
                    directionVectorY = 0;
                    stepInTheFutureX = x + dx * directionVectorX;
                    if (stepInTheFutureX > current.x){
                        x = current.x;
//                        this._remainX = stepInTheFutureX - currentGrid.x;
                        flag = true;
                    }
                    break;
                //south east
                case 7:
                    directionVectorX = 1;
                    directionVectorY = -1;
                    stepInTheFutureX = x + dx * directionVectorX;
                    stepInTheFutureY = y + dy * directionVectorY;
                    if (stepInTheFutureX > current.x && stepInTheFutureY < current.y){
                        x = current.x;
                        y = current.y;
//                        this._remainX = stepInTheFutureX - currentGrid.x;
//                        this._remainY = currentGrid.y - stepInTheFutureY;
                        flag = true;
                    }
                    break;
                default :
                    break;
            }
            if (!flag) {
                x += dx * directionVectorX;
                y += dy * directionVectorY;
            } else {
                prevPositionX = current.x;
                prevPositionY = current.y;
                //shift
//                this._path.shift();
                path.remove(0);
                flag = false;
            }
        }

        Point troopPos = IsometricUtils.getInstance().isoToX3TilePos(new IsoPoint(x, y));
        gridX = troopPos.x;
        gridY = troopPos.y;
    }

    private int getDirection(double x0, double y0, double x1, double y1){
        if (exactCompare(x0, x1)){
            if (y0 > y1)
                //north
                return 4;
                //south
            else if (y0 < y1) return 0;
        }
        else if (x0 < x1){
            if (y0 > y1)
                //north west
                return 3;
            if (exactCompare(y0, y1))
                //west
                return 2;
            if (y0 < y1)
                //south west
                return 1;
        }
        else if (x0 > x1){
            if (y0 > y1)
                //north east
                return 5;
            if (exactCompare(y0, y1))
                //east
                return 6;
            if (y0 < y1)
                //south east
                return 7;
        }
        return 0;
    }

    public void attack(int dt, AttackManager attackManager){
        if (path.size() == 0) {
            double deltaTime = (double) dt / 1000;
            double damage = this.damagePerAttack * this.attackSpeed * deltaTime;
            //truyền damage với buildingId vào attackManager.attackBuilding
            attackManager.attackBuilding(buildingId, (int) Math.ceil(damage));
        }
    }
}
