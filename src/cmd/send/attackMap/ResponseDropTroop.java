package cmd.send.attackMap;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;
import config.DataLoader;

import java.nio.ByteBuffer;

public class ResponseDropTroop extends BaseMsg {
    private String troopType;
    private int id;
    private int x;
    private int y;
    private int tick;


    public ResponseDropTroop(short error, String troopType, int id, int x, int y, int tick) {
        super(CmdDefine.DROP_TROOP, error);
        this.troopType = troopType;
        this.id = id;
        this.x = x;
        this.y = y;
        this.tick = tick;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        putStr(bf, troopType);
        bf.putInt(id);
        bf.putInt(x);
        bf.putInt(y);
        bf.putInt(tick);

        return packBuffer(bf);
    }

}
