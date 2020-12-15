package extension;


import bitzero.engine.sessions.ISession;

import bitzero.server.BitZeroServer;
import bitzero.server.config.ConfigHandle;
import bitzero.server.core.BZEventType;
import bitzero.server.entities.User;
import bitzero.server.entities.managers.ConnectionStats;
import bitzero.server.extensions.BZExtension;
import bitzero.server.extensions.data.DataCmd;

import bitzero.util.ExtensionUtility;
import bitzero.util.common.business.Debug;
import bitzero.util.datacontroller.business.DataController;
import bitzero.util.socialcontroller.bean.UserInfo;

import cmd.receive.authen.RequestLogin;

import config.DataLoader;
import event.eventType.DemoEventType;
import event.handler.NotifyController;
import event.handler.LoginSuccessHandler;
import event.handler.LogoutHandler;

import java.util.List;

import model.PlayerInfo;
import model.mapInfo.MapInfo;
import org.apache.commons.lang.exception.ExceptionUtils;

import org.json.JSONObject;

import org.junit.Assert;
import service.*;

import util.GuestLogin;

import util.metric.LogObject;
import util.metric.MetricLog;

import util.server.ServerConstant;
import util.server.ServerLoop;


public class FresherExtension extends BZExtension {
    private static String SERVERS_INFO =
        ConfigHandle.instance().get("servers_key") == null ? "servers" : ConfigHandle.instance().get("servers_key");

    private ServerLoop svrLoop;

    public FresherExtension() {
        super();
        setName("Fresher");
        svrLoop = new ServerLoop();
    }

    public void init() {
        new DataLoader();
//        selfNew();

        registerRequestHandler();
        registerEventHandler();
    }

    public ServerLoop getServerLoop() {
        return svrLoop;
    }

    @Override
    public void monitor() {
        try {
            ConnectionStats connStats = bz.getStatsManager().getUserStats();
            JSONObject data = new JSONObject();

            data.put("totalInPacket", bz.getStatsManager().getTotalInPackets());
            data.put("totalOutPacket", bz.getStatsManager().getTotalOutPackets());
            data.put("totalInBytes", bz.getStatsManager().getTotalInBytes());
            data.put("totalOutBytes", bz.getStatsManager().getTotalOutBytes());

            data.put("connectionCount", connStats.getSocketCount());
            data.put("totalUserCount", bz.getUserManager().getUserCount());

            DataController.getController().setCache(SERVERS_INFO, 60 * 5, data.toString());
        } catch (Exception e) {
            trace("Ex monitor");
        }
    }

    @Override
    public void destroy() {
        List<User> allUser = ExtensionUtility.globalUserManager.getAllUsers();
        if (allUser.size() == 0)
            return;

        User obj = null;

        for (int i = 0; i < allUser.size(); i++) {
            obj = allUser.get(i);
            // do sth with user
            LogObject logObject = new LogObject(LogObject.ACTION_LOGOUT);
            logObject.zingId = obj.getId();
            logObject.zingName = obj.getName();
            //System.out.println("Log logout = " + logObject.getLogMessage());
            MetricLog.writeActionLog(logObject);
        }
    }

    /**
     *
     * @param cmdId
     * @param session
     * @param objData
     *
     * the first packet send from client after handshake success will dispatch to doLogin() function
     */
    public void doLogin(short cmdId, ISession session, DataCmd objData) {
        RequestLogin reqGet = new RequestLogin(objData);
        reqGet.unpackData();
       
        try {
            
            UserInfo uInfo = getUserInfo(reqGet.sessionKey, reqGet.userId, session.getAddress());

//            try{
//                Assert.assertTrue("unit_test_error | check user info # null", uInfo!=null);
//                Debug.warn("unit_test | #1 | passed | user # null");
//            }catch(AssertionError e){
//                Debug.warn("unit_test | #1 | failed | user null, check FresherExtension->getUserInfo()");
//            }
//
//            try{
//                Assert.assertEquals("unit_test_error | check userId","1", uInfo.getUserId());
//                Debug.warn("unit_test | #2 | passed | check userId");
//            }catch(AssertionError e){
//                Debug.warn("unit_test | #2 | failed | userId # 1, check sendLoginRequest() function in client");
//            }

            User u = ExtensionUtility.instance().canLogin(uInfo, "", session);
            if (u!=null)
                u.setProperty("userId", uInfo.getUserId());            
        } catch (Exception e) {
            Debug.warn("DO LOGIN EXCEPTION " + e.getMessage());
            Debug.warn(ExceptionUtils.getStackTrace(e));
        }

    }

    private UserInfo getUserInfo(String username, int userId, String ipAddress) throws Exception {
        int customLogin = ServerConstant.CUSTOM_LOGIN;

//        try{
//            Assert.assertEquals("unit_test_error | check CUSTOM_LOGIN", 1, customLogin);
//            Debug.warn("unit_test | #0 | passed | check CUSTOM_LOGIN");
//        }catch(AssertionError e){
//            Debug.warn("unit_test | #0 | failed | cluster.properties -> custom_login # 2");
//        }

        switch(customLogin){
            case 1: // set direct userid
                return GuestLogin.setInfo(userId, "Fresher_" + userId);
            case 2: // login zingme
                return ExtensionUtility.getUserInfoFormPortal(username);
            case 3:
                return GuestLogin.newGuest();
            default: // auto increment
                return null;
        }                
    }

    private void registerRequestHandler(){
        /**
         * register new request handler to catch client's packet
         */
        trace("  Register  Request Handler ");
        addRequestHandler(DemoHandler.DEMO_MULTI_IDS, DemoHandler.class);
        addRequestHandler(UserHandler.USER_MULTI_IDS, UserHandler.class);
        addRequestHandler(MapHandler.MAP_MULTI_IDS, MapHandler.class);
        addRequestHandler(TroopHandler.TROOP_MULTI_IDS, TroopHandler.class);
        addRequestHandler(TrainHandler.TRAIN_MULTI_IDS, TrainHandler.class);
        addRequestHandler(CampaignHandler.CAMPAIGN_MULTI_IDS, CampaignHandler.class);
    }

    private void registerEventHandler(){
        /**
         * register new event
         */
        trace(" Event Handler ");
        addEventHandler(BZEventType.USER_LOGIN, LoginSuccessHandler.class);
        addEventHandler(BZEventType.USER_LOGOUT, LogoutHandler.class);
        addEventHandler(BZEventType.USER_DISCONNECT, LogoutHandler.class);

        addEventHandler(DemoEventType.CHANGE_POSITION, NotifyController.class);
//        addEventHandler(DemoEventType.LOGIN_SUCCESS, DemoHandler.class);
//        addEventHandler(DemoEventType.LOGIN_SUCCESS, UserHandler.class);
//        addEventHandler(DemoEventType.UPDATE_RESOURCE, UserHandler.class);
    }

    private void selfNew() {
        List<User> users = BitZeroServer.getInstance().getUserManager().getAllUsers();
        MapInfo mapInfo = new MapInfo();
        for (User user : users) {
            PlayerInfo playerInfo = new PlayerInfo(user.getId(), "FresherG4_" + user.getId());
            try {
                playerInfo.saveModel(user.getId());
                mapInfo.saveModel(user.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
