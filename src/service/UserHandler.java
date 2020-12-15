package service;

import bitzero.server.BitZeroServer;
import bitzero.server.core.BZEvent;
import bitzero.server.core.BZEventParam;
import bitzero.server.core.BZEventType;
import bitzero.server.core.IBZEvent;
import bitzero.server.entities.User;
import bitzero.server.extensions.BaseClientRequestHandler;
import bitzero.server.extensions.IServerEventHandler;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import cmd.CmdDefine;

import cmd.receive.user.RequestUpdateResource;
import cmd.receive.user.RequestUserInfo;

import cmd.send.demo.ResponseRequestUserInfo;
import cmd.send.user.ResponseGetTimestamp;
import cmd.send.user.ResponseGetUserResourceInfo;
import cmd.send.user.ResponseGetUsername;
import cmd.send.user.ResponseUpdateResource;
import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import extension.FresherExtension;

import model.PlayerInfo;

import model.mapInfo.MapInfo;
import org.apache.commons.lang.exception.ExceptionUtils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.server.ServerConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHandler extends BaseClientRequestHandler implements IServerEventHandler {
    public static short USER_MULTI_IDS = 1000;

    private final Logger logger = LoggerFactory.getLogger("UserHandler");
    
    public UserHandler() {
        super();
    }

    public void init() {
        getExtension().addEventListener(BZEventType.USER_DISCONNECT, this);
        getExtension().addEventListener(BZEventType.USER_RECONNECTION_SUCCESS, this);

        /**
         *  register new event, so the core will dispatch event type to this class
         */
        getExtension().addEventListener(DemoEventType.CHANGE_NAME, this);
        getExtension().addEventListener(DemoEventType.LOGIN_SUCCESS, this);
        getExtension().addEventListener(DemoEventType.UPDATE_RESOURCE, this);
    }

    private FresherExtension getExtension() {
        return (FresherExtension) getParentExtension();
    }

    public void handleServerEvent(IBZEvent ibzevent) {

        if (ibzevent.getType() == DemoEventType.LOGIN_SUCCESS) {
//            this.processGetMapInfo((User) ibzevent.getParameter(DemoEventParam.USER));
//            this.processGetMapObjectCount((User) ibzevent.getParameter(DemoEventParam.USER));
            this.processGetUsername((User) ibzevent.getParameter(DemoEventParam.USER));
            this.processGetUserResourceInfo((User) ibzevent.getParameter(DemoEventParam.USER));
            this.processGetTimestamp((User) ibzevent.getParameter(DemoEventParam.USER));
        }
        else if (ibzevent.getType() == DemoEventType.UPDATE_RESOURCE) {
            this.processGetUserResourceInfo((User) ibzevent.getParameter(DemoEventParam.USER));
        }
        else if (ibzevent.getType() == BZEventType.USER_DISCONNECT)
            this.userDisconnect((User) ibzevent.getParameter(BZEventParam.USER));
        else if (ibzevent.getType() == DemoEventType.CHANGE_NAME)
            this.userChangeName((User) ibzevent.getParameter(DemoEventParam.USER), (String)ibzevent.getParameter(DemoEventParam.NAME));
    }

    public void handleClientRequest(User user, DataCmd dataCmd) {
        try {
            switch (dataCmd.getId()) {
                case CmdDefine.GET_USER_INFO:
                    RequestUserInfo reqInfo = new RequestUserInfo(dataCmd);
                    getUserInfo(user);
                    break;
                case CmdDefine.GET_USER_RESOURCE_INFO:
                    processGetUserResourceInfo(user);
                    break;
                case CmdDefine.GET_TIMESTAMP:
                    processGetTimestamp(user);
                    break;
                case CmdDefine.UPDATE_RESOURCE:
                    RequestUpdateResource requestUpdateResource = new RequestUpdateResource(dataCmd);
                    processUpdateResource(user, requestUpdateResource);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.warn("USERHANDLER EXCEPTION " + e.getMessage());
            logger.warn(ExceptionUtils.getStackTrace(e));
        }

    }

    private void getUserInfo(User user) {
        try {
            PlayerInfo userInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (userInfo == null) {
                userInfo = new PlayerInfo(user.getId(), "FresherG4_" + user.getId());
                userInfo.saveModel(user.getId());
            }
            send(new ResponseRequestUserInfo(userInfo), user);
        } catch (Exception e) {

        }

    }

    private void userDisconnect(User user) {
        // log user disconnect
    }

    private void userChangeName(User user, String name){
        List<User> allUser = BitZeroServer.getInstance().getUserManager().getAllUsers();
        for(User aUser : allUser){
            // notify user's change
        }
    }

    //Fresher
    private void processGetUsername(User user){
        try{
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (pInfo==null){
                send(new ResponseGetUsername(HandlerError.Error.PLAYERINFO_NULL.getValue(), ""), user);
            }
            logger.warn("Username =" + pInfo.getName());
            send(new ResponseGetUsername(HandlerError.Error.SUCCESS.getValue(), pInfo.getName()), user);
        }catch(Exception e){
            logger.info("processGetName exception");
            send(new ResponseGetUsername(HandlerError.Error.EXCEPTION.getValue(), ""), user);
        }
    }

    private void processGetUserResourceInfo(User user) {
        int gold = 0;
        int elixir = 0;
        int darkElixir = 0;
        int coin = 0;
        try {
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (pInfo==null){
                send(new ResponseGetUserResourceInfo(HandlerError.Error.PLAYERINFO_NULL.getValue(), gold, elixir, darkElixir, coin), user);
                return;
            }

            if (mInfo==null){
                send(new ResponseGetUserResourceInfo(HandlerError.Error.MAPINFO_NULL.getValue(), gold, elixir, darkElixir, coin), user);
                return;
            }

            int[] currentResource = mInfo.getResource();

            gold = currentResource[ServerConstant.RESOURCE.GOLD.getValue()];
            elixir = currentResource[ServerConstant.RESOURCE.ELIXIR.getValue()];
            darkElixir = currentResource[ServerConstant.RESOURCE.DARK_ELIXIR.getValue()];
            coin = pInfo.getCoin();

//            gold = 10000;
//            elixir = 20000;
//            darkElixir = 30000;
//            coin = 40000;

            send(new ResponseGetUserResourceInfo(HandlerError.Error.SUCCESS.getValue(), gold, elixir, darkElixir, coin), user);


        } catch (Exception e) {
            send(new ResponseGetUserResourceInfo(HandlerError.Error.EXCEPTION.getValue(), gold, elixir, darkElixir, coin), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processUpdateResource(User user, RequestUpdateResource request) {
        int gold = request.gold;
        int elixir = request.elixir;
        int darkElixir = request.darkElixir;
        int coin = request.coin;
        try {
            PlayerInfo pInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            MapInfo mInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);

            if (pInfo==null){
                send(new ResponseUpdateResource(HandlerError.Error.PLAYERINFO_NULL.getValue()), user);
                return;
            }

            if (mInfo==null){
                send(new ResponseUpdateResource(HandlerError.Error.MAPINFO_NULL.getValue()), user);
                return;
            }

            int[] updateResource = new int[ServerConstant.RESOURCE.values().length];

            updateResource[ServerConstant.RESOURCE.GOLD.getValue()] = gold;
            updateResource[ServerConstant.RESOURCE.ELIXIR.getValue()] = elixir;
            updateResource[ServerConstant.RESOURCE.DARK_ELIXIR.getValue()] = darkElixir;
            updateResource[ServerConstant.RESOURCE.COIN.getValue()] = coin;

            if (coin >= 0) {

            }
            else {
                int tempCoin = (int) Math.ceil((double) gold/1000)
                            + (int) Math.ceil((double) elixir/1000)
                            + (int) Math.ceil((double) darkElixir/1000);
                if (tempCoin != -coin) {
                    send(new ResponseUpdateResource(HandlerError.Error.ERROR.getValue()), user);
                    return;
                }
            }

            mInfo.updateResource(updateResource);
            pInfo.setCoin(pInfo.getCoin() + updateResource[ServerConstant.RESOURCE.COIN.getValue()]);

            send(new ResponseUpdateResource(HandlerError.Error.SUCCESS.getValue()), user);

            pInfo.saveModel(user.getId());
            mInfo.saveModel(user.getId());

            dispatchEventUpdateResource(user);
        } catch (Exception e) {
            send(new ResponseUpdateResource(HandlerError.Error.EXCEPTION.getValue()), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processGetTimestamp(User user) {
        long timestamp = -1;
        try {
            timestamp = System.currentTimeMillis() / 1000;
            send(new ResponseGetTimestamp(HandlerError.Error.SUCCESS.getValue(), timestamp), user);

        } catch (Exception e) {
            send(new ResponseGetTimestamp(HandlerError.Error.EXCEPTION.getValue(), timestamp), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }
    
    private void dispatchEventUpdateResource(User user) {
        Map evtParams = new HashMap();
        evtParams.put(DemoEventParam.USER, user);
        ExtensionUtility.dispatchEvent(new BZEvent(DemoEventType.UPDATE_RESOURCE, evtParams));
    }
}
