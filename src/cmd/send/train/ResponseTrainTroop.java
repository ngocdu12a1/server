package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseTrainTroop extends BaseMsg {
    private String type;
    private int id;

    public ResponseTrainTroop(short error, String type, int id) {
        super(CmdDefine.TRAIN_TROOP, error);
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
