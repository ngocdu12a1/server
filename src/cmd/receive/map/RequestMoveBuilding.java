package cmd.receive.map;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestMoveBuilding extends BaseCmd {
    public int id;
    public int xOld;
    public int yOld;
    public int xNew;
    public int yNew;

    public RequestMoveBuilding(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            id = readInt(bf);
            xOld = readInt(bf);
            yOld = readInt(bf);
            xNew = readInt(bf);
            yNew = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
