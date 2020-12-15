package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.common.business.Debug;
import cmd.CmdDefine;
import org.junit.Assert;

import java.nio.ByteBuffer;

public class ResponseResetMap extends BaseMsg {
    public ResponseResetMap(short type) {
        super(CmdDefine.RESET_MAP, type);
    }

    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        return packBuffer(bf);
    }
}
