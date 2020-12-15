package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseDeleteObstacle extends BaseMsg {
    private int id;
    private long startActionTimestamp;
    public ResponseDeleteObstacle(short error, int id, long startActionTimestamp) {
        super(CmdDefine.DELETE_OBSTACLE, error);
        this.id = id;
        this.startActionTimestamp = startActionTimestamp;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);
        bf.putLong(startActionTimestamp);

        return packBuffer(bf);
    }

}
