package cmd.send.attackMap;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import com.google.gson.JsonObject;
import config.DataLoader;
import model.PlayerInfo;
import model.attackMapInfo.campaignMapInfo.CampaignMapInfo;
import model.mapInfo.Building.ResourceBuilding.Storage;
import model.mapInfo.Building.TownHall.TownHall;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import model.troopInfo.TroopInfo;
import util.server.ServerConstant;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Collections;

public class ResponseEnterCampaign extends BaseMsg {
    private int level;
    private MapInfo mapInfo;
    private CampaignMapInfo campaignMapInfo;


    public ResponseEnterCampaign(short error, int level, CampaignMapInfo campaignMapInfo, MapInfo mapInfo) {
        super(CmdDefine.ENTER_CAMPAIGN, error);
        this.level = level;
        this.campaignMapInfo = campaignMapInfo;
        this.mapInfo = mapInfo;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(level);

        if (campaignMapInfo == null || mapInfo == null) {
            bf.putInt(0);
            bf.putInt(0);
            return packBuffer(bf);
        }

        int[] idList = campaignMapInfo.getIdList();
        bf.putInt(idList.length);
        for (int id : idList) {
            MapObject mapObjectInfo = campaignMapInfo.getMapObjectInfo(id);
            Point mapObjectPos = campaignMapInfo.getMapObjectPos(id);

            bf.putInt(id);
            putStr(bf, mapObjectInfo.getType());
            bf.putInt(mapObjectInfo.getLevel());

            bf.putInt(mapObjectPos.x
                    * (ServerConstant.MAP_Y + ServerConstant.BORDER_Y)
                    + mapObjectPos.y);

            String mapObjectName = DataLoader.getInstance()
                    .getMapObjectName(mapObjectInfo.getType());

            switch (mapObjectName)
            {
                case "Storage":
                    String harvestType = DataLoader.getInstance()
                            .getMap(mapObjectInfo.getType(), String.valueOf(mapObjectInfo.getLevel())).get("type");
                    if (harvestType.equals("gold")) {
                        bf.putInt(((Storage) mapObjectInfo).getQuantity());
                        bf.putInt(0);
                    }
                    else {
                        bf.putInt(0);
                        bf.putInt(((Storage) mapObjectInfo).getQuantity());
                    }
                    break;
                case "TownHall":
                    bf.putInt(((TownHall) mapObjectInfo).getGold());
                    bf.putInt(((TownHall) mapObjectInfo).getElixir());
                    break;
                default:
                    bf.putInt(0);
                    bf.putInt(0);
                    break;
            }
        }

        TroopInfo troopInfo = new TroopInfo(mapInfo);
        int[] totalTroop = troopInfo.getTotalTroop();
        int numberOfTroop = 0;
        for (int troop : totalTroop)
            if (troop != 0)
                numberOfTroop++;
        bf.putInt(numberOfTroop);
        for (int i=0; i<totalTroop.length; i++)
            if (totalTroop[i] != 0) {
                String troopType = DataLoader.getInstance().getTroopTypeFromEnum(i);
                putStr(bf, troopType);
                bf.putInt(totalTroop[i]);
            }

        return packBuffer(bf);
    }

}
