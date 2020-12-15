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
import cmd.receive.demo.RequestMove;

import cmd.receive.demo.RequestSetName;
import cmd.send.demo.*;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import model.PlayerInfo;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.server.ServerConstant;

public class DemoHandler extends BaseClientRequestHandler implements IServerEventHandler {
    
    public static short DEMO_MULTI_IDS = 6000;

    /**
     * log4j level
     * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
     */

    private final Logger logger = LoggerFactory.getLogger("DemoHandler");
    
    public DemoHandler() {
        super();
    }

    /**
     *  this method automatically loaded when run the program
     *  register new event, so the core will dispatch event type to this class
     */
    public void init() {
        getParentExtension().addEventListener(DemoEventType.LOGIN_SUCCESS, this);
    }

    @Override
    /**
     * this method handle all client requests with cmdId in range [1000:2999]
     *
     */
    public void handleClientRequest(User user, DataCmd dataCmd) {

        try {
            switch (dataCmd.getId()) {
                // get username
                case CmdDefine.GET_NAME:
                    processGetName(user);
                    break;
                // set username
                case CmdDefine.SET_NAME:
                    RequestSetName set = new RequestSetName(dataCmd);
                    processSetName(set, user);
                    break;
                case CmdDefine.MOVE:
                    Debug.warn("unit_test | #3 | passed | cmdId = " + CmdDefine.MOVE);
                    ChangePosition pos = new ChangePosition(dataCmd);
                    try{

                        Assert.assertTrue("unit_test_error | check params value ", pos.x >= 0 && pos.x <= ServerConstant.MAP_X && pos.y >= 0 && pos.y <= ServerConstant.MAP_Y && (pos.x + pos.y > 0));
                        Debug.warn("unit_test | #4 | passed | check params value " + CmdDefine.MOVE);

                        processChangePosition(user, pos);
                    }catch(AssertionError e){
                        Debug.warn("unit_test | #4 | failed | check params value, x = " + pos.x + " y = " + pos.y);
                    }


                    break;
                case CmdDefine.RESET_MAP:
                    processResetMap(user);
                    break;
                default:
                    Debug.warn("unit_test | #3 | failed | cmdId # " + CmdDefine.MOVE + " , check sendMove() function in client");
            }

        } catch (Exception e) {
            logger.warn("DEMO HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

    }

    /**
     * events will be dispatch here
     */
    public void handleServerEvent(IBZEvent ibzevent) {        
        if (ibzevent.getType() == DemoEventType.LOGIN_SUCCESS) {
            this.processUserLoginSuccess((User)ibzevent.getParameter(DemoEventParam.USER), (String)ibzevent.getParameter(DemoEventParam.NAME));
        }
    }

    private void processChangePosition(User user, ChangePosition newPos){
        try {
            PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            Point pos = new Point(newPos.x, newPos.y);
            if (userInfo==null){
                send(new ResponseChangePosition(DemoError.PLAYERINFO_NULL.getValue(), pos), user);
                return;
            }

            if (!userInfo.isValidPos(newPos.x, newPos.y)){
                send(new ResponseChangePosition(DemoError.INVALID_PARAM.getValue(), pos), user);
                return;
            }

            if (!userInfo.visit(newPos.x, newPos.y)){
                send(new ResponseChangePosition(DemoError.VISITED.getValue(), pos), user);
                return;
            }



            PlayerInfo pInfo = (PlayerInfo) PlayerInfo.getModel(user.getId(), PlayerInfo.class);
            try{
                pInfo.visit(pos.x, pos.y);
                Assert.assertTrue("unit_test_error | check data change ", pInfo.countNumberOfVisited() > 1);
                Debug.warn("unit_test | #5 | passed | check data change");
            }catch(AssertionError e){
                Debug.warn("unit_test | #5 | failed | check data change, number of change : " + pInfo.countNumberOfVisited());
            }

            send(new ResponseChangePosition(DemoError.SUCCESS.getValue(), pos), user);

            // send current position to user
            //processSendUpdateStatusToUser(user);


        } catch (Exception e) {
            send(new ResponseChangePosition(DemoError.EXCEPTION.getValue(), new Point(newPos.x, newPos.y)), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processResetMap(User user){
        try {
            PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);

            if (userInfo==null){
                send(new ResponseResetMap(DemoError.PLAYERINFO_NULL.getValue()), user);
                return;
            }

            userInfo.resetMap();
            // save change to db
            userInfo.saveModel(user.getId());

            send(new ResponseResetMap(DemoError.SUCCESS.getValue()), user);

        } catch (Exception e) {
            Debug.warn(ExceptionUtils.getStackTrace(e));
            send(new ResponseResetMap(DemoError.EXCEPTION.getValue()), user);
        }
    }
    
    private void processUserLoginSuccess(User user, String name){
        /**
         * process event
         */
        processSendUpdateStatusToUser(user);

    }

    private void processSendUpdateStatusToUser(User user){
        logger.warn("processUserLoginSuccess, name = " + user.getName());
        // send update status to user
//        PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
//        if (userInfo==null){
//            logger.info("PlayerInfo null");
//            send(new ResponseUpdateStatus(DemoError.PLAYERINFO_NULL.getValue(), userInfo.position, userInfo.visitedMap), user);
//        }
//
//        send(new ResponseUpdateStatus(DemoError.SUCCESS.getValue(), userInfo.position, userInfo.visitedMap), user);
    }

    private void processGetName(User user){
        try{
            PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (userInfo==null){
                logger.info("PlayerInfo null");
                send(new ResponseGetName(DemoError.PLAYERINFO_NULL.getValue(), ""), user);
            }

            logger.info("get name = " + userInfo.getName());
            send(new ResponseGetName(DemoError.SUCCESS.getValue(), userInfo.getName()), user);
        }catch(Exception e){
            logger.info("processGetName exception");
            send(new ResponseGetName(DemoError.EXCEPTION.getValue(), ""), user);
        }
    }

    private void processSetName(RequestSetName requestSet, User user){
        try{
            PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (userInfo==null)
                send(new ResponseSetName(DemoError.PLAYERINFO_NULL.getValue(), ""), user);
            String name = userInfo.setName(requestSet.getName());
            send(new ResponseSetName(DemoError.SUCCESS.getValue(), name), user);
            logger.info("set new name = " + name);
            /**
             * dispatch event for another handler
             */
            Map evtParams = new HashMap();
            evtParams.put(DemoEventParam.USER, user);
            evtParams.put(DemoEventParam.NAME, requestSet.getName());
            ExtensionUtility.dispatchEvent(new BZEvent(DemoEventType.LOGIN_SUCCESS, evtParams));
            ExtensionUtility.dispatchEvent(new BZEvent(DemoEventType.CHANGE_NAME, evtParams));
        }catch(Exception e){
            send(new ResponseSetName(DemoError.EXCEPTION.getValue(), ""), user);
        }
    }

    public enum DemoError{
        SUCCESS((short)0),
        ERROR((short)1),
        PLAYERINFO_NULL((short)2),
        EXCEPTION((short)3),
        INVALID_PARAM((short)4),
        VISITED((short)5),;

        
        private final short value;
        private DemoError(short value){
            this.value = value;
        }
        
        public short getValue(){
            return this.value;
        }
    }
}
