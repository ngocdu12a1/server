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
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;

import java.awt.*;
import java.nio.ByteBuffer;

public class ResponseGetMapInfoList extends BaseMsg {
    private MapInfo mInfo;

    public ResponseGetMapInfoList(short error, MapInfo mInfo) {
        super(CmdDefine.GET_MAP_INFO_LIST, error);
        this.mInfo = mInfo;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        int[] idList = mInfo.getIdList();

        bf.putInt(idList.length);

        for (int id : idList) {
            MapObject mapObjectInfo = mInfo.getMapObjectInfo(id);
            Point mapObjectPos = mInfo.getMapObjectPos(id);

            bf.putInt(id);

            bf.putInt(mapObjectPos.x);
            bf.putInt(mapObjectPos.y);

            putStr(bf, mapObjectInfo.getType());
            bf.putInt(mapObjectInfo.getLevel());
            bf.putLong(mapObjectInfo.getStartActionTimestamp());

            String mapObjectName = DataLoader.getInstance()
                    .getMapObjectName(mapObjectInfo.getType());
            switch (mapObjectName)
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
        }

        return packBuffer(bf);
    }

}
