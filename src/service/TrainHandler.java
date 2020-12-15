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
import cmd.receive.train.*;
import cmd.send.demo.*;
import cmd.send.train.*;
import config.DataLoader;
import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import model.PlayerInfo;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import model.mapInfo.Building.ArmyBuilding.Barrack;
import model.trainInfo.TrainInfo;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.server.ServerConstant;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TrainHandler extends BaseClientRequestHandler implements IServerEventHandler {

    public static short TRAIN_MULTI_IDS = 4000;

    /**
     * log4j level
     * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
     */

    private final Logger logger = LoggerFactory.getLogger("TrainHandler");

    public TrainHandler() {
        super();
    }

    /**
     *  this method automatically loaded when run the program
     *  register new event, so the core will dispatch event type to this class
     */
    public void init() {

        getParentExtension().addEventListener(DemoEventType.LOGIN_SUCCESS, this);
    }

    /**
     * events will be dispatch here
     */
    public void handleServerEvent(IBZEvent ibzevent) {
        if (ibzevent.getType() == DemoEventType.LOGIN_SUCCESS) {
            this.processUpdateAll((User)ibzevent.getParameter(DemoEventParam.USER));
        }
    }

    private void dispatchEventUpdateResource(User user) {
        Map evtParams = new HashMap();
        evtParams.put(DemoEventParam.USER, user);
        ExtensionUtility.dispatchEvent(new BZEvent(DemoEventType.UPDATE_RESOURCE, evtParams));
    }

    @Override
    /**
     * this method handle all client requests with cmdId in range [1000:2999]
     *
     */
    public void handleClientRequest(User user, DataCmd dataCmd) {

        try {
            switch (dataCmd.getId()) {
                case CmdDefine.GET_TRAIN_INFO:
                    processGetTrainInfo(user);
                    break;
                case CmdDefine.TRAIN_TROOP:
                    RequestTrainTroop requestTrainTroop = new RequestTrainTroop(dataCmd);
                    processTrainTroop(user, requestTrainTroop);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.SKIP_TROOP:
                    RequestSkipTroop requestSkipTroop = new RequestSkipTroop(dataCmd);
                    processSkipTroop(user, requestSkipTroop);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.CANCEL_TROOP:
                    RequestCancelTroop requestCancelTroop = new RequestCancelTroop(dataCmd);
                    processCancelTroop(user, requestCancelTroop);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.TRAIN_TROOP_FINISH:
                    RequestTrainTroopFinish requestTrainTroopFinish = new RequestTrainTroopFinish(dataCmd);
                    processTrainTroopFinish(user, requestTrainTroopFinish);
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

    //Check resource and subtract the player resource but NOT save
    private boolean checkoutResource(String type, long level, PlayerInfo pInfo, MapInfo mInfo, int delta) {
        int[] currentResource = mInfo.getResource();
        currentResource[ServerConstant.RESOURCE.COIN.getValue()] = pInfo.getCoin();

        int[] requiredResource = new int[ServerConstant.RESOURCE.values().length];
        if (level > 0)
            requiredResource = DataLoader.getInstance().getRequiredResource(type, (int) level);
        else
        {
            // 30s = 1 coin
           requiredResource[ServerConstant.RESOURCE.COIN.getValue()] = (int) Math.ceil((double)-level/30);
        }

        if (delta > 0) {
            for (int i=0; i<ServerConstant.RESOURCE.values().length; i++) {
                if (currentResource[i] < requiredResource[i]) {
                    return false;
                }
            }
        }

        for (int i=0; i<ServerConstant.RESOURCE.values().length; i++) {
            requiredResource[i] = - requiredResource[i] / delta;
        }

        mInfo.updateResource(requiredResource);
        pInfo.setCoin(pInfo.getCoin() + requiredResource[ServerConstant.RESOURCE.COIN.getValue()]);

        return true;
    }

    private void processUpdateAll(User user) {
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                return;
            }
            TrainInfo trainInfo = new TrainInfo(mInfo);
            trainInfo.updateAll();

            mInfo.saveModel(user.getId());
        } catch (Exception e) {
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }
    
    private void processGetTrainInfo(User user) {
        String json = null;
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseGetTrainInfo(HandlerError.Error.MAPINFO_NULL.getValue(), json), user);
                return;
            }

            TrainInfo trainInfo = new TrainInfo(mInfo);
            json = trainInfo.getTrainInfo();

            send(new ResponseGetTrainInfo(HandlerError.Error.SUCCESS.getValue(), json), user);

        } catch (Exception e) {
            send(new ResponseGetTrainInfo(HandlerError.Error.EXCEPTION.getValue(), json), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processTrainTroop(User user, RequestTrainTroop request) {
        String type = request.type;
        int id = request.id;
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseTrainTroop(HandlerError.Error.MAPINFO_NULL.getValue(), type, id), user);
                return;
            }
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (pInfo==null){
                send(new ResponseTrainTroop(HandlerError.Error.PLAYERINFO_NULL.getValue(), type, id), user);
                return;
            }

            if (!checkoutResource(type, 1, pInfo, mInfo, 1)) {
                send(new ResponseTrainTroop(HandlerError.Error.ERROR.getValue(), type, id), user);
                return;
            }

            TrainInfo trainInfo = new TrainInfo(mInfo);
            id = trainInfo.trainTroop(type, id);

            if (id != -1) {
                mInfo.saveModel(user.getId());
                pInfo.saveModel(user.getId());
                send(new ResponseTrainTroop(HandlerError.Error.SUCCESS.getValue(), type, id), user);
            }
            else {
                send(new ResponseTrainTroop(HandlerError.Error.ERROR.getValue(), type, id), user);
            }
            

        } catch (Exception e) {
            send(new ResponseTrainTroop(HandlerError.Error.EXCEPTION.getValue(), type, id), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processCancelTroop(User user, RequestCancelTroop request) {
        String type = request.type;
        int id = request.id;
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseCancelTroop(HandlerError.Error.MAPINFO_NULL.getValue(), type, id), user);
                return;
            }
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (pInfo==null){
                send(new ResponseCancelTroop(HandlerError.Error.PLAYERINFO_NULL.getValue(), type, id), user);
                return;
            }

            checkoutResource(type, 1, pInfo, mInfo, -1);

            TrainInfo trainInfo = new TrainInfo(mInfo);
            id = trainInfo.cancelTroop(type, id);

            if (id != -1) {
                mInfo.saveModel(user.getId());
                pInfo.saveModel(user.getId());
                send(new ResponseCancelTroop(HandlerError.Error.SUCCESS.getValue(), type, id), user);
            }
            else {
                send(new ResponseCancelTroop(HandlerError.Error.ERROR.getValue(), type, id), user);
            }
            

        } catch (Exception e) {
            send(new ResponseCancelTroop(HandlerError.Error.EXCEPTION.getValue(), type, id), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processTrainTroopFinish(User user, RequestTrainTroopFinish request) {
        int barrackID = request.barrackID;
        int armyCampID = request.armyCampID;
        long timestamp = request.timestamp;
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseTrainTroopFinish(HandlerError.Error.MAPINFO_NULL.getValue(), barrackID, armyCampID, timestamp), user);
                return;
            }

            TrainInfo trainInfo = new TrainInfo(mInfo);
            timestamp = trainInfo.finishTrainTroop(barrackID, armyCampID, timestamp);

            if (timestamp != -1) {
                mInfo.saveModel(user.getId());
                send(new ResponseTrainTroopFinish(HandlerError.Error.SUCCESS.getValue(), barrackID, armyCampID, timestamp), user);
            }
            else {
                send(new ResponseTrainTroopFinish(HandlerError.Error.ERROR.getValue(), barrackID, armyCampID, timestamp), user);
            }
            

        } catch (Exception e) {
            send(new ResponseTrainTroopFinish(HandlerError.Error.EXCEPTION.getValue(), barrackID, armyCampID, timestamp), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processSkipTroop(User user, RequestSkipTroop request) {
        int barrackID = request.barrackID;
        int armyCampID = request.armyCampID;
        long timestamp = request.timestamp;
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseSkipTroop(HandlerError.Error.MAPINFO_NULL.getValue(), barrackID, armyCampID, timestamp), user);
                return;
            }
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (pInfo==null){
                send(new ResponseSkipTroop(HandlerError.Error.PLAYERINFO_NULL.getValue(), barrackID, armyCampID, timestamp), user);
                return;
            }

            long currentTimestamp = System.currentTimeMillis() / 1000;

            if (timestamp < currentTimestamp-10 || timestamp > currentTimestamp+10) {
                send(new ResponseSkipTroop(HandlerError.Error.ERROR.getValue(), barrackID, armyCampID, timestamp), user);
                return;
            }

            Barrack barrack = (Barrack) mInfo.getMapObjectInfo(barrackID);
            long startTraintimestamp = barrack.getTrainTimestamp();

            String troopType = barrack.getTrainQueue().peek();
            long timelength = Integer.parseInt(
                DataLoader.getInstance().getMap(troopType, "base").get("trainingTime")
            );

            long remainTime = timelength - (timestamp - startTraintimestamp);

            if(!checkoutResource(troopType, -remainTime, pInfo, mInfo, 1)) {
                send(new ResponseSkipTroop(HandlerError.Error.ERROR.getValue(), barrackID, armyCampID, timestamp), user);
                return;
            }

            TrainInfo trainInfo = new TrainInfo(mInfo);
            timestamp = trainInfo.skipTroop(barrackID, armyCampID, timestamp);

            if (timestamp != -1) {
                mInfo.saveModel(user.getId());
                send(new ResponseSkipTroop(HandlerError.Error.SUCCESS.getValue(), barrackID, armyCampID, timestamp), user);
            }
            else {
                send(new ResponseSkipTroop(HandlerError.Error.ERROR.getValue(), barrackID, armyCampID, timestamp), user);
            }


        } catch (Exception e) {
            send(new ResponseSkipTroop(HandlerError.Error.EXCEPTION.getValue(), barrackID, armyCampID, timestamp), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }
}
