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
import cmd.receive.attackMap.RequestDropTroop;
import cmd.receive.attackMap.RequestEndCampaign;
import cmd.receive.attackMap.RequestEnterCampaign;
import cmd.receive.attackMap.RequestStartCampaign;
import cmd.receive.train.RequestCancelTroop;
import cmd.receive.train.RequestSkipTroop;
import cmd.receive.train.RequestTrainTroop;
import cmd.receive.train.RequestTrainTroopFinish;
import cmd.send.attackMap.*;
import cmd.send.train.*;
import config.DataLoader;
import event.eventType.DemoEventParam;
import event.eventType.DemoEventType;
import model.PlayerInfo;
import model.attackMapInfo.AttackManager;
import model.attackMapInfo.campaignMapInfo.CampaignMapInfo;
import model.mapInfo.Building.ArmyBuilding.Barrack;
import model.mapInfo.MapInfo;
import model.trainInfo.TrainInfo;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.server.ServerConstant;

import java.util.HashMap;
import java.util.Map;

public class CampaignHandler extends BaseClientRequestHandler implements IServerEventHandler {

    public static short CAMPAIGN_MULTI_IDS = 5000;

    /**
     * log4j level
     * ALL < DEBUG < INFO < WARN < ERROR < FATAL < OFF
     */

    private final Logger logger = LoggerFactory.getLogger("CampaignHandler");

    public CampaignHandler() {
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
//        if (ibzevent.getType() == DemoEventType.LOGIN_SUCCESS) {
//            this.processUpdateAll((User)ibzevent.getParameter(DemoEventParam.USER));
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
                case CmdDefine.GET_CAMPAIGN_INFO:
                    processGetCampaignInfo(user);
                    break;
                case CmdDefine.ENTER_CAMPAIGN:
                    RequestEnterCampaign requestEnterCampaign = new RequestEnterCampaign(dataCmd);
                    processEnterCampaign(user, requestEnterCampaign);
                    break;
                case CmdDefine.START_CAMPAIGN:
                    RequestStartCampaign requestStartCampaign = new RequestStartCampaign(dataCmd);
                    processStartCampaign(user, requestStartCampaign);
                    break;
                case CmdDefine.DROP_TROOP:
                    RequestDropTroop requestDropTroop = new RequestDropTroop(dataCmd);
                    processDropTroop(user, requestDropTroop);
                    break;
                case CmdDefine.END_CAMPAIGN:
                    RequestEndCampaign requestEndCampaign = new RequestEndCampaign(dataCmd);
                    processEndCampaign(user, requestEndCampaign);
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
    
    private void processGetCampaignInfo(User user) {
        try {
            PlayerInfo playerInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (playerInfo == null){
                send(new ResponseGetCampaignInfo(HandlerError.Error.MAPINFO_NULL.getValue(), null), user);
                return;
            }

            send(new ResponseGetCampaignInfo(HandlerError.Error.SUCCESS.getValue(), playerInfo), user);

        } catch (Exception e) {
            send(new ResponseGetCampaignInfo(HandlerError.Error.EXCEPTION.getValue(), null), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processEnterCampaign(User user, RequestEnterCampaign request) {
        int level = request.level;
        try {
            PlayerInfo playerInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (playerInfo == null) {
                send(new ResponseEnterCampaign(HandlerError.Error.PLAYERINFO_NULL.getValue(), level, null, null), user);
                return;
            }
            MapInfo mapInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mapInfo == null) {
                send(new ResponseEnterCampaign(HandlerError.Error.MAPINFO_NULL.getValue(), level, null, null), user);
                return;
            }

            AttackManager attackManager = new AttackManager(playerInfo, level);

            user.setProperty(ServerConstant.ATTACK_MAP_INFO, attackManager);
            CampaignMapInfo campaignMapInfo = attackManager.getCampaignMapInfo();

            send(new ResponseEnterCampaign(HandlerError.Error.SUCCESS.getValue(), level, campaignMapInfo, mapInfo), user);

        } catch (Exception e) {
            send(new ResponseEnterCampaign(HandlerError.Error.EXCEPTION.getValue(), level, null, null), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processStartCampaign(User user, RequestStartCampaign request) {
        int[] selectedTroop = request.selectedTroop;
        try {
            MapInfo mapInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mapInfo == null) {
                send(new ResponseStartCampaign(HandlerError.Error.MAPINFO_NULL.getValue(), selectedTroop), user);
                return;
            }
            AttackManager attackManager = (AttackManager) user.getProperty(ServerConstant.ATTACK_MAP_INFO);
            if (attackManager==null){
                send(new ResponseStartCampaign(HandlerError.Error.ERROR.getValue(), selectedTroop), user);
                return;
            }

            if (attackManager.setTroop(mapInfo, selectedTroop) != -1) {
                mapInfo.saveModel(user.getId());
                send(new ResponseStartCampaign(HandlerError.Error.SUCCESS.getValue(), selectedTroop), user);
            }
            else
                send(new ResponseStartCampaign(HandlerError.Error.ERROR.getValue(), selectedTroop), user);

        } catch (Exception e) {
            send(new ResponseStartCampaign(HandlerError.Error.EXCEPTION.getValue(), selectedTroop), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processDropTroop(User user, RequestDropTroop request) {
        String troopType = request.troopType;
        int id = request.id;
        int x = request.x;
        int y = request.y;
        int tick = request.tick;
        try {
            AttackManager attackManager = (AttackManager) user.getProperty(ServerConstant.ATTACK_MAP_INFO);
            if (attackManager==null){
                send(new ResponseDropTroop(HandlerError.Error.ERROR.getValue(), troopType, id, x, y, tick), user);
                return;
            }

            if (attackManager.dropTroop(troopType, id, x, y, tick) != -1) {
                send(new ResponseDropTroop(HandlerError.Error.SUCCESS.getValue(), troopType, id, x, y, tick), user);
            }
            else
                send(new ResponseDropTroop(HandlerError.Error.ERROR.getValue(), troopType, id, x, y, tick), user);

        } catch (Exception e) {
            send(new ResponseDropTroop(HandlerError.Error.EXCEPTION.getValue(), troopType, id, x, y, tick), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }

    private void processEndCampaign(User user, RequestEndCampaign request) {
        int tick = request.tick;
        try {
            PlayerInfo playerInfo = (PlayerInfo) user.getProperty(ServerConstant.PLAYER_INFO);
            if (playerInfo == null) {
                send(new ResponseEndCampaign(HandlerError.Error.PLAYERINFO_NULL.getValue(), 0, 0, 0), user);
                return;
            }
            MapInfo mapInfo = (MapInfo) user.getProperty(ServerConstant.MAP_INFO);
            if (mapInfo == null) {
                send(new ResponseEndCampaign(HandlerError.Error.MAPINFO_NULL.getValue(), 0, 0, 0), user);
                return;
            }
            AttackManager attackManager = (AttackManager) user.getProperty(ServerConstant.ATTACK_MAP_INFO);
            if (attackManager==null){
                send(new ResponseEndCampaign(HandlerError.Error.ERROR.getValue(), 0, 0, 0), user);
                return;
            }

            if (attackManager.endCampaign(playerInfo, mapInfo, tick) != -1) {
                playerInfo.saveModel(user.getId());
                mapInfo.saveModel(user.getId());

                int level = attackManager.getLevel();
                int star = attackManager.getStar();
                int gold = attackManager.getGold();
                int elixir = attackManager.getElixir();

                send(new ResponseEndCampaign(HandlerError.Error.SUCCESS.getValue(), gold, elixir, star), user);
            }
            else {
                send(new ResponseEndCampaign(HandlerError.Error.ERROR.getValue(), 0, 0, 0), user);
            }

        } catch (Exception e) {
            send(new ResponseEndCampaign(HandlerError.Error.EXCEPTION.getValue(), 0, 0, 0), user);
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }
    }
}
