package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseFinishUpgradeBuilding extends BaseMsg {
    private int id;
    public ResponseFinishUpgradeBuilding(short error, int id) {
        super(CmdDefine.FINISH_UPGRADE_BUILDING, error);
        this.id = id;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);

        return packBuffer(bf);
    }

}
