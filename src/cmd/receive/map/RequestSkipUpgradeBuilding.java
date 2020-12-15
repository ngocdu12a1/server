package cmd.receive.map;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestSkipUpgradeBuilding extends BaseCmd {
    public int id;
    public long timestamp;

    public RequestSkipUpgradeBuilding(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            id = readInt(bf);
            timestamp = Long.parseLong(readString(bf));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
