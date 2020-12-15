package cmd.receive.attackMap;

import bitzero.server.extensions.data.BaseCmd;
import bitzero.server.extensions.data.DataCmd;
import config.DataLoader;

import java.nio.ByteBuffer;

public class RequestStartCampaign extends BaseCmd {
    public int numberOfTroop;
    public int[] selectedTroop = new int[DataLoader.getInstance().getMaxTroop()];

    public RequestStartCampaign(DataCmd data) {
        super(data);
        // TODO Auto-generated constructor stub
        unpackData();
    }

    @Override
    public void unpackData() {
        ByteBuffer bf = makeBuffer();
        try {
            numberOfTroop = readInt(bf);
            for (int i=0; i<numberOfTroop; i++) {
                String troopType = readString(bf);
                int quantity = readInt(bf);
                int troopEnum = DataLoader.getInstance().getEnumFromTroopType(troopType);
                selectedTroop[troopEnum] = quantity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
