package cmd.send.attackMap;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseEndCampaign extends BaseMsg {
    private int gold;
    private int elixir;
    private int star;


    public ResponseEndCampaign(short error, int gold, int elixir, int star) {
        super(CmdDefine.END_CAMPAIGN, error);
        this.gold = gold;
        this.elixir = elixir;
        this.star = star;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(gold);
        bf.putInt(elixir);
        bf.putInt(star);

        return packBuffer(bf);
    }

}
