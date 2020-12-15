package cmd.send.train;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseGetTrainInfo extends BaseMsg {
    private String json;

    public ResponseGetTrainInfo(short error, String json) {
        super(CmdDefine.GET_TRAIN_INFO, error);
        this.json = json;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        putStr(bf, json);

        return packBuffer(bf);
    }

}
