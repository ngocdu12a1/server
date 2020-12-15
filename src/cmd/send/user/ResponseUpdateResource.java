package cmd.send.user;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

public class ResponseUpdateResource extends BaseMsg {
    public ResponseUpdateResource(short error) {
        super(CmdDefine.UPDATE_RESOURCE, error);
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        return packBuffer(bf);
    }

}
