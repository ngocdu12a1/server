package cmd.send.user;

import bitzero.server.extensions.data.BaseMsg;
import cmd.CmdDefine;

import java.nio.ByteBuffer;

/**
 * Created by hieupt on 11/8/18.
 * all packet send to client will extends BaseMsg, override createData() function
 */
public class ResponseGetUsername extends BaseMsg {
    private String name;
    public ResponseGetUsername(short error, String name) {
        super(CmdDefine.GET_USERNAME, error);
        this.name = name;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();
        putStr(bf, name);
        /**
         * or can put other types with built-in function
         */
        return packBuffer(bf);
    }

    public String getName(){
        return name;
    }
}
