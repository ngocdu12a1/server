package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseBuildBuilding extends BaseMsg {
    private int id;
    private String type;
    private int x;
    private int y;
    private long startActionTimestamp;

    public ResponseBuildBuilding(short error, int id, String type, int x, int y) {
        super(CmdDefine.BUILD_BUILDING, error);
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.startActionTimestamp = System.currentTimeMillis()/1000;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(id);
        putStr(bf, type);
        bf.putInt(x);
        bf.putInt(y);
        bf.putLong(startActionTimestamp);

        return packBuffer(bf);
    }

}
