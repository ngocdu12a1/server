package service;

import bitzero.server.core.BZEvent;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.IServerEventHandler;
import bitzero.server.extensions.data.DataCmd;
import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import cmd.CmdDefine;
import cmd.receive.demo.ChangePosition;
import cmd.receive.demo.RequestSetName;
import cmd.send.demo.*;
import cmd.send.troop.ResponseGetTroop;
import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import model.PlayerInfo;
import model.mapInfo.MapInfo;
import model.troopInfo.TroopInfo;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.server.ServerConstant;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TroopHandler extends BaseClientRequestHandler implements IServerEventHandler {

    public static short TROOP_MULTI_IDS = 3000;

    /**
     * log4j level
     * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
     */

    private final Logger logger = LoggerFactory.getLogger("TroopHandler");

    public TroopHandler() {
        super();
    }

    /**
     *  this method automatically loaded when run the program
     *  register new event, so the core will dispatch event type to this class
     */
    public void init() {

//        getParentExtension().addEventListener(DemoEventType.LOGIN_SUCCESS, this);
    }

    /**
     * events will be dispatch here
     */
    public void handleServerEvent(IBZEvent ibzevent) {        
        //        if (ibzevent.getType() == DemoEventType.LOGIN_SUCCESS) {
        //            this.processUserLoginSuccess((User)ibzevent.getParameter(DemoEventParam.USER), (String)ibzevent.getParameter(DemoEventParam.NAME));
        //        }
            }

    @Override
    /**
     * this method handle all client requests with cmdId in range [1000:2999]
     *
     */
    public void handleClientRequest(User user, DataCmd dataCmd) {

        try {
            switch (dataCmd.getId()) {
                case CmdDefine.GET_TROOP:
                    processGetTroop(user);
                    break;
                default:
                    Debug.warn("Invalid command");
                    break;
            }

        } catch (Exception e) {
            logger.warn("DEMO HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

    }
    
    private void processGetTroop(User user) {
        String json = null;
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseGetTroop(HandlerError.Error.MAPINFO_NULL.getValue(), json), user);
                return;
            }

            TroopInfo troopInfo = new TroopInfo(mInfo);
            json = troopInfo.getTroop();

            send(new ResponseGetTroop(HandlerError.Error.SUCCESS.getValue(), json), user);

        } catch (Exception e) {
            send(new ResponseGetTroop(HandlerError.Error.EXCEPTION.getValue(), json), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }
}
