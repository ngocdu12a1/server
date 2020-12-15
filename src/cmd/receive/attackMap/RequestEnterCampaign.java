package cmd.receive.attackMap;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestEnterCampaign extends BaseCmd {
    public int level;

    public RequestEnterCampaign(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            level = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
