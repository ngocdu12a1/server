package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseCancelBuildBuilding extends BaseMsg {
    private int id;
    public ResponseCancelBuildBuilding(short error, int id) {
        super(CmdDefine.CANCEL_BUILD_BUILDING, error);
        this.id = id;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);

        return packBuffer(bf);
    }

}
