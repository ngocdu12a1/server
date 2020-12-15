package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseSkipTroop extends BaseMsg {
    private int barrackID;
    private int armyCampID;
    private long timestamp;

    public ResponseSkipTroop(short error, int barrackID, int armyCampID, long timestamp) {
        super(CmdDefine.SKIP_TROOP, error);
        this.barrackID = barrackID;
        this.armyCampID = armyCampID;
        this.timestamp = timestamp;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(barrackID);
        bf.putInt(armyCampID);
        bf.putLong(timestamp);

        return packBuffer(bf);
    }

}
