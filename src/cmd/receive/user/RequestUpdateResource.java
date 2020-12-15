package cmd.receive.user;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;

import java.nio.ByteBuffer;

public class RequestUpdateResource extends BaseCmd {
    public int gold;
    public int elixir;
    public int darkElixir;
    public int coin;

    public RequestUpdateResource(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            gold = readInt(bf);
            elixir = readInt(bf);
            darkElixir = readInt(bf);
            coin = readInt(bf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
