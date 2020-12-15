package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.common.business.Debug;
import cmd.CmdDefine;
import org.junit.Assert;
import util.server.ServerConstant;

import java.awt.*;
import java.nio.ByteBuffer;

public class ResponseGetMapInfo extends BaseMsg {
    private int[][] grid;
    public ResponseGetMapInfo(short error, int[][] grid) {
        super(CmdDefine.GET_MAP_INFO, error);
        this.grid = grid;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(grid.length);
        bf.putInt(grid[0].length);

        for (int i=0; i<grid.length; i++){
            for (int j=0; j<grid[i].length; j++)
                bf.putInt(grid[i][j]);
        }

        return packBuffer(bf);
    }

}
