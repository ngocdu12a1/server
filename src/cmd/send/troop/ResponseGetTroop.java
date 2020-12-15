package cmd.send.troop;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseGetTroop extends BaseMsg {
    private String json;

    public ResponseGetTroop(short error, String json) {
        super(CmdDefine.GET_TROOP, error);
        this.json = json;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        putStr(bf, json);

        return packBuffer(bf);
    }

}
