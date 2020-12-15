package cmd.receive.train;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestCancelTroop extends BaseCmd {
    public String type;
    public int id;

    public RequestCancelTroop(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            type = readString(bf);
            id = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
