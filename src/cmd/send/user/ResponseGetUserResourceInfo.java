package cmd.send.user;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseGetUserResourceInfo extends BaseMsg {
    private int gold;
    private int elixir;
    private int darkElixir;
    private int coin;
    public ResponseGetUserResourceInfo(short error, int gold, int elixir, int darkElixir, int coin) {
        super(CmdDefine.GET_USER_RESOURCE_INFO, error);
        this.gold = gold;
        this.elixir = elixir;
        this.darkElixir = darkElixir;
        this.coin = coin;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(gold);
        bf.putInt(elixir);
        bf.putInt(darkElixir);
        bf.putInt(coin);

        return packBuffer(bf);
    }

}
