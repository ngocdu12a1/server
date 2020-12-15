package cmd.send.map;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseGetMapObjectCount extends BaseMsg {
    private int[] idList;
    private int[] offlineFinishIdList;
    public ResponseGetMapObjectCount(short error, int[] idList, int[] offlineFinishIdList) {
        super(CmdDefine.GET_MAP_OBJECT_COUNT, error);
        this.idList = idList;
        this.offlineFinishIdList = offlineFinishIdList;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        bf.putInt(idList.length);
        for (int i=0; i<idList.length; i++)
        {
            bf.putInt(idList[i]);
        }
        bf.putInt(offlineFinishIdList.length);
        for (int i=0; i<offlineFinishIdList.length; i++)
        {
            bf.putInt(offlineFinishIdList[i]);
        }

        return packBuffer(bf);
    }

}
