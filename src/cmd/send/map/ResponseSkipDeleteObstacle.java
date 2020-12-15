package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseSkipDeleteObstacle extends BaseMsg {
    private int id;
    private long timestamp;
    public ResponseSkipDeleteObstacle(short error, int id, long timestamp) {
        super(CmdDefine.SKIP_DELETE_OBSTACLE, error);
        this.id = id;
        this.timestamp = timestamp;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);
        bf.putLong(timestamp);

        return packBuffer(bf);
    }

}
