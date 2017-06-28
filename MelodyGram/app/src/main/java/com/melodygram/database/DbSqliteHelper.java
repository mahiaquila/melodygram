package com.melodygram.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by LALIT on 15-06-2016.
 */
public class DbSqliteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MelodyGram.db";
    private static final int DATABASE_VERSION = 1;

    public static final String phContactTable = "phContactTable";

    public static final String phContactId = "phContactId";
    public static final String phContactName = "phContactName";
    public static final String phContactNumber = "phContactNumber";
    public static final String phContactLabel = "phContactLabel";
    public static final String phChatContactStatus = "phChatContactStatus";
    public static final String phContactPicPath = "phContactPicPath";

    private static final String createPhContactTable = "create table if not exists "
            + phContactTable
            + "("
            + phContactId
            + " Text not null,"
            + phContactName
            + " Text not null,"
            + phContactNumber
            + " Text not null,"
            + phContactLabel
            + " Text not null,"
            + phChatContactStatus
            + " Text not null,"
            + phContactPicPath
            + " Text not null);";

    public static final String appContactTable = "appContactTable";
    public static final String appContactsUserId = "appContactsUserId";
    public static final String appContactsName = "appContactsName";
    public static final String appLocalContactsName = "appLocalContactsName";
    public static final String appContactsCountryCode = "appContactsCountryCode";
    public static final String appContactsCountry = "appContactsCountry";
    public static final String appContactsMobileNo = "appContactsMobileNo";
    public static final String appContacts = "appContactsIsBlocked";
    public static final String appContactsLastSeen = "appContactsLastSeen";
    public static final String appContactsProfilePic = "appContactsProfilePic";
    public static final String appContactsStatus = "appContactsStatus";
    public static final String appContactsGender = "appContactsGender";
    public static final String appProfilePicPrivacy = "appProfilePicPrivacy";
    public static final String appLastSeenPrivacy = "appLastSeenPrivacy";
    public static final String appStatusPrivacy = "appStatusPrivacy";
    public static final String appOnlinePrivacy = "appOnlinePrivacy";
    public static final String appReadReceiptsPrivacy = "appReadReceiptsPrivacy";
    public static final String appNotfication = "appNotfication";
    public static final String appMuteChat = "appMuteChat";
    public static final String CONTACT_DELETE_FLAG = "deleteFlag";
    private static final String createAppContactTable = "create table if not exists "
            + appContactTable
            + "("
            + appContactsUserId
            + " INTEGER PRIMARY KEY,"
            + appContactsName
            + " Text not null,"
            + appLocalContactsName
            + " Text not null,"
            + appContactsCountryCode
            + " Text not null,"
            + appContactsCountry
            + " Text not null,"
            + appContactsMobileNo
            + " Text not null,"
            + appContactsLastSeen
            + " Text not null,"
            + appContactsProfilePic
            + " Text not null,"
            + appContactsStatus
            + " Text not null,"
            + appContactsGender
            + " Text not null,"
            + CONTACT_DELETE_FLAG
            + " Text not null,"
            + appProfilePicPrivacy
            + " Text not null,"
            + appLastSeenPrivacy
            + " Text not null,"
            + appStatusPrivacy
            + " Text not null,"
            + appOnlinePrivacy
            + " Text not null,"
            + appReadReceiptsPrivacy
            + " Text not null,"
            + appNotfication
            + " Text not null,"
            + appMuteChat
            + " Text not null);";

    public static final String friendsListTable = "friendsListTable";
    public static final String friendsUserId = "friendsUserId";
    public static final String friendsName = "friendsName";
    public static final String friendsMobileNo = "friendsMobileNo";
    public static final String friendsStatus = "friendsStatus";
    public static final String friendsProfilePic = "friendsProfilePic";
    public static final String friendsRoomId = "friendsRoomId";
    public static final String friendsLastchat = "friendsLastchat";
    public static final String friendsLastChatTime = "friendsLastChatTime";
    public static final String friendsChatType = "friendsChatType";
    public static final String friendsCountryCode = "friendsCountryCode";
    public static final String friendsLastSeen = "friendsLastSeen";
    public static final String friendsUnreadMSG = "friendsUnreadMSG";
    public static final String friendsIsDeleted = "friendsIsDeleted";
    public static final String friendsIsOnline = "friendsIsOnline";
    public static final String groupUsers = "groupUsers";
    public static final String unSeenCount = "unSeenCount";
    public static final String mute = "mute";
    public static final String profilePicPrivacy = "profilePicPrivacy";
    public static final String lastSeenPrivacy = "lastSeenPrivacy";
    public static final String statusPrivacy = "statusPrivacy";
    public static final String ReadReceipt = "readReceipt";
    public static final String onlinePrivacy = "onlinePrivacy";
    public static final String CHAT_DELETE_FLAG = "chat_delete_flag";
    public static final String BLOCK = "block";
    public static final String IS_BLOCK = "isBlock";
    public static final String LOCATE_TOP = "locatetop";
    public static final String LOCATE_DATE = "lcatedate";
    private static final String createFriendsTable = "create table if not exists "
            + friendsListTable
            + "("
            + friendsUserId
            + " integer primary key,"
            + friendsName
            + " Text,"
            + friendsMobileNo
            + " Text,"
            + friendsStatus
            + " Text,"
            + friendsProfilePic
            + " Text,"
            + friendsRoomId
            + " Text,"
            + friendsLastchat
            + " Text,"
            + friendsLastChatTime
            + " DATETIME,"
            + friendsChatType
            + " Text,"
            + friendsCountryCode
            + " Text,"
            + friendsLastSeen
            + " Text,"
            + friendsIsDeleted
            + " Text,"
            + friendsIsOnline
            + " Text,"
            + groupUsers
            + " Text,"
            + unSeenCount
            + " Text,"
            + CHAT_DELETE_FLAG
            + " Text,"
            + mute
            + " Text,"
            + profilePicPrivacy
            + " Text,"
            + lastSeenPrivacy
            + " Text,"
            + statusPrivacy
            + " Text,"
            + onlinePrivacy
            + " Text,"
            + BLOCK
            + " Text," +
            LOCATE_TOP + " INTEGER DEFAULT 0," +LOCATE_DATE +" Text,"+ReadReceipt +" Text,"+IS_BLOCK +" Text);";

    public static final String chatMessageTable = "chatMessageTable";
    public static final String chatPrimaryKey = "chatPrimaryKey";
    public static final String chatMessageId = "chatMessageId";
    public static final String chatMessageSenderName = "chatMessageSenderName";
    public static final String chatMessageSenderPic = "chatMessageSenderPic";
    public static final String chatMessageRoomId = "chatMessageRoomId";
    public static final String chatMessageUserId = "chatMessageUserId";
    public static final String chatMessage = "chatMessage";
    public static final String chatMessageType = "chatMessageType";
    public static final String chatMessageFileUrl = "chatMessageFileUrl";
    public static final String chatMessageLat = "chatMessageLat";
    public static final String chatMessageLan = "chatMessageLan";
    public static final String chatMessageUserTime = "chatMessageUserTime";
    public static final String chatMessageUserDate = "chatMessageUserDate";
    public static final String chatMessageToFromFlag = "chatMessageToFromFlag";
    public static final String chatMessageSingleTickFlag = "chatMessageSingleTickFlag";
    public static final String chatMessageSeen = "chatMessageSeen";
    public static final String chatMessageSeenTime = "chatMessageSeenTime";
    public static final String chatFileThumbnail = "chatFileThumbnail";
    public static final String chatIsRemoved = "chatIsRemoved";
    public static final String chatIsSent = "chatIsSent";
    public static final String chatTempMessageId = "chatTempMessageId";
    public static final String chatDisappear = "chatDisappear";
    public static final String chatAudioTime = "chatAudioTime";
    public static final String stickerId = "stickerId";
    public static final String CHAT_IS_EDIT_SENT = "chatIsEditSent";
    public static final String profilePicVisible = "isProfilePic";
    public static final String imageDownload = "imaageDownload";

    private static final String createChatMessageTable = "create table if not exists "
            + chatMessageTable
            + "("
            + chatPrimaryKey
            + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + chatMessageId
            + " INTEGER not null,"
            + chatMessageSenderName
            + " Text,"
            + chatMessageSenderPic
            + " Text,"
            + chatMessageRoomId
            + " Text,"
            + chatMessageUserId
            + " Text,"
            + chatMessage
            + " Text,"
            + chatMessageType
            + " Text,"
            + chatMessageFileUrl
            + " Text,"
            + chatMessageLat
            + " Text,"
            + chatMessageLan
            + " Text,"
            + chatMessageUserTime
            + " Text,"
            + chatMessageUserDate
            + " Text,"
            + chatMessageToFromFlag
            + " Text,"
            + chatMessageSingleTickFlag
            + " Text,"
            + chatMessageSeen
            + " Text,"
            + chatMessageSeenTime
            + " Text,"
            + chatFileThumbnail
            + " Text,"
            + chatIsRemoved
            + " Text,"
            + chatIsSent
            + " Text,"
            + chatTempMessageId
            + " Text,"
            + chatDisappear
            + " Text,"
            + chatAudioTime
            + " Text," +
             stickerId
            + " Text," +
            CHAT_IS_EDIT_SENT
           + " Text,"+
            imageDownload
            + " INTEGER);";

    public static final String TABLE_NAME_STICKERS = "stickersTable";
    public static final String STICKERS_ID = "stickersId";
    public static final String STICKERS_NAME = "stickersName";
    public static final String STICKERS_DESCRIPTION = "stickersDescription";
    public static final String STICKERS_PIC = "stickersPic";
    public static final String STICKERS_IS_FREE = "stickersIsFree";
    public static final String STICKERS_IS_ACTIVE = "stickersIsActive";
    public static final String STICKERS_COST = "stickersCost";
    public static final String STICKERS_TIMESTAMP = "stickersTimestamp";
    public static final String STICKERS_IS_PRUCHESED = "stickersIsPurchesed";
    public static final String STICKERS_CATEGORY_ID = "stickersCategoryId";
    private static final String CREATE_TABLE_STICKERS = "create table if not exists "
            + TABLE_NAME_STICKERS
            + "("
            + STICKERS_ID
            + " Text not null,"
            + STICKERS_NAME
            + " Text not null,"
            + STICKERS_PIC
            + " Text not null," + STICKERS_CATEGORY_ID + " Text not null);";
    public static final String TABLE_NAME_CATEGORY = "categoryTable";
    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CATEGORY_THUMB_INACTIVE = "categoryThumbInactive";
    public static final String CATEGORY_THUMB = "categoryThumb";
    public static final String CATEGORY_IS_FREE = "categoryIsFree";
    public static final String CATEGORY_COST = "categoryCost";
    public static final String CATEGORY_IS_PRUCHESED = "categoryIsPurchesed";
    private static final String CREATE_TABLE_CATEGORY = "create table if not exists "
            + TABLE_NAME_CATEGORY
            + "("
            + CATEGORY_ID
            + " INTEGER PRIMARY KEY,"
            + CATEGORY_NAME
            + " Text not null,"
            + CATEGORY_THUMB
            + " Text not null," +
            CATEGORY_THUMB_INACTIVE
            + " Text not null,"
            + CATEGORY_IS_FREE
            + " Text not null,"
            + CATEGORY_COST
            + " Text not null,"
            + CATEGORY_IS_PRUCHESED
            + " Text not null);";
    public static final String USER_ID = "Id";
    public static final String NAME = "name";
    public static final String COUNTRY = "country";
    public static final String COUNTRY_CODE = "countryCode";
    public static final String MOBILE_NO = "mobile";
    public static final String PROFILE_PIC = "profilePic";
    public static final String VERIFICATION_ID = "verificationId";
    public static final String VERIFIED = "verified";
    public static final String VERIFY = "verify";
    public static final String FIRST_TIME_FLAG = "firstTimeFlag";
    public static final String LOGIN_SUCCESS = "login";
    public static final String GENDER = "gender";
    public static final String CITY = "city";
    public static final String BIRTHDATE = "birthdate";
    public static final String TABLE_NAME_CREDENTAILS = "credentails";
    private static final String CREATE_TABLE_CREDENTAILS = "create table if not exists "
            + TABLE_NAME_CREDENTAILS
            + "("
            + USER_ID
            + " Text not null,"
            + NAME
            + " Text not null,"
            + GENDER
            + " Text not null,"
            + CITY
            + " Text not null,"
            + BIRTHDATE
            + " Text not null,"
            + COUNTRY
            + " Text not null,"
            + COUNTRY_CODE
            + " Text not null,"
            + MOBILE_NO
            + " Text not null,"
            + PROFILE_PIC
            + " Text not null,"
            + VERIFICATION_ID
            + " Text not null,"
            + VERIFIED
            + " Text not null,"
            + VERIFY
            + " Text not null,"
            + FIRST_TIME_FLAG
            + " Text not null," + LOGIN_SUCCESS + " Text not null);";
    public static final String TABLE_NAME_MUSIC = "music";
    public static final String MUSIC_ID = "Id";
    public static final String CAT_ID = "catId";
    public static final String MUSIC_NAME = "Name";
    public static final String PIC = "Pic";
    public static final String MUSIC_LINK = "downloadLink";
    public static final String MUSIC_LOCAL_PATH = "localPath";
    public static final String TABLE_NAME_MUSIC_CATEGORY = "musicCategoryTable";
    public static final String MUSIC_CATEGORY_ID = "categoryId";
    public static final String MUSIC_CATEGORY_NAME = "categoryName";
    public static final String MUSIC_CATEGORY_THUMB = "categoryThumb";
    public static final String MUSIC_CATEGORY_IS_FREE = "categoryIsFree";
    public static final String MUSIC_CATEGORY_COST = "categoryCost";
    public static final String MUSIC_CATEGORY_IS_PRUCHESED = "categoryIsPurchesed";
    public static final String MUSIC_SIZE = "size";
    private static final String CREATE_TABLE_MUSIC_CATEGORY = "create table if not exists "
            + TABLE_NAME_MUSIC_CATEGORY
            + "("
            + MUSIC_CATEGORY_ID
            + " Text,"
            + MUSIC_CATEGORY_NAME
            + " Text,"
            + MUSIC_CATEGORY_THUMB
            + " Text,"
            + MUSIC_CATEGORY_IS_FREE
            + " Text,"
            + MUSIC_CATEGORY_COST
            + " Text,"
            + MUSIC_SIZE
            + " Text,"
            + MUSIC_CATEGORY_IS_PRUCHESED
            + " Text);";
    private static final String CREATE_TABLE_MUSIC = "create table if not exists "
            + TABLE_NAME_MUSIC
            + "("
            + MUSIC_ID
            + " Text,"
            + CAT_ID
            + " Text,"
            + MUSIC_NAME
            + " Text,"
            + PIC
            + " Text,"
            + MUSIC_LOCAL_PATH
            + " Text,"
            + MUSIC_LINK
            + " Text);";

    public static final String TABLE_NAME_NOTIFICATION = "notificationTable";
    public static final String NOTIFICATION_ID = "notificationId";
    public static final String NOTIFICATION_MESSAGE = "notificationMessage";
    public static final String NOTIFICATION_TYPE = "type";
    public static final String NOTIFICATION_REF_ID = "referenceId";
    public static final String NOTIFICATION_TIME = "notificationTime";
    public static final String NOTIFICATION_NAME = "notificationName";
    public static final String NOTIFICATION_PIC = "notificationPic";
    private static final String CREATE_TABLE_NOTIFICATION = "create table if not exists "
            + TABLE_NAME_NOTIFICATION
            + "("
            + NOTIFICATION_ID
            + " INTEGER PRIMARY KEY,"
            + USER_ID
            + " Text not null,"
            + NOTIFICATION_MESSAGE
            + " Text not null,"
            + NOTIFICATION_TYPE
            + " Text not null,"
            + NOTIFICATION_REF_ID
            + " Text not null,"
            + NOTIFICATION_TIME
            + " Text not null,"
            + NOTIFICATION_NAME
            + " Text not null," + NOTIFICATION_PIC + " Text not null);";

    public DbSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(createPhContactTable);
        database.execSQL(createAppContactTable);
        database.execSQL(createFriendsTable);
        database.execSQL(createChatMessageTable);
        database.execSQL(CREATE_TABLE_STICKERS);
        database.execSQL(CREATE_TABLE_CATEGORY);
        database.execSQL(CREATE_TABLE_CREDENTAILS);
        database.execSQL(CREATE_TABLE_MUSIC);
        database.execSQL(CREATE_TABLE_MUSIC_CATEGORY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {

        // onCreate(database);
    }

}
