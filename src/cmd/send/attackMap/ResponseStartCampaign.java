package cmd.send.attackMap;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import com.google.gson.JsonObject;
import config.DataLoader;
import model.PlayerInfo;
import util.server.ServerConstant;

import java.nio.ByteBuffer;

public class ResponseStartCampaign extends BaseMsg {
    private int[] selectedTroop;


    public ResponseStartCampaign(short error, int[] selectedTroop) {
        super(CmdDefine.START_CAMPAIGN, error);
        this.selectedTroop = selectedTroop;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        int numberOfTroop = 0;
        for (int troop : selectedTroop)
            if (troop != 0)
                numberOfTroop++;
        bf.putInt(numberOfTroop);
        for (int i=0; i<selectedTroop.length; i++)
            if (selectedTroop[i] != 0) {
                String troopType = DataLoader.getInstance().getTroopTypeFromEnum(i);
                putStr(bf, troopType);
                bf.putInt(selectedTroop[i]);
            }

        return packBuffer(bf);
    }

}
