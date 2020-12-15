package cmd.receive.attackMap;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestEndCampaign extends BaseCmd {
    public int tick;

    public RequestEndCampaign(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            tick = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
