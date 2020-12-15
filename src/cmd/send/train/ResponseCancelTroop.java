package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseCancelTroop extends BaseMsg {
    private String type;
    private int id;

    public ResponseCancelTroop(short error, String type, int id) {
        super(CmdDefine.CANCEL_TROOP, error);
        this.type = type;
        this.id = id;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        putStr(bf, type);
        bf.putInt(id);

        return packBuffer(bf);
    }

}
