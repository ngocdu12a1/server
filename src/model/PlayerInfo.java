package model;

import java.awt.Point;

import cmd.obj.demo.DemoDirection;
import cmd.obj.demo.MaxPosition;
import util.database.DataModel;
import util.server.ServerConstant;

public class PlayerInfo extends DataModel {
    // Zing me
    private int id;
    private String name;

    //demo
    public Point position;
    public boolean[][] visitedMap = new boolean[ServerConstant.MAP_X][ServerConstant.MAP_Y];

    //fresher
    private int level;
    private int coin;
    private int[][] campaignResource = new int[ServerConstant.MAX_CAMPAIGN+1][ServerConstant.RESOURCE.values().length];
    private int[] campaignStar = new int[ServerConstant.MAX_CAMPAIGN+1];
    private int currentCampaign = 1;

    public PlayerInfo(int _id, String _name) {
        super();
        id = _id;
        name = _name;

        //demo
        position = new Point(0, 0);
        visitedMap[ServerConstant.DEFAULT_X][ServerConstant.DEFAULT_Y] = true;

        //fresher
        level = 1;
        coin = 0;
    }

    public String toString() {
        return String.format("%s|%s", new Object[] { id, name });
    }

    //Demo
    public Point move(short direction){
        if (direction == DemoDirection.UP.getValue()){
            position.x++;
        }
        else if (direction == DemoDirection.DOWN.getValue()){
            position.x--;
        }
        else if (direction == DemoDirection.RIGHT.getValue()){
            position.y++;
        }
        else{
            position.y--;
        }
        
        position.x = position.x % MaxPosition.X;
        position.y = position.y % MaxPosition.Y;
                
        return position;
    }

    public String getName(){
        return name;
    }

    public String setName(String name){
        this.name = name;
        return this.getName();
    }

    public boolean visit(int x, int y){
        if (visitedMap[x][y])
            return false;
        else {
            visitedMap[x][y] = true;
            position.x = x;
            position.y = y;
            return true;
        }
    }

    public void resetMap(){
        for (int i=0; i<ServerConstant.MAP_X; i++)
            for (int j=0; j<ServerConstant.MAP_Y; j++)
                visitedMap[i][j] = false;
        visitedMap[ServerConstant.DEFAULT_X][ServerConstant.DEFAULT_Y] = true;

        position.x = ServerConstant.DEFAULT_X;
        position.y = ServerConstant.DEFAULT_Y;
    }

    public boolean isValidPos(int x, int y){
        return ( x >= 0 && x < ServerConstant.MAP_X && y >= 0 && y < ServerConstant.MAP_Y);
    }

    public int countNumberOfVisited(){
        int count = 0;
        for (int i=0; i<ServerConstant.MAP_X; i++)
            for (int j=0; j<ServerConstant.MAP_Y; j++)
                if (visitedMap[i][j] == true)
                    count++;
        return count;
    }

    //Fresher

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int[][] getCampaignResource() {
        return campaignResource;
    }

    public void updateCampaignResource(int level, int[] resource) {
        for (int i=0; i<ServerConstant.RESOURCE.values().length; i++) {
            campaignResource[level][i] += resource[i];
        }
    }

    public int[] getCampaignStar() {
        return campaignStar;
    }

    public void setCampaignStar(int level, int star) {
        campaignStar[level] = star;
    }

    public int getCurrentCampaign() {
        return currentCampaign;
    }

    public void setCurrentCampaign(int currentCampaign) {
        this.currentCampaign = currentCampaign;
    }
}
