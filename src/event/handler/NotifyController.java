package event.handler;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.exceptions.BZException;
import bitzero.server.extensions.BaseServerEventHandler;
import bitzero.util.common.business.Debug;
import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import org.junit.Assert;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NotifyController extends BaseServerEventHandler {
    @Override
    public void handleServerEvent(IBZEvent ibzEvent) throws BZException {

        try{
            Assert.assertTrue("unit_test_error | check dispatch event ", ibzEvent.getType() == DemoEventType.CHANGE_POSITION);
            Debug.warn("unit_test | #8 | passed | check dispatch event " + DemoEventType.CHANGE_POSITION);
        }catch(AssertionError e){
            Debug.warn("unit_test | #8 | failed | check dispatch event fail, event = " + ibzEvent.getType());
        }

        if (ibzEvent.getType() == DemoEventType.CHANGE_POSITION)
            processChangePosition((User) ibzEvent.getParameter(DemoEventParam.USER),
                    (Point) ibzEvent.getParameter(DemoEventParam.POSITION));
    }

    private void processChangePosition(User user, Point pos){
        List<User> allUser = BitZeroServer.getInstance().getUserManager().getAllUsers();
        for(User aUser : allUser){
            // notify user's change

        }
    }
}
