package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseUpgradeBuilding extends BaseMsg {
    private int id;
    private long startActionTimestamp;
    public ResponseUpgradeBuilding(short error, int id, long startActionTimestamp) {
        super(CmdDefine.UPGRADE_BUILDING, error);
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
