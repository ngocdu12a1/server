package cmd.send.user;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseGetTimestamp extends BaseMsg {
    long timestamp;
    public ResponseGetTimestamp(short error, long timestamp) {
        super(CmdDefine.GET_TIMESTAMP, error);
        this.timestamp = timestamp;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putLong(timestamp);

        return packBuffer(bf);
    }

}
