package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseSkipUpgradeBuilding extends BaseMsg {
    private int id;
    private long timestamp;
    public ResponseSkipUpgradeBuilding(short error, int id, long timestamp) {
        super(CmdDefine.SKIP_UPGRADE_BUILDING, error);
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
