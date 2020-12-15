package cmd;


public class CmdDefine {
    public static final short CUSTOM_LOGIN = 1;

    //User cmd
    public static final short GET_USER_INFO = 1001;
    public static final short GET_USER_RESOURCE_INFO = 1002;
    public static final short GET_TIMESTAMP = 1003;
    public static final short GET_USERNAME = 1004;
    public static final short UPDATE_RESOURCE = 1005;


    //Map cmd
    public static final short GET_MAP_INFO = 2001;
    public static final short GET_MAP_OBJECT_COUNT = 2002;
    public static final short GET_MAP_OBJECT_INFO = 2003;
    public static final short MOVE_BUILDING = 2004;
    public static final short BUILD_BUILDING =	2005;
    public static final short DELETE_OBSTACLE =	2006;
    public static final short FINISH_DELETE_OBSTACLE = 2007;
    public static final short SKIP_DELETE_OBSTACLE = 2008;
    public static final short UPGRADE_BUILDING = 2009;
    public static final short FINISH_UPGRADE_BUILDING =	2010;
    public static final short SKIP_UPGRADE_BUILDING = 2011;
    public static final short CANCEL_BUILD_BUILDING =	2012;
    public static final short CANCEL_UPGRADE_BUILDING =	2013;
    public static final short GET_MAP_INFO_LIST = 2014;

    //Troop cmd
    public static final short GET_TROOP = 3001;

    //Train cmd
    public static final short GET_TRAIN_INFO = 4001;
    public static final short TRAIN_TROOP = 4002;
    public static final short SKIP_TROOP = 4003;
    public static final short CANCEL_TROOP = 4004;
    public static final short TRAIN_TROOP_FINISH = 4005;

    //Attack Map cmd
    public static final short GET_CAMPAIGN_INFO = 5001;
    public static final short ENTER_CAMPAIGN = 5002;
    public static final short START_CAMPAIGN = 5003;
    public static final short DROP_TROOP = 5004;
    public static final short END_CAMPAIGN = 5005;

   
    //Log cmd
    public static final short MOVE_OLD = 6001;
    public static final short GET_NAME = 6002;
    public static final short SET_NAME = 6003;

    public static final short UPDATE_STATUS = 6004;
    public static final short MOVE = 6005;
    public static final short RESET_MAP = 6006;
}
