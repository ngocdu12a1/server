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
import cmd.receive.map.*;
import cmd.send.map.*;
import config.DataLoader;
import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import model.PlayerInfo;
import model.mapInfo.MapInfo;
import model.mapInfo.MapObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.server.ServerConstant;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MapHandler extends BaseClientRequestHandler implements IServerEventHandler {

    public static short MAP_MULTI_IDS = 2000;

    /**
     * log4j level
     * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
     */

    private final Logger logger = LoggerFactory.getLogger("MapHandler");

    public MapHandler() {
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
            this.processGetMapInfo((User) ibzevent.getParameter(DemoEventParam.USER));
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
                case CmdDefine.GET_MAP_INFO:
                    processGetMapInfo(user);
                    break;
                case CmdDefine.GET_MAP_OBJECT_COUNT:
                    processGetMapObjectCount(user);
                    break;
                case CmdDefine.GET_MAP_OBJECT_INFO:
                    RequestGetMapObjectInfo requestGetMapObjectInfo = new RequestGetMapObjectInfo(dataCmd);
                    processGetMapObjectInfo(user, requestGetMapObjectInfo);
                    break;
                case CmdDefine.MOVE_BUILDING:
                    RequestMoveBuilding requestMoveBuilding = new RequestMoveBuilding(dataCmd);
                    processMoveBuilding(user, requestMoveBuilding);
                    break;
                case CmdDefine.BUILD_BUILDING:
                    RequestBuildBuilding requestBuildBuilding = new RequestBuildBuilding(dataCmd);
                    processBuildBuilding(user, requestBuildBuilding);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.DELETE_OBSTACLE:
                    RequestDeleteObstacle requestDeleteObstacle = new RequestDeleteObstacle(dataCmd);
                    processDeleteObstacle(user, requestDeleteObstacle);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.FINISH_DELETE_OBSTACLE:
                    RequestFinishDeleteObstacle requestFinishDeleteObstacle = new RequestFinishDeleteObstacle(dataCmd);
                    processFinishDeleteObstacle(user, requestFinishDeleteObstacle);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.SKIP_DELETE_OBSTACLE:
                    RequestSkipDeleteObstacle requestSkipDeleteObstacle = new RequestSkipDeleteObstacle(dataCmd);
                    processSkipDeleteObstacle(user, requestSkipDeleteObstacle);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.UPGRADE_BUILDING:
                    RequestUpgradeBuilding requestUpgradeBuilding = new RequestUpgradeBuilding(dataCmd);
                    processUpgradeBuilding(user, requestUpgradeBuilding);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.FINISH_UPGRADE_BUILDING:
                    RequestFinishUpgradeBuilding requestFinishUpgradeBuilding = new RequestFinishUpgradeBuilding(dataCmd);
                    processFinishUpgradeBuilding(user, requestFinishUpgradeBuilding);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.SKIP_UPGRADE_BUILDING:
                    RequestSkipUpgradeBuilding requestSkipUpgradeBuilding = new RequestSkipUpgradeBuilding(dataCmd);
                    processSkipUpgradeBuilding(user, requestSkipUpgradeBuilding);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.CANCEL_BUILD_BUILDING:
                    RequestCancelBuildBuilding requestCancelBuildBuilding = new RequestCancelBuildBuilding(dataCmd);
                    processCancelBuildBuilding(user, requestCancelBuildBuilding);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.CANCEL_UPGRADE_BUILDING:
                    RequestCancelUpgradeBuilding requestCancelUpgradeBuilding = new RequestCancelUpgradeBuilding(dataCmd);
                    processCancelUpgradeBuilding(user, requestCancelUpgradeBuilding);
                    dispatchEventUpdateResource(user);
                    break;
                case CmdDefine.GET_MAP_INFO_LIST:
                    processGetMapInfoList(user);
                    break;
                default:
                    Debug.warn("Invalid command");
            }

        } catch (Exception e) {
            logger.warn("DEMO HANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

    }

    private void processGetMapInfoList(User user) {
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseGetMapInfoList(HandlerError.Error.MAPINFO_NULL.getValue(), mInfo), user);
                return;
            }

            send(new ResponseGetMapInfoList(HandlerError.Error.SUCCESS.getValue(), mInfo), user);

        } catch (Exception e) {
            send(new ResponseGetMapInfoList(HandlerError.Error.EXCEPTION.getValue(), null), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processGetMapInfo(User user){
        int[][] grid = new int[ServerConstant.MAP_X][ServerConstant.MAP_Y];
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseGetMapInfo(HandlerError.Error.MAPINFO_NULL.getValue(), grid), user);
                return;
            }

            grid = mInfo.getMapGrid();

            send(new ResponseGetMapInfo(HandlerError.Error.SUCCESS.getValue(), grid), user);

        } catch (Exception e) {
            send(new ResponseGetMapInfo(HandlerError.Error.EXCEPTION.getValue(), grid), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processGetMapObjectCount(User user) {
        int[] idList = new int[0];
        int[] offlineFinishIdList = new int[0];
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mInfo==null){
                send(new ResponseGetMapObjectCount(HandlerError.Error.MAPINFO_NULL.getValue(), idList, offlineFinishIdList), user);
                return;
            }

//            offlineFinishIdList = mInfo.updateAllAction();
            idList = mInfo.getIdList();

            send(new ResponseGetMapObjectCount(HandlerError.Error.SUCCESS.getValue(), idList, offlineFinishIdList), user);

        } catch (Exception e) {
            send(new ResponseGetMapObjectCount(HandlerError.Error.EXCEPTION.getValue(), idList, offlineFinishIdList), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processGetMapObjectInfo(User user, RequestGetMapObjectInfo request) {
        Point mapObjectPos = new Point(0, 0);
        MapObject mapObjectInfo = new MapObject();
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            int id = request.id;

//            System.out.println(request.id);

            if (mInfo==null){
                send(new ResponseGetMapObjectInfo(HandlerError.Error.MAPINFO_NULL.getValue(), id, mapObjectPos, mapObjectInfo), user);
                return;
            }

            mapObjectPos = mInfo.getMapObjectPos(id);
            mapObjectInfo = mInfo.getMapObjectInfo(id);

            send(new ResponseGetMapObjectInfo(HandlerError.Error.SUCCESS.getValue(), id, mapObjectPos, mapObjectInfo), user);

        } catch (Exception e) {
            send(new ResponseGetMapObjectInfo(HandlerError.Error.EXCEPTION.getValue(), request.id, mapObjectPos, mapObjectInfo), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processMoveBuilding(User user, RequestMoveBuilding request) {
        try {
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            int id = request.id;
            int xOld = request.xOld;
            int yOld = request.yOld;
            int xNew = request.xNew;
            int yNew = request.yNew;

            if (mInfo==null){
                send(new ResponseMoveBuilding(HandlerError.Error.MAPINFO_NULL.getValue(), id, xOld, yOld, xNew, yNew), user);
                return;
            }

            boolean check = mInfo.moveBuilding(id, xNew, yNew);

            if (check)
            {
                send(new ResponseMoveBuilding(HandlerError.Error.SUCCESS.getValue(), id, xOld, yOld, xNew, yNew), user);
                mInfo.saveModel(user.getId());
            }
            else
                send(new ResponseMoveBuilding(HandlerError.Error.ERROR.getValue(), id, xOld, yOld, xNew, yNew), user);

        } catch (Exception e) {
            send(new ResponseMoveBuilding(HandlerError.Error.EXCEPTION.getValue(), request.id, request.xOld, request.yOld, request.xNew, request.yNew), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    //Check resource and subtract the player resource but NOT save
    private boolean checkoutResource(String type, long level, PlayerInfo pInfo, MapInfo mInfo) {
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

        for (int i=0; i<ServerConstant.RESOURCE.values().length; i++) {
            if (currentResource[i] < requiredResource[i]) {
                return false;
            }
        }

        for (int i=0; i<ServerConstant.RESOURCE.values().length; i++) {
            requiredResource[i] = - requiredResource[i];
        }

        mInfo.updateResource(requiredResource);
        pInfo.setCoin(pInfo.getCoin() + requiredResource[ServerConstant.RESOURCE.COIN.getValue()]);

        return true;
    }

    private void processBuildBuilding(User user, RequestBuildBuilding request) {
        try {
            int id = -1;
            String type = request.type;
            int x = request.x;
            int y = request.y;

            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (pInfo==null){
                send(new ResponseBuildBuilding(HandlerError.Error.PLAYERINFO_NULL.getValue(), id, type, x, y), user);
                return;
            }

            if (mInfo==null){
                send(new ResponseBuildBuilding(HandlerError.Error.MAPINFO_NULL.getValue(), id, type, x, y), user);
                return;
            }

            boolean check;

            if (DataLoader.getInstance().getMapObjectName(type).equals("BuilderHut"))
            {
                int count = mInfo.getNumberOfBuilding(type);
                check = checkoutResource(type, count + 1, pInfo, mInfo);
            }
            else
            {
                check = checkoutResource(type, 1, pInfo, mInfo);
            }

            if (!check)
            {
                send(new ResponseBuildBuilding(HandlerError.Error.ERROR.getValue(), id, type, x, y), user);
                return;
            }

            id = mInfo.buildBuilding(type, x, y);

            if (id == -1)
                send(new ResponseBuildBuilding(HandlerError.Error.ERROR.getValue(), id, type, x, y), user);
            else {
                pInfo.saveModel(user.getId());
                mInfo.saveModel(user.getId());

                send(new ResponseBuildBuilding(HandlerError.Error.SUCCESS.getValue(), id, type, x, y), user);
            }
        } catch (Exception e) {
            send(new ResponseBuildBuilding(HandlerError.Error.EXCEPTION.getValue(), -1, request.type, request.x, request.y), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processDeleteObstacle(User user, RequestDeleteObstacle request) {
        try {
            int id = request.id;
            long timestamp = 0;

            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);

            if (mInfo==null){
                send(new ResponseDeleteObstacle(HandlerError.Error.MAPINFO_NULL.getValue(), id, timestamp), user);
                return;
            }
            if (pInfo==null){
                send(new ResponseDeleteObstacle(HandlerError.Error.PLAYERINFO_NULL.getValue(), id, timestamp), user);
                return;
            }

            boolean check;
            String type = mInfo.getMapObjectInfo(id).getType();
            check = checkoutResource(type, 1, pInfo, mInfo);
            if (!check)
            {
                send(new ResponseDeleteObstacle(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
                return;
            }

            id = mInfo.deleteObstacle(id);

            if (id == -1)
                send(new ResponseDeleteObstacle(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
            else {
                timestamp = mInfo.getMapObjectInfo(id).getStartActionTimestamp();
                mInfo.saveModel(user.getId());
                pInfo.saveModel(user.getId());
                send(new ResponseDeleteObstacle(HandlerError.Error.SUCCESS.getValue(), id, timestamp), user);
            }
        } catch (Exception e) {
            send(new ResponseDeleteObstacle(HandlerError.Error.EXCEPTION.getValue(), request.id, 0), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processFinishDeleteObstacle(User user, RequestFinishDeleteObstacle request) {
        try {
            int id = request.id;

            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (mInfo==null){
                send(new ResponseFinishDeleteObstacle(HandlerError.Error.MAPINFO_NULL.getValue(), id), user);
                return;
            }

            id = mInfo.finishDeleteObstacle(id);

            if (id == -1)
                send(new ResponseFinishDeleteObstacle(HandlerError.Error.ERROR.getValue(), id), user);
            else {
                mInfo.saveModel(user.getId());
                send(new ResponseFinishDeleteObstacle(HandlerError.Error.SUCCESS.getValue(), id), user);
            }
        } catch (Exception e) {
            send(new ResponseFinishDeleteObstacle(HandlerError.Error.EXCEPTION.getValue(), request.id), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processSkipDeleteObstacle(User user, RequestSkipDeleteObstacle request) {
        try {
            int id = request.id;
            long timestamp = request.timestamp;

            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (pInfo==null){
                send(new ResponseSkipDeleteObstacle(HandlerError.Error.PLAYERINFO_NULL.getValue(), id, timestamp), user);
                return;
            }

            if (mInfo==null){
                send(new ResponseSkipDeleteObstacle(HandlerError.Error.MAPINFO_NULL.getValue(), id, timestamp), user);
                return;
            }

            long currentTimestamp = System.currentTimeMillis() / 1000;

            if (timestamp < currentTimestamp-10 || timestamp > currentTimestamp+10) {
                send(new ResponseSkipDeleteObstacle(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
                return;
            }

            String type = mInfo.getMapObjectInfo(id).getType();
            int level = mInfo.getMapObjectInfo(id).getLevel();
            long startActionTimestamp = mInfo.getMapObjectInfo(id).getStartActionTimestamp();

            long timelength = Integer.parseInt(
                DataLoader.getInstance().getMap(type, String.valueOf(level)).get("buildTime")
            );

            long remainTime = timelength - (timestamp - startActionTimestamp);

            boolean check = checkoutResource(mInfo.getMapObjectInfo(id).getType(), -remainTime, pInfo, mInfo);

            if (!check) {
                send(new ResponseSkipDeleteObstacle(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
                return;
            }

            id = mInfo.skipDeleteObstacle(id);

            if (id == -1)
                send(new ResponseSkipDeleteObstacle(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
            else {
                mInfo.saveModel(user.getId());
                send(new ResponseSkipDeleteObstacle(HandlerError.Error.SUCCESS.getValue(), id, timestamp), user);
            }
        } catch (Exception e) {
            send(new ResponseSkipDeleteObstacle(HandlerError.Error.EXCEPTION.getValue(), request.id, request.timestamp), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processUpgradeBuilding(User user, RequestUpgradeBuilding request) {
        try {
            int id = request.id;
            long timestamp = 0;

            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);

            if (mInfo==null){
                send(new ResponseUpgradeBuilding(HandlerError.Error.MAPINFO_NULL.getValue(), id, timestamp), user);
                return;
            }
            if (pInfo==null){
                send(new ResponseUpgradeBuilding(HandlerError.Error.PLAYERINFO_NULL.getValue(), id, timestamp), user);
                return;
            }

            boolean check;
            String type = mInfo.getMapObjectInfo(id).getType();
            int level = mInfo.getMapObjectInfo(id).getLevel();
            check = checkoutResource(type, level+1, pInfo, mInfo);
            if (!check)
            {
                send(new ResponseUpgradeBuilding(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
                return;
            }

            id = mInfo.upgradeBuilding(id);

            if (id == -1)
                send(new ResponseUpgradeBuilding(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
            else {
                timestamp = mInfo.getMapObjectInfo(id).getStartActionTimestamp();
                mInfo.saveModel(user.getId());
                send(new ResponseUpgradeBuilding(HandlerError.Error.SUCCESS.getValue(), id, timestamp), user);
            }
        } catch (Exception e) {
            send(new ResponseUpgradeBuilding(HandlerError.Error.EXCEPTION.getValue(), request.id, 0), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processFinishUpgradeBuilding(User user, RequestFinishUpgradeBuilding request) {
        try {
            int id = request.id;

            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (mInfo==null){
                send(new ResponseFinishUpgradeBuilding(HandlerError.Error.MAPINFO_NULL.getValue(), id), user);
                return;
            }

            id = mInfo.finishUpgradeBuilding(id);

            if (id == -1)
                send(new ResponseFinishUpgradeBuilding(HandlerError.Error.ERROR.getValue(), id), user);
            else {
                mInfo.saveModel(user.getId());
                send(new ResponseFinishUpgradeBuilding(HandlerError.Error.SUCCESS.getValue(), id), user);
            }
        } catch (Exception e) {
            send(new ResponseFinishUpgradeBuilding(HandlerError.Error.EXCEPTION.getValue(), request.id), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processSkipUpgradeBuilding(User user, RequestSkipUpgradeBuilding request) {
        try {
            int id = request.id;
            long timestamp = request.timestamp;

            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (pInfo==null){
                send(new ResponseSkipUpgradeBuilding(HandlerError.Error.PLAYERINFO_NULL.getValue(), id, timestamp), user);
                return;
            }

            if (mInfo==null){
                send(new ResponseSkipUpgradeBuilding(HandlerError.Error.MAPINFO_NULL.getValue(), id, timestamp), user);
                return;
            }

            long currentTimestamp = System.currentTimeMillis() / 1000;

            if (timestamp < currentTimestamp-10 || timestamp > currentTimestamp+10) {
                send(new ResponseSkipUpgradeBuilding(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
                return;
            }

            String type = mInfo.getMapObjectInfo(id).getType();
            int level = mInfo.getMapObjectInfo(id).getLevel();
            long startActionTimestamp = mInfo.getMapObjectInfo(id).getStartActionTimestamp();

            long timelength = Integer.parseInt(
                DataLoader.getInstance().getMap(type, String.valueOf(level+1)).get("buildTime")
            );

            long remainTime = timelength - (timestamp - startActionTimestamp);

            boolean check = checkoutResource(mInfo.getMapObjectInfo(id).getType(), -remainTime, pInfo, mInfo);
            if(!check) {
                send(new ResponseSkipUpgradeBuilding(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
                return;
            }

            id = mInfo.skipUpgradeBuilding(id);

            if (id == -1)
                send(new ResponseSkipUpgradeBuilding(HandlerError.Error.ERROR.getValue(), id, timestamp), user);
            else {
                mInfo.saveModel(user.getId());
                send(new ResponseSkipUpgradeBuilding(HandlerError.Error.SUCCESS.getValue(), id, timestamp), user);
            }
        } catch (Exception e) {
            send(new ResponseSkipUpgradeBuilding(HandlerError.Error.EXCEPTION.getValue(), request.id, request.timestamp), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processCancelBuildBuilding(User user, RequestCancelBuildBuilding request) {
        try {
            int id = request.id;

            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (pInfo==null){
                send(new ResponseCancelBuildBuilding(HandlerError.Error.PLAYERINFO_NULL.getValue(), id), user);
                return;
            }
            if (mInfo==null){
                send(new ResponseCancelBuildBuilding(HandlerError.Error.MAPINFO_NULL.getValue(), id), user);
                return;
            }


            String type = mInfo.getMapObjectInfo(id).getType();
            int level = 1;
            int[] returnedResource = DataLoader.getInstance().getRequiredResource(type, (int) level);
            for (int i=0; i<ServerConstant.RESOURCE.values().length; i++) {
                returnedResource[i] = returnedResource[i] / 2;
            }

            mInfo.updateResource(returnedResource);
            pInfo.setCoin(pInfo.getCoin() + returnedResource[ServerConstant.RESOURCE.COIN.getValue()]);

            id = mInfo.cancelBuildBuilding(id);

            if (id == -1)
                send(new ResponseCancelBuildBuilding(HandlerError.Error.ERROR.getValue(), id), user);
            else {
                mInfo.saveModel(user.getId());
                send(new ResponseCancelBuildBuilding(HandlerError.Error.SUCCESS.getValue(), id), user);
            }
        } catch (Exception e) {
            send(new ResponseCancelBuildBuilding(HandlerError.Error.EXCEPTION.getValue(), request.id), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processCancelUpgradeBuilding(User user, RequestCancelUpgradeBuilding request) {
        try {
            int id = request.id;

            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (pInfo==null){
                send(new ResponseCancelUpgradeBuilding(HandlerError.Error.PLAYERINFO_NULL.getValue(), id), user);
                return;
            }
            if (mInfo==null){
                send(new ResponseCancelUpgradeBuilding(HandlerError.Error.MAPINFO_NULL.getValue(), id), user);
                return;
            }


            String type = mInfo.getMapObjectInfo(id).getType();
            int level = mInfo.getMapObjectInfo(id).getLevel() + 1;
            int[] returnedResource = DataLoader.getInstance().getRequiredResource(type, (int) level);
            for (int i=0; i<ServerConstant.RESOURCE.values().length; i++) {
                returnedResource[i] = returnedResource[i] / 2;
            }

            mInfo.updateResource(returnedResource);
            pInfo.setCoin(pInfo.getCoin() + returnedResource[ServerConstant.RESOURCE.COIN.getValue()]);

            id = mInfo.cancelUpgradeBuilding(id);

            if (id == -1)
                send(new ResponseCancelUpgradeBuilding(HandlerError.Error.ERROR.getValue(), id), user);
            else {
                mInfo.saveModel(user.getId());
                send(new ResponseCancelUpgradeBuilding(HandlerError.Error.SUCCESS.getValue(), id), user);
            }
        } catch (Exception e) {
            send(new ResponseCancelUpgradeBuilding(HandlerError.Error.EXCEPTION.getValue(), request.id), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

}
