package cmd.send.attackMap;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import config.DataLoader;
import model.PlayerInfo;
import util.server.ServerConstant;

import java.nio.ByteBuffer;

public class ResponseGetCampaignInfo extends BaseMsg {
    private int numberOfCampaign;
    private PlayerInfo playerInfo;
    private int[][] campaignResource = new int[ServerConstant.MAX_CAMPAIGN+1][ServerConstant.RESOURCE.values().length];
    private int[] campaignStar = new int[ServerConstant.MAX_CAMPAIGN+1];


    public ResponseGetCampaignInfo(short error, PlayerInfo playerInfo) {
        super(CmdDefine.GET_CAMPAIGN_INFO, error);
        this.playerInfo = playerInfo;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        if (playerInfo == null)
            numberOfCampaign = 0;
        else
            numberOfCampaign = ServerConstant.MAX_CAMPAIGN;

        int currentCampaign = playerInfo.getCurrentCampaign();

        campaignResource = playerInfo.getCampaignResource();
        campaignStar = playerInfo.getCampaignStar();

        bf.putInt(numberOfCampaign);
        bf.putInt(currentCampaign);

        for (int level=1; level<=numberOfCampaign; level++) {
            JsonObject json = DataLoader.getInstance().getCampaignMap(level);
            JsonObject jObjRes = json.get("resourse").getAsJsonObject();

            int gold = jObjRes.get("gold").getAsInt()
                    - campaignResource[level][ServerConstant.RESOURCE.GOLD.getValue()];
            int elixir = jObjRes.get("elixir").getAsInt()
                    - campaignResource[level][ServerConstant.RESOURCE.ELIXIR.getValue()];
            int star = campaignStar[level];

            bf.putInt(gold);
            bf.putInt(elixir);
            bf.putInt(star);
        }

        return packBuffer(bf);
    }

}
