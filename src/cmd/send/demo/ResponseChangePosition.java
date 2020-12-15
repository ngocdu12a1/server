package cmd.send.demo;

import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.common.business.Debug;
import cmd.CmdDefine;
import cmd.receive.demo.ChangePosition;
import org.junit.Assert;
import util.server.ServerConstant;

import java.awt.*;
import java.nio.ByteBuffer;

public class ResponseChangePosition extends BaseMsg {
    private Point pos;
    public ResponseChangePosition(short error, Point pos) {
        super(CmdDefine.MOVE, error);
        this.pos = pos;
    }

    @Override
    public byte[] createData() {
        ByteBuffer bf = makeBuffer();

        try{
            Assert.assertTrue("unit_test_error | check return error ", this.Error == 0);
            Debug.warn("unit_test | #6 | passed | check return error");

        }catch(AssertionError e){
            Debug.warn("unit_test | #6 | failed | check return error = " + this.Error);
        }


        try{
            bf.putInt(pos.x);
            bf.putInt(pos.y);
            Assert.assertTrue("unit_test_error | check put value to client ", bf.position()==9);
            Debug.warn("unit_test | #7 | passed | check put value to client");

        }catch(AssertionError e){
            Debug.warn("unit_test | #7 | failed | check put value to client, please put 2 int param");
        }

        return packBuffer(bf);
    }

}
