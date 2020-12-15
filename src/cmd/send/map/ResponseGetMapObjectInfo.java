package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import config.DataLoader;
import model.mapInfo.Building.ArmyBuilding.Barrack;
import model.mapInfo.Building.ArmyBuilding.Laboratory;
import model.mapInfo.Building.ResourceBuilding.BuilderHut;
import model.mapInfo.Building.ResourceBuilding.Resource;
import model.mapInfo.Building.ResourceBuilding.Storage;
import model.mapInfo.Building.TownHall.TownHall;
import model.mapInfo.MapObject;

import java.awt.*;
import java.nio.ByteBuffer;

public class ResponseGetMapObjectInfo extends BaseMsg {
    private int id;
    private Point mapObjectPos;
    private MapObject mapObjectInfo;
    public ResponseGetMapObjectInfo(short error, int id, Point mapObjectPos, MapObject mapObjectInfo) {
        super(CmdDefine.GET_MAP_OBJECT_INFO, error);
        this.id = id;
        this.mapObjectPos = mapObjectPos;
        this.mapObjectInfo = mapObjectInfo;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);

        bf.putInt(mapObjectPos.x);
        bf.putInt(mapObjectPos.y);

        putStr(bf, mapObjectInfo.getType());
        bf.putInt(mapObjectInfo.getLevel());
        bf.putLong(mapObjectInfo.getStartActionTimestamp());

        String mapObjectType = DataLoader.getInstance()
                .getMapObjectName(mapObjectInfo.getType());
        switch (mapObjectType)
        {
            case "Obstacle":
                break;
            case "ArmyCamp":
                break;
            case "Barrack":
                bf.putLong(((Barrack) mapObjectInfo).getTrainTimestamp());
                break;
            case "Laboratory":
                putBoolean(bf, ((Laboratory) mapObjectInfo).getIsResearching());
                break;
            case "Defence":
                break;
            case "Wall":
                break;
            case "BuilderHut":
                bf.putInt(((BuilderHut) mapObjectInfo).getTargetID());
                break;
            case "Resource":
                bf.putInt(((Resource) mapObjectInfo).getQuantity());
                bf.putLong(((Resource) mapObjectInfo).getHarvestTimeStamp());
                break;
            case "Storage":
                bf.putInt(((Storage) mapObjectInfo).getQuantity());
                break;
            case "TownHall":
                bf.putInt(((TownHall) mapObjectInfo).getGold());
                bf.putInt(((TownHall) mapObjectInfo).getElixir());
                bf.putInt(((TownHall) mapObjectInfo).getDarkElixir());
                break;
            default:
                break;
        }
        return packBuffer(bf);
    }

}
