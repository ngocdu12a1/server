package cmd.receive.map;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestBuildBuilding extends BaseCmd {
    public String type;
    public int x;
    public int y;

    public RequestBuildBuilding(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            type = readString(bf);
            x = readInt(bf);
            y = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
