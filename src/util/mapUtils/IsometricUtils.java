package util.mapUtils;

import util.server.ServerConstant;

import java.awt.*;


public class IsometricUtils {
    public static IsometricUtils getInstance() {
        return SingletonHelper.INSTANCE;
    }

    private static class SingletonHelper {
        private static final IsometricUtils INSTANCE = new IsometricUtils();
    }

    int tileWidth = ServerConstant.GRASS_TILE_WIDTH;
    int tileHeight = ServerConstant.GRASS_TILE_HEIGHT;
    Point rootPosition = ServerConstant.ROOT_TILE_POS;

    public IsoPoint isoTo2D(IsoPoint p) {
        IsoPoint temp = new IsoPoint(0, 0);
        temp.y = (this.tileWidth * p.y - this.tileHeight * p.x) / 2;
        temp.x = (this.tileWidth * p.y + this.tileHeight * p.x) / 2;
        return temp;
    }

    public IsoPoint twoDToIso(IsoPoint p) {
        IsoPoint temp = new IsoPoint(0, 0);
        temp.y = (p.x + p.y) / this.tileWidth;
        temp.x = (p.x - p.y) / this.tileHeight;
        return temp;
    }

    public double getTile2DSize() {
        IsoPoint p = new IsoPoint(this.tileWidth / 2, this.tileHeight / 2);
        IsoPoint result = this.isoTo2D(p);
        return result.x;
    }

    /*
    Iso map root position is p(width / 2, 0)
     */
    public Point isoToTilePos( IsoPoint pos) {
        IsoPoint p = new IsoPoint(pos.x, pos.y);
        //var p = pos;
        IsoPoint twoDP = this.isoTo2D(p);
        Point result = new Point(0, 0);
        double tile2DSize = this.getTile2DSize();
        result.x = (int) Math.floor(twoDP.x / tile2DSize);
        result.y = (int) Math.floor(twoDP.y / tile2DSize);
        result.x -= this.rootPosition.x;
        result.y -= this.rootPosition.y;
        return result;
    }

    public IsoPoint tilePosToIso(Point pos) {
        IsoPoint p = new IsoPoint(pos.x + this.rootPosition.x, pos.y + this.rootPosition.y);
        double tile2DSize = this.getTile2DSize();
        p.x *= tile2DSize;
        p.y *= tile2DSize;
        return this.twoDToIso(p);
    }


    // get center x3Tile of Tile postition
    public Point tilePosToX3TilePos(Point pos) {
        return new Point(pos.x * 3 + 1, pos.y * 3 + 1);
    }

    public Point x3TilePosToTilePos(Point pos) {
        return new Point((int) Math.floor((double) pos.x / 3), (int) Math.floor((double) pos.y / 3));
    }

    public IsoPoint x3TilePosToIso(Point pos) {
        IsoPoint p = new IsoPoint(pos.x + this.rootPosition.x * 3, pos.y + this.rootPosition.y * 3);
        double x3Tile2DSize = this.getTile2DSize() / 3;
        p.x *= x3Tile2DSize;
        p.y *= x3Tile2DSize;
        return this.twoDToIso(p);
    }

    public Point isoToX3TilePos(IsoPoint pos) {
        IsoPoint p = new IsoPoint(pos.x, pos.y);
        //var p = pos;
        IsoPoint twoDP = this.isoTo2D(p);
        Point result = new Point(0, 0);
        double tile2DSize = this.getTile2DSize() / 3;
        result.x = (int) Math.floor(twoDP.x / tile2DSize);
        result.y = (int) Math.floor(twoDP.y / tile2DSize);
        result.x -= this.rootPosition.x * 3;
        result.y -= this.rootPosition.y * 3;
        return result;
    }

    public IsoPoint centerTilePosToIsoPos(Point pos) {
        IsoPoint isoPos = this.tilePosToIso(new Point(pos.x, pos.y));
        return new IsoPoint(isoPos.x, isoPos.y + this.tileHeight / 2);
    }

    public IsoPoint centerX3TilePosToIsoPos(Point pos) {
        IsoPoint isoPos = this.x3TilePosToIso(new Point(pos.x, pos.y));
        return new IsoPoint(isoPos.x, isoPos.y + this.tileHeight / 6);
    }
}
