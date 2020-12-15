package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import config.DataLoader;
import model.mapInfo.Building.ArmyBuilding.Barrack;
import model.mapInfo.Building.ArmyBuilding.Laboratory;
import model.mapInfo.Building.ResourceBuilding.BuilderHut;
import model.mapInfo.Building.ResourceBuilding.Resource;
import model.mapInfo.Building.ResourceBuilding.Storage;
import model.mapInfo.MapObject;

import java.awt.*;
import java.nio.ByteBuffer;

public class ResponseMoveBuilding extends BaseMsg {
    private int id;
    private int xOld;
    private int yOld;
    private int xNew;
    private int yNew;
    public ResponseMoveBuilding(short error, int id, int xOld, int yOld, int xNew, int yNew) {
        super(CmdDefine.MOVE_BUILDING, error);
        this.id = id;
        this.xOld = xOld;
        this.yOld = yOld;
        this.xNew = xNew;
        this.yNew = yNew;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);
        bf.putInt(xOld);
        bf.putInt(yOld);
        bf.putInt(xNew);
        bf.putInt(yNew);

        return packBuffer(bf);
    }

}
