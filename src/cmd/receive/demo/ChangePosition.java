package cmd.receive.demo;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.common.business.CommonHandle;

import java.nio.ByteBuffer;

public class ChangePosition extends BaseCmd {
    public int x;
    public int y;
    public ChangePosition(DataCmd dataCmd) {
        super(dataCmd);
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            x = readInt(bf);
            y = readInt(bf);
        } catch (Exception e) {
            x = y = 0;
        }
    }
}
