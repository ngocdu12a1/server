package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseFinishDeleteObstacle extends BaseMsg {
    private int id;
    public ResponseFinishDeleteObstacle(short error, int id) {
        super(CmdDefine.FINISH_DELETE_OBSTACLE, error);
        this.id = id;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);

        return packBuffer(bf);
    }

}
