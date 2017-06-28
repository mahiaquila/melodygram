package com.melodygram.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;


import com.melodygram.model.FriendsModel;
import com.melodygram.utils.DateTimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
/**
 * Created by LALIT on 15-06-2016.
 */
public class FriendsDataSource {
    private SQLiteDatabase sqlDatabase;
    private DbSqliteHelper sqlHelper;
    private String[] allColumns = {DbSqliteHelper.friendsUserId,
            DbSqliteHelper.friendsName,
            DbSqliteHelper.friendsMobileNo, DbSqliteHelper.friendsStatus,
            DbSqliteHelper.friendsProfilePic, DbSqliteHelper.friendsRoomId,
            DbSqliteHelper.friendsLastchat, DbSqliteHelper.friendsLastChatTime, DbSqliteHelper.friendsChatType,
            DbSqliteHelper.friendsCountryCode, DbSqliteHelper.friendsLastSeen,
            DbSqliteHelper.friendsIsDeleted, DbSqliteHelper.friendsIsOnline, DbSqliteHelper.unSeenCount,
            DbSqliteHelper.mute, DbSqliteHelper.profilePicPrivacy,
            DbSqliteHelper.lastSeenPrivacy, DbSqliteHelper.statusPrivacy,
            DbSqliteHelper.onlinePrivacy, DbSqliteHelper.BLOCK,DbSqliteHelper.ReadReceipt,DbSqliteHelper.IS_BLOCK};
    private String[] LastChat = {DbSqliteHelper.friendsLastchat};
    Context context;

    public FriendsDataSource(Context context) {
        sqlHelper = new DbSqliteHelper(context);
        this.context = context;
    }

    public void open() throws SQLException {
        sqlDatabase = sqlHelper.getWritableDatabase();
    }

    public void close() {
        sqlHelper.close();
    }

    public Long createFriends(String contactsUserId, String contactsName, String contactsMobileNo,
                              String contactsStatus, String contactsProfilePic,
                              String contactsRoomId,
                              String friendsLastchat,
                              String friendsLastChatTime, String friendsChatType,
                              String contactsCountryCode, String friendsLastSeen, String friendIsOnline,
                              String unseencount, String mute, String profilePicPrivacy,
                              String lastSeenPrivacy, String statusPrivacy, String onlinePrivacy, String block,String readReceipts,String isBlock) {
        long insertId = 0;
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.CHAT_DELETE_FLAG, "1");
        values.put(DbSqliteHelper.friendsUserId, contactsUserId);
        values.put(DbSqliteHelper.friendsName, contactsName);
        values.put(DbSqliteHelper.friendsMobileNo, contactsMobileNo);
        values.put(DbSqliteHelper.friendsStatus, contactsStatus);
        values.put(DbSqliteHelper.friendsProfilePic, contactsProfilePic);
        values.put(DbSqliteHelper.unSeenCount, unseencount);

        values.put(DbSqliteHelper.friendsCountryCode, contactsCountryCode);
        values.put(DbSqliteHelper.friendsLastSeen, friendsLastSeen);
        values.put(DbSqliteHelper.friendsIsOnline, friendIsOnline);
        values.put(DbSqliteHelper.friendsLastchat, friendsLastchat);
        values.put(DbSqliteHelper.friendsRoomId, contactsRoomId);
        values.put(DbSqliteHelper.mute, mute);
        values.put(DbSqliteHelper.profilePicPrivacy, profilePicPrivacy);
        values.put(DbSqliteHelper.lastSeenPrivacy, lastSeenPrivacy);
        values.put(DbSqliteHelper.statusPrivacy, statusPrivacy);
        values.put(DbSqliteHelper.onlinePrivacy, onlinePrivacy);
        values.put(DbSqliteHelper.ReadReceipt, readReceipts);
        values.put(DbSqliteHelper.BLOCK, block);
        values.put(DbSqliteHelper.IS_BLOCK, isBlock);
        String locateRoom = getLocateRoom();
        if (locateRoom != null && locateRoom.equalsIgnoreCase(contactsRoomId)) {
            values.put(DbSqliteHelper.LOCATE_DATE, getUTCDate());
        } else {
            values.put(DbSqliteHelper.LOCATE_DATE, friendsLastChatTime);
        }
        if (isFriendAvailable(contactsUserId)) {
            try {
            String lastChat = getLastChat(contactsRoomId);
                if(lastChat!=null) {
                    String[] lastChatArrayDB = lastChat.split("/");
                    if (lastChat != null && lastChat.length() > 1 && lastChatArrayDB.length > 2) {
                        long lastChatValueDB = Long.valueOf(lastChatArrayDB[2]);
                        String[] lastChatArrayServer = friendsLastchat.split("/");
                        if (lastChatArrayServer.length > 0 && lastChatArrayServer.length > 1 && lastChatArrayDB.length > 1) {
                            long lastChatValueServer = Long.valueOf(lastChatArrayServer[2]);
                            if (lastChatValueServer > lastChatValueDB) {
                                values.put(DbSqliteHelper.friendsIsDeleted, "");
                                values.put(DbSqliteHelper.onlinePrivacy, "0");
                            }
                        }
                    }
                    String lastChatTime = getFriendLastChat(contactsRoomId);
                    if (lastChatTime != null) {
                        if (!compaireTime(friendsLastChatTime, lastChatTime)) {
                            values.put(DbSqliteHelper.friendsLastChatTime, friendsLastChatTime);
                            values.put(DbSqliteHelper.friendsChatType, friendsChatType);
                        }
                    }
                    insertId = sqlDatabase.update(
                            DbSqliteHelper.friendsListTable, values,
                            DbSqliteHelper.friendsUserId + " = " + "'"
                                    + contactsUserId + "'", null);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        } else {
            values.put(DbSqliteHelper.friendsChatType, friendsChatType);
            values.put(DbSqliteHelper.friendsLastChatTime, friendsLastChatTime);
            values.put(DbSqliteHelper.friendsIsDeleted, "");
            insertId = sqlDatabase.insert(DbSqliteHelper.friendsListTable,
                    null, values);
        }
        return insertId;
    }

    public int updateFriendsMessage(String contactsRoomId, String message,
                                    String chatType, String time) {
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.friendsLastchat, message);
        values.put(DbSqliteHelper.friendsRoomId, contactsRoomId);
        SimpleDateFormat inputFormat = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        outputFormat.setTimeZone(TimeZone.getDefault());
        try {
            Date date = inputFormat.parse(time);
            values.put(DbSqliteHelper.friendsLastChatTime,
                    outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String chatMessageType = "";
        if (chatType != null && chatType.equalsIgnoreCase("message")) {
            chatMessageType = message;
        } else if (chatType != null && chatType.equalsIgnoreCase("location")) {
            chatMessageType = "Sent Location";
        } else if (chatType != null && chatType.equalsIgnoreCase("pic")) {
            chatMessageType = "Sent Picture";
        } else if (chatType != null && chatType.equalsIgnoreCase("video")) {
            chatMessageType = "Sent Video file";
        } else if (chatType != null && chatType.equalsIgnoreCase("audio")) {
            chatMessageType = "You sent a voice message ";
        } else if (chatType != null && chatType.equalsIgnoreCase("file")) {
            chatMessageType = "Sent file";
        } else if (chatType != null && chatType.equalsIgnoreCase("music")) {
            chatMessageType = "You sent a voice with music";
        } else if (chatType != null
                && chatType.equalsIgnoreCase("sticker")) {
            chatMessageType = "Sent sticker";
        }
        values.put(DbSqliteHelper.friendsLastchat, chatMessageType);
        int insertId = sqlDatabase.update(DbSqliteHelper.friendsListTable,
                values, DbSqliteHelper.friendsRoomId + " = " + "'"
                        + contactsRoomId + "'", null);
        return insertId;
    }


    public List<FriendsModel> getAllUnDeletedFriends() {


        ContactDataSource contactDataSource = new ContactDataSource(context);
        List<FriendsModel> friendsDetailsList = new ArrayList<>();
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                allColumns, DbSqliteHelper.friendsIsDeleted
                        + " != " + "'" + "1" + "'", null, null, null,
                DbSqliteHelper.LOCATE_DATE + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FriendsModel friendsModel = cursorToFriendsList(cursor,
                    contactDataSource);
            friendsDetailsList.add(friendsModel);
            cursor.moveToNext();
        }
        cursor.close();
        return friendsDetailsList;
    }


    private boolean compaireTime(String serverTime, String localTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter2 = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        try {
            Date date1 = formatter2.parse(localTime);
            Date date2 = formatter.parse(serverTime);
            if (date1.compareTo(date2) < 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public String getFriendLastChat(String roomId) {
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                new String[]{DbSqliteHelper.friendsLastChatTime},
                DbSqliteHelper.friendsRoomId + " = " + "'" + roomId + "'",
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            return cursor.getString(0);
        }
        return null;
    }

    public List<FriendsModel> getAllBlockedFriends() {
        ContactDataSource contactDataSource = new ContactDataSource(context);
        List<FriendsModel> friendsDetailsList = new ArrayList<>();
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                allColumns, DbSqliteHelper.BLOCK
                        + " = " + "'" + "1" + "'", null, null, null,
                DbSqliteHelper.LOCATE_DATE + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            FriendsModel friendsModel = cursorToFriendsList(cursor,
                    contactDataSource);
            friendsDetailsList.add(friendsModel);
            cursor.moveToNext();
        }
        cursor.close();
        return friendsDetailsList;
    }


    public void deleteRowMessage(String chatMessageRoomId)
    {
        sqlDatabase.delete(DbSqliteHelper.friendsListTable,
                DbSqliteHelper.friendsRoomId + " = " + "'"
                        + chatMessageRoomId +  "'", null);
    }

    public boolean isFriendAvailable(String id) {
        String[] friendsId = {DbSqliteHelper.friendsUserId};
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                friendsId, DbSqliteHelper.friendsUserId + " = " + "'" + id
                        + "'", null, null, null, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;

    }


    private FriendsModel cursorToFriendsList(Cursor cursor,
                                             ContactDataSource contactDataSource) {
        FriendsModel friendsModel = new FriendsModel();
        friendsModel.setFriendsUserId(cursor.getString(0));
        friendsModel.setFriendAppName(cursor.getString(1));
        friendsModel.setFriendsPhoneNumber(cursor.getString(2));
        friendsModel.setFriendsStatus(cursor.getString(3));
        friendsModel.setFriendsPicIconUrl(cursor.getString(4));
        friendsModel.setFriendsRoomId(cursor.getString(5));
        if (cursor.getString(6).contains("/"))
            friendsModel.setFriendsLastchat(cursor.getString(6).split("/")[0]);
        else
            friendsModel.setFriendsLastchat(cursor.getString(6));
        friendsModel.setFriendsLastChatTime(cursor.getString(7));
        if (cursor.getString(8).equals("onetoone")) {
            String phoneName = contactDataSource.getContactName(cursor
                    .getString(2));
            if (phoneName != null)
                friendsModel.setFriendName(phoneName);
            else
                friendsModel.setFriendName(cursor.getString(2));
        } else {
            friendsModel.setFriendName(cursor.getString(1));
        }
        friendsModel.setFriendsChatType(cursor.getString(8));
        friendsModel.setFriendsCountryCode(cursor.getString(9));
        friendsModel.setFriendsLastSeen(cursor.getString(10));
        friendsModel.setFriendsIsDeleted(cursor.getString(11));
        friendsModel.setFriendsOnline(cursor.getString(12));
        friendsModel.setSeenCount(cursor.getString(13));
        friendsModel.setMute(cursor.getString(14));
        friendsModel.setProfilePicPrivacy(cursor.getString(15));
        friendsModel.setLastSeenPrivacy(cursor.getString(16));
        friendsModel.setStatusPrivacy(cursor.getString(17));
        friendsModel.setOnlinePrivacy(cursor.getString(18));
        friendsModel.setFriendsChatBlocked(cursor.getString(19));
        friendsModel.setReadReceiptsPrivacy(cursor.getString(20));
        friendsModel.setIsBlocked(cursor.getString(21));
        return friendsModel;
    }

    public void setLocate(String friendsRoomId) {
        String locateQuery = "UPDATE " + DbSqliteHelper.friendsListTable + " SET " + DbSqliteHelper.LOCATE_TOP + " = 0 WHERE " + DbSqliteHelper.friendsRoomId + " != " + "'" + friendsRoomId + "'";
        sqlDatabase.execSQL(locateQuery);
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.LOCATE_TOP, 1);
        values.put(DbSqliteHelper.LOCATE_DATE, getUTCDate());
        sqlDatabase.update(DbSqliteHelper.friendsListTable,
                values, DbSqliteHelper.friendsRoomId + " = " + "'" + friendsRoomId
                        + "'", null);
    }

    public int updateBlock(String userId, String value) {
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.BLOCK, value);
        int insertId = sqlDatabase.update(DbSqliteHelper.friendsListTable,
                values, DbSqliteHelper.friendsUserId + " = " + "'" + userId
                        + "'", null);
        return insertId;
    }

    public int updateDeletedFriend(String userId, String friendsChatIsDeleted) {
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.friendsIsDeleted, friendsChatIsDeleted);
        int insertId = sqlDatabase.update(DbSqliteHelper.friendsListTable,
                values, DbSqliteHelper.friendsUserId + " = " + "'" + userId
                        + "'", null);
        return insertId;
    }

    public String getLastChat(String roomId) {
        String value = null;
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                LastChat, DbSqliteHelper.friendsRoomId + " = " + "'" + roomId
                        + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            value = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return value;
    }

    public String getLocateRoom() {
        String value = null;
        String[] block = {DbSqliteHelper.friendsRoomId};
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                block, DbSqliteHelper.LOCATE_TOP + " = " + "'" + 1
                        + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            value = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return value;
    }
    public String getBlockValue(String roomId) {
        String value = "0";
        String[] block = {DbSqliteHelper.BLOCK};
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                block, DbSqliteHelper.friendsRoomId + " = " + "'" + roomId
                        + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            value = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return value;
    }
    public String getISBlockValue(String roomId) {
        String value = "0";
        String[] block = {DbSqliteHelper.IS_BLOCK};
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                block, DbSqliteHelper.friendsRoomId + " = " + "'" + roomId
                        + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            value = cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();
        return value;
    }
    public void updateBatchCount(String roomId) {
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.unSeenCount, "0");
        sqlDatabase
                .update(DbSqliteHelper.friendsListTable, values,
                        DbSqliteHelper.friendsRoomId + " = " + "'" + roomId
                                + "'", null);
    }


    public FriendsModel getFriendsDetails(String friendId) {
        FriendsModel friendsModel=null;
        ContactDataSource contactDataSource = new ContactDataSource(context);
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                allColumns, DbSqliteHelper.friendsUserId + " = " + "'"
                        + friendId + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
             friendsModel = cursorToFriendsList(cursor,
                    contactDataSource);
            cursor.moveToNext();

        }
        cursor.close();
        return friendsModel;
    }

    public void updateChatDeleteFlag() {
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.CHAT_DELETE_FLAG, "0");
        sqlDatabase.update(DbSqliteHelper.friendsListTable, values, null, null);

    }

    public ArrayList<String> getAllRoomId() {
        String[] roomId = {DbSqliteHelper.friendsRoomId};
        ArrayList<String> friendsDetailsList = new ArrayList<>();
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                roomId, null, null, null, null,
                null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            friendsDetailsList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return friendsDetailsList;
    }

    private String getUTCDate() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(new Date());
    }

    public FriendsModel getFriendAvailable(String roomId) {
        ContactDataSource contactDataSource = new ContactDataSource(context);
        FriendsModel friendsModel = null;
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.friendsListTable,
                allColumns, DbSqliteHelper.friendsRoomId + " = " + "'" + roomId
                        + "'", null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            friendsModel = cursorToFriendsList(cursor, contactDataSource);

            cursor.moveToNext();
        }
        cursor.close();
        return friendsModel;
    }
}
