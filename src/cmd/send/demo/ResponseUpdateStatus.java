package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.common.business.Debug;
import cmd.CmdDefine;
import org.junit.Assert;
import util.server.ServerConstant;

import java.awt.*;
import java.nio.ByteBuffer;

public class ResponseUpdateStatus extends BaseMsg {

    private Point position;
    public boolean[][] visitedMap;

    public ResponseUpdateStatus(short error, Point position, boolean[][] visitedMap) {
        super(CmdDefine.UPDATE_STATUS, error);
        this.position = position;
        this.visitedMap = visitedMap;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(position.x);
        bf.putInt(position.y);

        bf.putInt(visitedMap.length);
        bf.putInt(visitedMap[0].length);

        for (int i=0; i<visitedMap.length; i++){
            for (int j=0; j<visitedMap[i].length; j++)
                putBoolean(bf, visitedMap[i][j]);
        }

        return packBuffer(bf);
    }
}
