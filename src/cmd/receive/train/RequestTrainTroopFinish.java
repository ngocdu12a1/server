package cmd.receive.train;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestTrainTroopFinish extends BaseCmd {
    public int barrackID;
    public int armyCampID;
    public long timestamp;

    public RequestTrainTroopFinish(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            barrackID = readInt(bf);
            armyCampID = readInt(bf);
            timestamp = Long.parseLong(readString(bf));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
