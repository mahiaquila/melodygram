package com.melodygram.constants;

/**
 * Created by LALIT on 14-06-2016.
 */
public class APIConstant
{

  //  public static final String BASE_URL = "http://13.228.40.90/";
   // public static final String SERVER_PATH = "http://13.228.40.90/";

    public static final String BASE_URL = "http://52.76.195.179/";
   public static final String SERVER_PATH = "http://52.76.195.179/";
//  // old old  public static final String BASE_URL = "http://52.74.56.14/melodygram/";
  // old old public static final String SERVER_PATH = "http://52.74.56.14/melodygram/";

    public static final String FORMAT_JSON = "/format/json";
    public static final String LOG_IN_URL = BASE_URL+"user";
    public static final String RESENT_OTP = BASE_URL+"user/resend";
    public static final String OTP_VERIFY = BASE_URL + "user/verify";
    public static final String CHANGE_NUMBER = BASE_URL + "user/changenumber";
    public static final String OTP_VERIFY_NUMBER_CHANGE = BASE_URL + "user/verify";
    public static final String UPDATE_PROFILE_PIC = BASE_URL
            + "user/updatepic";
    public static final String UPDATE_PROFILE = BASE_URL
            + "user/update";
    public static final String GET_APP_CONTACTS =BASE_URL+ "user/mycontacts";
    public static final String GET_ALL_FRIENDS = BASE_URL
            + "chat/users";
    public static final String GET_CHAT_ROOM = BASE_URL
            + "chat/room";
    public static final String GET_MESSAGE_URL = BASE_URL
            + "chat/messages" + FORMAT_JSON;
    public static final String POST_MESSAGE_URL = BASE_URL
            + "chat/sendmessage";
    public static final String BLOCK_USER = BASE_URL
            + "chat/updateblock";
    public static final String PRIVACY_SETTINGS = BASE_URL
            + "user/updateprofilesettings";
    public static final String SEND_LINK = BASE_URL
            + "chat/sendlink";
    public static final String GET_MUSIC = BASE_URL
            + "chat/getallmusic";
    public static final String DELETE_ACCOUNT = BASE_URL
            + "user/deleteaccount" + FORMAT_JSON;
    public static final String OLD_MSG_URL = BASE_URL
            + "bashi/oldmessages" + FORMAT_JSON;
    public static final String POST_MSG_URL_NEW = BASE_URL
            + "chat/sendchat";
    public static final String GET_MY_STICKERS_URL = BASE_URL
            + "chat/getallstickers";
    public static final String GET_STICKERS = BASE_URL
            + "bashi/getallbashistickers" + FORMAT_JSON;
    public static final String GET_EDIT_MESSAGE = BASE_URL+"chat/editmessage";

    public static final String GET_GROUP_ROOM_ID = BASE_URL
            + "bashi/creategroupchat" + FORMAT_JSON;
    public static final String UPDATE_GROUP_NAME = BASE_URL
            + "bashi/updategroupname" + FORMAT_JSON;
    public static final String UPDATE_GROUP_PIC = BASE_URL
            + "bashi/updategrouppic" + FORMAT_JSON;
    public static final String STATUS_UPDATE = BASE_URL
            + "bashi/updatestatus" + FORMAT_JSON;
    public static final String ADD_FRND_FAV = BASE_URL
            + "bashi/addtofav" + FORMAT_JSON;
    public static final String REMOVE_FRND_FAV = BASE_URL
            + "bashi/removefav" + FORMAT_JSON;
    public static final String VERIFIY_YOURSELF = BASE_URL
            + "bashi/verify" + FORMAT_JSON;
    public static final String EXIT_GROUP = BASE_URL
            + "bashi/updategroupmembers" + FORMAT_JSON;
    public static final String UPDATE_GROUP_MEMEBRS = BASE_URL
            + "bashi/updategroupmembers" + FORMAT_JSON;
    public static final String TYPING_FRIEND = BASE_URL
            + "chat/typing";
    public static final String REGISTER_GCM = BASE_URL
            + "user/savegcm" + FORMAT_JSON;
    public static final String NOTIFY_FRIENDS = BASE_URL
            + "bashi/notifyfriends" + FORMAT_JSON;
    public static final String CHAT_PRIVATE = BASE_URL
            + "bashi/addprivate" + FORMAT_JSON;
    public static final String BLOCK_UNBLOCK = BASE_URL
            + "bashi/updatesettings" + FORMAT_JSON;
    public static final String CHANGE_PHONE_NO = BASE_URL
            + "bashi/changenumber" + FORMAT_JSON;

    public static final String userprivacy = BASE_URL
            + "index.php/bashi/updateuserprivacy" + FORMAT_JSON;

    public static final String DELETE_ROOM = BASE_URL
            + "bashi/deleteroom" + FORMAT_JSON;
    public static final String LEAVE_GROUP = BASE_URL
            + "bashi/leavegroup" + FORMAT_JSON;
    public static final String OTP_RESEND = BASE_URL
            + "bashi/resendverification" + FORMAT_JSON;
    public static final String SEND_MAIL = BASE_URL
            + "index.php/bashi/sendmail" + FORMAT_JSON;
    public static final String MUTE_USER = BASE_URL
            + "bashi/updatemute" + FORMAT_JSON;

}
