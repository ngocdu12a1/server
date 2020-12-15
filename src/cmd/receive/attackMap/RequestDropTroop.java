package cmd.receive.attackMap;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestDropTroop extends BaseCmd {
    public String troopType;
    public int id;
    public int x;
    public int y;
    public int tick;

    public RequestDropTroop(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            troopType = readString(bf);
            id = readInt(bf);
            x = readInt(bf);
            y = readInt(bf);
            tick = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
