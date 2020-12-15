package model.attackMapInfo;

import util.server.ServerConstant;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Fersher_LOCAL on 8/4/2020.
 */
public class MapGrid {
    private int[][] mapGrid;
    private ArrayList<Integer>[][] defList;
    private int[][] borderGrid;

    public MapGrid(int[][] mapGrid, int[][] borderGrid) {
        this.mapGrid = mapGrid;
        this.borderGrid = borderGrid;
        defList = new ArrayList[mapGrid.length][mapGrid[0].length];
        for (int i=0; i<mapGrid.length; i++)
            for (int j=0; j<mapGrid[0].length; j++)
                defList[i][j] = new ArrayList<Integer>();
    }

    boolean checkBoundary(int x, int y) {
        if (x<0 || x>=mapGrid.length || y<0 || y>=mapGrid[0].length)
            return false;
        return true;
    }

    public int[][] getMapGrid() {
        return mapGrid;
    }

    public int getId(int i, int j) {
        if (!checkBoundary(i, j))
            return ServerConstant.MAP_UNAVAILABLE;
        return this.mapGrid[i][j];
    }

    public ArrayList<Integer> getDefList(Point pos) {
        return this.defList[pos.x][pos.y];
    }

    public void addDefRange(int posX, int posY, int width, int height, int id, int range) {
        int centerX = posX + (int)Math.ceil(width / 2.0) - 1;
        int centerY = posY + (int)Math.ceil(height / 2.0) - 1;

        for (int i = centerX - range; i <= centerX + range; i++) {
            int diffI = centerX - i;
            int maxDiffJ = (int)Math.floor(Math.sqrt(range * range - diffI * diffI));
            for (int j = centerY - maxDiffJ; j <= centerY + maxDiffJ; j++) {
                if (!checkBoundary(i, j))
                    continue;
                defList[i][j].add(id);
            }
        }
    }

    public void removeDefRange(int posX, int posY, int width, int height, int id, int range) {
        int centerX = posX + (int)Math.ceil(width / 2.0) - 1;
        int centerY = posY + (int)Math.ceil(height / 2.0) - 1;

        for (int i = centerX - range; i <= centerX + range; i++) {
            int diffI = centerX - i;
            int maxDiffJ = (int)Math.floor(Math.sqrt(range * range - diffI * diffI));
            for (int j = centerY - maxDiffJ; j <= centerY + maxDiffJ; j++) {
                if (!checkBoundary(i, j))
                    continue;
                for (int k=0; k<defList[i][j].size(); k++) {
                    if (defList[i][j].get(k) == id) {
                        defList[i][j].remove(k);
                        break;
                    }
                }
            }
        }
    }

    public boolean checkBorder(int x, int y) {
        return (borderGrid[x][y] == 1);
    }
}
