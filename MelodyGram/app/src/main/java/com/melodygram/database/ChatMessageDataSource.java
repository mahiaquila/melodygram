package com.melodygram.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.melodygram.constants.ChatGlobalStates;
import com.melodygram.model.ChatMessageModel;
import com.melodygram.singleton.AppController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ChatMessageDataSource {


	private SQLiteDatabase sqlDatabase;
	private DbSqliteHelper sqlHelper;
	private String userId;
    private Context context;

	private String[] allColumns = { DbSqliteHelper.chatPrimaryKey,
			DbSqliteHelper.chatMessageId,
			DbSqliteHelper.chatMessageSenderName,
			DbSqliteHelper.chatMessageSenderPic, DbSqliteHelper.chatMessageRoomId,
			DbSqliteHelper.chatMessageUserId, DbSqliteHelper.chatMessage,
			DbSqliteHelper.chatMessageType, DbSqliteHelper.chatMessageFileUrl,
			DbSqliteHelper.chatMessageLat, DbSqliteHelper.chatMessageLan,
			DbSqliteHelper.chatMessageUserTime,
			DbSqliteHelper.chatMessageUserDate,
			DbSqliteHelper.chatMessageToFromFlag,
			DbSqliteHelper.chatMessageSingleTickFlag, DbSqliteHelper.chatMessageSeen,
			DbSqliteHelper.chatMessageSeenTime,
			DbSqliteHelper.chatFileThumbnail, DbSqliteHelper.chatIsRemoved,
			DbSqliteHelper.chatIsSent, DbSqliteHelper.chatTempMessageId,
			DbSqliteHelper.chatDisappear,DbSqliteHelper.chatAudioTime ,DbSqliteHelper.stickerId,DbSqliteHelper.CHAT_IS_EDIT_SENT,DbSqliteHelper.imageDownload};

	public ChatMessageDataSource(Context context) {
		sqlHelper = new DbSqliteHelper(context);
		userId = AppController.getInstance().getUserId();
		this.context = context;
	}

	public void open() throws SQLException {
		sqlDatabase = sqlHelper.getWritableDatabase();
	}

	public void close() {
		sqlHelper.close();
	}

	public Long createChatMessage(String chatMessageId,
			String chatMessageSenderName, String chatMessageSenderPic,
			String chatMessageRoomId, String chatMessageUserId,
			String chatMessage, String chatMessageType,
			String chatMessageFileUrl, String chatMessageLat,
			String chatMessageLan, String chatMessageUserTime,
			String chatMessageUserDate, String chatMessageToFromFlag,
			String chatMessageSingleTickFlag,
			String chatMessageSeen, String chatMessageSeenTime,
			String chatFileThumbnail,
			String chatIsRemoved, String chatIsSent, String chatTempMessageId,
			String chatAudioTime,String disappear,String stickerId,String chatIsEditSent,int imageDownload) {

		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId, Integer.valueOf(chatMessageId));
		values.put(DbSqliteHelper.chatMessageSenderName, chatMessageSenderName);
		values.put(DbSqliteHelper.chatMessageSenderPic, chatMessageSenderPic);
		values.put(DbSqliteHelper.chatMessageRoomId, chatMessageRoomId);
		values.put(DbSqliteHelper.chatMessageUserId, chatMessageUserId);
		values.put(DbSqliteHelper.chatMessage, chatMessage);
		values.put(DbSqliteHelper.chatMessageType, chatMessageType);

		values.put(DbSqliteHelper.chatMessageFileUrl, chatMessageFileUrl);
		values.put(DbSqliteHelper.chatMessageLat, chatMessageLat);
		values.put(DbSqliteHelper.chatMessageLan, chatMessageLan);
		values.put(DbSqliteHelper.chatMessageUserTime, chatMessageUserTime);
		values.put(DbSqliteHelper.chatMessageUserDate, chatMessageUserDate);
		values.put(DbSqliteHelper.chatMessageToFromFlag, chatMessageToFromFlag);
		values.put(DbSqliteHelper.chatMessageSingleTickFlag,
				chatMessageSingleTickFlag);
		values.put(DbSqliteHelper.chatMessageSeen, chatMessageSeen);
		values.put(DbSqliteHelper.chatMessageSeenTime, chatMessageSeenTime);
		values.put(DbSqliteHelper.chatFileThumbnail, chatFileThumbnail);
		values.put(DbSqliteHelper.chatIsRemoved, chatIsRemoved);
		values.put(DbSqliteHelper.chatIsSent, chatIsSent);
		values.put(DbSqliteHelper.chatTempMessageId, chatTempMessageId);
		values.put(DbSqliteHelper.chatAudioTime, chatAudioTime);
		values.put(DbSqliteHelper.chatDisappear, disappear);
		values.put(DbSqliteHelper.stickerId, stickerId);
		values.put(DbSqliteHelper.CHAT_IS_EDIT_SENT, chatIsEditSent);
		values.put(DbSqliteHelper.imageDownload, imageDownload);
		Long insertId = sqlDatabase.insert(DbSqliteHelper.chatMessageTable,
				null, values);
		ChatMessageModel chatMessageModel = getLatestMsgContent(chatMessageRoomId);
		if(chatMessageModel !=null)
		{
			FriendsDataSource friendsDataSource = new FriendsDataSource(context);
			friendsDataSource.open();
			friendsDataSource.updateFriendsMessage(chatMessageRoomId, chatMessageModel.getActualMsg(), chatMessageModel.getMessageType(), chatMessageModel.getMsgTime());
			friendsDataSource.close();
		}

		return insertId;
	}
	public boolean isMsgAvailable(String messageId) {
		Cursor cursor = sqlDatabase.query(
				DbSqliteHelper.chatMessageTable, allColumns,
				DbSqliteHelper.chatMessageId + " = " + "'"
						+ messageId + "'", null, null, null, null);
		return cursor.moveToFirst();
	}

	public ChatMessageModel getLatestMsgContent(String roomId)
	{
		ChatMessageModel chatMessageModel = null;
		Cursor cursor = sqlDatabase.rawQuery("SELECT "+ DbSqliteHelper.chatMessage+","+DbSqliteHelper.chatMessageType+","+DbSqliteHelper.chatMessageUserTime+ " FROM " + DbSqliteHelper.chatMessageTable + " WHERE  "+DbSqliteHelper.chatMessageRoomId + " = " + "'" + roomId + "' ORDER BY "+DbSqliteHelper.chatPrimaryKey+
				" DESC LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0)
		{
			cursor.moveToFirst();
			chatMessageModel = new ChatMessageModel();
			chatMessageModel.setActualMsg(cursor.getString(0));
			chatMessageModel.setMessageType(cursor.getString(1));
			chatMessageModel.setMsgTime(cursor.getString(2));
			return chatMessageModel;
		}
		else
			return chatMessageModel;
	}

	public int updateEditMsg(String chatMessageId, String actualMsg,String chatIsEditSent) {
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId,
				Integer.parseInt(chatMessageId));
		values.put(DbSqliteHelper.chatMessage, actualMsg);
		values.put(DbSqliteHelper.CHAT_IS_EDIT_SENT, chatIsEditSent);
		int insertId = sqlDatabase.update(
				DbSqliteHelper.chatMessageTable, values,
				DbSqliteHelper.chatMessageId + " = " + "'"
						+ chatMessageId + "'", null);
		return insertId;
	}

	public int updateChatMessage(String chatMessageId,
			String chatMessageSenderName, String chatMessageSenderPic,
			String chatMessageRoomId, String chatMessageUserId,
			String chatMessage, String chatMessageType,
			String chatMessageFileUrl, String chatMessageLat,
			String chatMessageLan, String chatMessageUserTime,
			String chatMessageUserDate, String chatMessageToFromFlag,
			String chatMessageSingleTickFlag,
			String chatMessageSeen, String chatMessageSeenTime, String chatIsRemoved, String chatIsSent,
			String chatTempMessageId, String chatAudioTime,String disappear,String stickerId,String chatEditisSent,int imageDownload) {
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId, Integer.valueOf(chatMessageId));
		values.put(DbSqliteHelper.chatMessageSenderName, chatMessageSenderName);
		values.put(DbSqliteHelper.chatMessageSenderPic, chatMessageSenderPic);
		values.put(DbSqliteHelper.chatMessageRoomId, chatMessageRoomId);
		values.put(DbSqliteHelper.chatMessageUserId, chatMessageUserId);
		values.put(DbSqliteHelper.chatMessage, chatMessage);
		values.put(DbSqliteHelper.chatMessageType, chatMessageType);
		values.put(DbSqliteHelper.chatMessageFileUrl, chatMessageFileUrl);
		values.put(DbSqliteHelper.chatMessageLat, chatMessageLat);
		values.put(DbSqliteHelper.chatMessageLan, chatMessageLan);
		values.put(DbSqliteHelper.chatMessageUserTime, chatMessageUserTime);
		values.put(DbSqliteHelper.chatMessageUserDate, chatMessageUserDate);
		values.put(DbSqliteHelper.chatMessageToFromFlag, chatMessageToFromFlag);
		values.put(DbSqliteHelper.chatMessageSingleTickFlag,
				chatMessageSingleTickFlag);
		values.put(DbSqliteHelper.chatMessageSeen, chatMessageSeen);
		values.put(DbSqliteHelper.chatMessageSeenTime, chatMessageSeenTime);
		values.put(DbSqliteHelper.chatDisappear, disappear);
		values.put(DbSqliteHelper.chatIsRemoved, chatIsRemoved);
		values.put(DbSqliteHelper.chatIsSent, chatIsSent);
		values.put(DbSqliteHelper.chatTempMessageId, chatTempMessageId);
		values.put(DbSqliteHelper.chatAudioTime, chatAudioTime);
		values.put(DbSqliteHelper.stickerId, stickerId);
		values.put(DbSqliteHelper.CHAT_IS_EDIT_SENT, chatEditisSent);
		values.put(DbSqliteHelper.imageDownload, imageDownload);
		int insertId;
		if (chatMessageUserId.equals(userId)) {
			insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
					values, DbSqliteHelper.chatTempMessageId + " = '"
							+ chatTempMessageId + "' AND "
							+ DbSqliteHelper.chatMessageUserId + " = '"
							+ chatMessageUserId + "' AND "
							+ DbSqliteHelper.chatMessageRoomId + " = " + "'"
							+ chatMessageRoomId + "'", null);
		} else {
			insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
					values, DbSqliteHelper.chatMessageId + " = "
							+ chatMessageId + " AND "
							+ DbSqliteHelper.chatMessageUserId + " = '"
							+ chatMessageUserId + "' AND "
							+ DbSqliteHelper.chatMessageRoomId + " = " + "'"
							+ chatMessageRoomId + "'", null);
		}
		return insertId;
	}

	public int updateMediaUrl(String chatMessageId, String chatMessageFileUrl) {
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId, chatMessageId);
		values.put(DbSqliteHelper.chatMessageFileUrl, chatMessageFileUrl);

		int insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
				values, DbSqliteHelper.chatMessageId + " = " + "'"
						+ chatMessageId + "'", null);
		return insertId;
	}


	public int updateDownloadImage(String chatMessageId, int imageDownLoadId) {
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId, chatMessageId);
		values.put(DbSqliteHelper.imageDownload, imageDownLoadId);

		int insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
				values, DbSqliteHelper.chatMessageId + " = " + "'"
						+ chatMessageId + "'", null);
		return insertId;
	}


	public int updateLastseen(String chatMessageSeenId,
			String chatMessageSeenTime) {
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId, chatMessageSeenId);
		values.put(DbSqliteHelper.chatMessageSeen, "1");
		values.put(DbSqliteHelper.chatMessageSeenTime, chatMessageSeenTime);

		int insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
				values, DbSqliteHelper.chatMessageId + " = " + "'"
						+ chatMessageSeenId + "'", null);
		return insertId;
	}

	public String getLatestMsgId(String chatMessageRoomId)
	{
		Cursor cursor = sqlDatabase.rawQuery("SELECT "
				+ DbSqliteHelper.chatMessageId + " FROM "
				+ DbSqliteHelper.chatMessageTable + " where "
				+ DbSqliteHelper.chatMessageRoomId + " = '" + chatMessageRoomId
				+ "'" + " AND " + DbSqliteHelper.chatIsSent + " = '1' ORDER BY " + DbSqliteHelper.chatMessageId + " DESC LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			if(cursor.getString(0)!=null)
			{
				return cursor.getString(0);
			}
		}
			return "-1";

	}


	public String getLatestUnDeletedMsgId(String chatMessageRoomId) {
		Cursor cursor = sqlDatabase.rawQuery("SELECT "
				+ DbSqliteHelper.chatMessageId + " FROM "
				+ DbSqliteHelper.chatMessageTable + " where "
				+ DbSqliteHelper.chatMessageRoomId + " = '" + chatMessageRoomId
				+ "'" + " AND " + DbSqliteHelper.chatIsRemoved + " = '" + ""
				+ "'" + " order by " + DbSqliteHelper.chatMessageId + " DESC LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			if(cursor.getString(0)!=null)
			{
				return cursor.getString(0);
			}
		}

		return "-1";

	}

	public ChatMessageModel getLatestMsg(String chatMessageRoomId) {

		ChatMessageModel chatMessageModel=null;

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.chatMessageTable,
				allColumns, DbSqliteHelper.chatMessageRoomId + " = " + "'"
						+ chatMessageRoomId + "' AND "
						+ DbSqliteHelper.chatIsRemoved + " = '" + "" + "'",
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			 chatMessageModel = cursorToChatMessage(cursor);

			cursor.moveToNext();
		}

		cursor.close();
		return chatMessageModel;



	}

	public String getUnseenMsgId(String chatMessageRoomId) {

		Cursor cursor = sqlDatabase.rawQuery("SELECT "
				+ DbSqliteHelper.chatMessageId +  " FROM "
				+ DbSqliteHelper.chatMessageTable + " where "
				+ DbSqliteHelper.chatMessageRoomId + " = '" + chatMessageRoomId
				+ "'" + " AND " + DbSqliteHelper.chatMessageSeen + " != '1' ORDER BY "+DbSqliteHelper.chatMessageId + " ASC LIMIT 1", null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			if(cursor.getString(0)!=null)
			{
				return cursor.getString(0);
			}
		}
		return "-1";

	}

	public List<ChatMessageModel> getAllChatMessage(String chatMessageRoomId) {
		List<ChatMessageModel> chatMessageModelList = new ArrayList<ChatMessageModel>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.chatMessageTable,
				allColumns, DbSqliteHelper.chatMessageRoomId + " = " + "'"
						+ chatMessageRoomId + "' AND "
						+ DbSqliteHelper.chatIsRemoved + " = '" + "" + "'",
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ChatMessageModel chatMessageModel = cursorToChatMessage(cursor);
			chatMessageModelList.add(chatMessageModel);
			cursor.moveToNext();
		}

		cursor.close();
		return chatMessageModelList;
	}


	public ArrayList<ChatMessageModel> getAllChatTextMessage(String chatType)
	{
		ArrayList<ChatMessageModel> chatMessageModelList = new ArrayList<>();
		Cursor cursor = sqlDatabase.query(DbSqliteHelper.chatMessageTable, allColumns, DbSqliteHelper.chatMessageType + " = " + "'" + chatType + "' AND " + DbSqliteHelper.chatIsRemoved
				+ " = '" + "" + "'", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ChatMessageModel chatMessageModel = cursorToChatMessage(cursor);
			chatMessageModelList.add(chatMessageModel);
			cursor.moveToNext();
		}

		cursor.close();
		return chatMessageModelList;
	}

	public LinkedHashMap<String, ChatMessageModel> getAllUnsentMsg(
			String chatMessageRoomId) {
		LinkedHashMap<String, ChatMessageModel> unsentItemList = new LinkedHashMap<String, ChatMessageModel>();
		Cursor cursor = sqlDatabase.query(DbSqliteHelper.chatMessageTable,
				allColumns, DbSqliteHelper.chatMessageRoomId + " = " + "'"
						+ chatMessageRoomId + "' AND "
						+ DbSqliteHelper.chatIsRemoved + " = '" + "" + "'"
						+ " AND " + DbSqliteHelper.chatIsSent + " = '" + "0"
						+ "'", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ChatMessageModel chatMessageModel = cursorToChatMessage(cursor);
			unsentItemList.put(cursor.getString(20), chatMessageModel);
			cursor.moveToNext();
		}
		cursor.close();
		return unsentItemList;
	}

	public List<ChatMessageModel> getLimitedChatMessage(
			String chatMessageRoomId) {
		List<ChatMessageModel> chatMessageModelList = new ArrayList<ChatMessageModel>();

		String rawQuery = "select * from (select * from "
				+ DbSqliteHelper.chatMessageTable + " where "
				+ DbSqliteHelper.chatMessageRoomId + " = '" + chatMessageRoomId
				+ "' AND " + DbSqliteHelper.chatIsRemoved + " = '" + "" + "'"
				+ " order by " + DbSqliteHelper.chatPrimaryKey + " DESC ) order by " + DbSqliteHelper.chatPrimaryKey
				+ " ASC";

		Cursor cursor = sqlDatabase.rawQuery(rawQuery, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ChatMessageModel chatMessageModel = cursorToChatMessage(cursor);
			chatMessageModelList.add(chatMessageModel);
			cursor.moveToNext();
		}
		cursor.close();
		return chatMessageModelList;
	}

	private ChatMessageModel cursorToUserId(Cursor cursor) {
		ChatMessageModel chatMessageModel = new ChatMessageModel();

		chatMessageModel.setMsgId(cursor.getString(0));
		return chatMessageModel;
	}

	public Integer getFstMsgPrimaryKey(String chatMessageId,
			boolean isDummyMsgId) {
		Cursor cursor;
		if (isDummyMsgId) {
			cursor = sqlDatabase.rawQuery("SELECT "
					+ DbSqliteHelper.chatPrimaryKey + " FROM "
					+ DbSqliteHelper.chatMessageTable + " where "
					+ DbSqliteHelper.chatTempMessageId + " = '" + chatMessageId
					+ "'", null);
		} else {
			cursor = sqlDatabase.rawQuery("SELECT "
					+ DbSqliteHelper.chatPrimaryKey + " FROM "
					+ DbSqliteHelper.chatMessageTable + " where "
					+ DbSqliteHelper.chatMessageId + " = '" + chatMessageId
					+ "'", null);
		}

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getInt(0);
		} else
			return null;
	}

	public List<ChatMessageModel> getLimitedChatMessage(
			String chatMessageRoomId, String chatMessageId,
			boolean isDummyMsgId) {
		List<ChatMessageModel> chatMessageModelList = new ArrayList<ChatMessageModel>();

		Integer fstMsgPrimaryKey = getFstMsgPrimaryKey(chatMessageId,
				isDummyMsgId);
		if (fstMsgPrimaryKey != null) {
			String rawQuery = "select * from (select * from "
					+ DbSqliteHelper.chatMessageTable + " where "
					+ DbSqliteHelper.chatMessageRoomId + " = '"
					+ chatMessageRoomId + "' AND "
					+ DbSqliteHelper.chatPrimaryKey + " < '" + fstMsgPrimaryKey
					+ "' AND " + DbSqliteHelper.chatIsRemoved + " = '" + ""
					+ "'" + " order by " + DbSqliteHelper.chatPrimaryKey
					+ " DESC limit 20 ) order by "
					+ DbSqliteHelper.chatPrimaryKey + " ASC";


			Cursor cursor = sqlDatabase.rawQuery(rawQuery, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				ChatMessageModel chatMessageModel = cursorToChatMessage(cursor);
				chatMessageModelList.add(chatMessageModel);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return chatMessageModelList;
	}

	public List<ChatMessageModel> getChatMessageAvailable(
			String chatMessageUserId, String chatMessageRoomId,
			String chatTempMessageId) {
		List<ChatMessageModel> chatMessageModelList = new ArrayList<ChatMessageModel>();
		Cursor cursor = null;
		if (chatMessageUserId.equals(userId))
			cursor = sqlDatabase.query(DbSqliteHelper.chatMessageTable,
					allColumns, DbSqliteHelper.chatTempMessageId + " = " + "'"
							+ chatTempMessageId + "' AND "
							+ DbSqliteHelper.chatMessageUserId + " = "
							+ chatMessageUserId + " AND "
							+ DbSqliteHelper.chatMessageRoomId + " = " + "'"
							+ chatMessageRoomId + "'", null, null, null, null);
		else
			cursor = sqlDatabase.query(DbSqliteHelper.chatMessageTable,
					allColumns, DbSqliteHelper.chatMessageId + " = " + "'"
							+ chatTempMessageId + "' AND "
							+ DbSqliteHelper.chatMessageUserId + " = "
							+ chatMessageUserId + " AND "
							+ DbSqliteHelper.chatMessageRoomId + " = " + "'"
							+ chatMessageRoomId + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ChatMessageModel chatMessageModel = cursorToChatMessage(cursor);
			chatMessageModelList.add(chatMessageModel);
			cursor.moveToNext();
		}

		cursor.close();
		return chatMessageModelList;
	}

	public boolean isMsgAvailable(String messageId,String roomId) {
		String[] column = { DbSqliteHelper.chatMessageId };
		Cursor cursor = sqlDatabase.query(DbSqliteHelper.chatMessageTable,
				column, DbSqliteHelper.chatMessageId + " = " + "'" + messageId
						+ "' AND "
						+ DbSqliteHelper.chatMessageRoomId + "!= " + "'"
						+ roomId + "'", null, null, null, null);
		if (cursor.getCount() > 0)
			return true;
		else
			return false;

	}

	public void deleteAllChatMessage(String chatMessageRoomId) {


		String lastMessageId = getLatestMsgId(chatMessageRoomId);
		sqlDatabase.delete(DbSqliteHelper.chatMessageTable,
				DbSqliteHelper.chatMessageRoomId + " = " + "'"
						+ chatMessageRoomId + "' AND "
						+ DbSqliteHelper.chatMessageId + "!= " + "'"
						+ lastMessageId + "'", null);
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId, lastMessageId);
		values.put(DbSqliteHelper.chatMessageSeen, "1");
		values.put(DbSqliteHelper.chatIsRemoved, "1");
		sqlDatabase.update(DbSqliteHelper.chatMessageTable, values,
				DbSqliteHelper.chatMessageId + " = " + "'" + lastMessageId
						+ "'", null);

	}



	public int deleteSelectedChatMessage(String chatMessageId) {

		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatMessageId, chatMessageId);
		values.put(DbSqliteHelper.chatIsRemoved, "1");
		int insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
				values, DbSqliteHelper.chatMessageId + " = " + "'"
						+ chatMessageId + "'", null);
		return insertId;
	}



	public int noOfRows(String chatMessageRoomId) {
		Cursor mCount = sqlDatabase.rawQuery("select count(*) from "
				+ (DbSqliteHelper.chatMessageTable + " where "
						+ DbSqliteHelper.chatMessageRoomId + "='"
						+ chatMessageRoomId + "' AND "
						+ DbSqliteHelper.chatIsRemoved + " = '" + "" + "'"),
				null);

		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	public int noOfMessages(String chatMessageRoomId) {
		Cursor mCount = sqlDatabase.rawQuery("select count(*) from "
				+ (DbSqliteHelper.chatMessageTable + " where "
						+ DbSqliteHelper.chatMessageRoomId + "='"
						+ chatMessageRoomId + "'"), null);
		mCount.moveToFirst();
		int count = mCount.getInt(0);
		mCount.close();
		return count;
	}

	private ChatMessageModel cursorToChatMessage(Cursor cursor) {
		ChatMessageModel chatMessageModel = new ChatMessageModel();
		chatMessageModel.setMsgId(cursor.getString(1));
		chatMessageModel.setSenderName(cursor.getString(2));
		chatMessageModel.setProfileImgUrl(cursor.getString(3));
		chatMessageModel.setMessageRoomId(cursor.getString(4));
		chatMessageModel.setUserId(cursor.getString(5));
		chatMessageModel.setActualMsg(cursor.getString(6));
		chatMessageModel.setMessageType(cursor.getString(7));
		chatMessageModel.setPostImgUrl(cursor.getString(8));
		chatMessageModel.setLatitude(cursor.getString(9));
		chatMessageModel.setLangitude(cursor.getString(10));
		chatMessageModel.setMsgTime(cursor.getString(11));
		chatMessageModel.setChatMessageSeen(cursor.getString(15));
		chatMessageModel.setChatMessageSeenTime(cursor.getString(16));
		chatMessageModel.setChatFileThumbnail(cursor.getString(17));
		chatMessageModel.setChatIsRemoved(cursor.getString(18));
		chatMessageModel.setChatIsSent(cursor.getString(19));
		chatMessageModel.setChatTempMessageId(cursor.getString(20));
		chatMessageModel.setChatAudioTime(cursor.getString(22));
		chatMessageModel.setChatDisappear(cursor.getString(21));
		chatMessageModel.setChatStickerAudioBuzzId(cursor.getString(23));
		chatMessageModel.setChatEditMsg(cursor.getString(24));
		chatMessageModel.setImageDownlaod(cursor.getInt(25));

		return chatMessageModel;
	}

	public int updatePicLocalPath(String path, String msgId) {
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatFileThumbnail, path);
		int insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
				values, DbSqliteHelper.chatMessageId + " = " + "'" + msgId
						+ "'", null);
		return insertId;
	}

	public int updateAudioVideo(String msgId) {
		ContentValues values = new ContentValues();
		values.put(DbSqliteHelper.chatIsSent, "1");
		int insertId = sqlDatabase.update(DbSqliteHelper.chatMessageTable,
				values, DbSqliteHelper.chatTempMessageId + " = " + "'" + msgId
						+ "'", null);
		return insertId;
	}

	public long getNextChatMessageCount(String chatMessageRoomId,
			String chatMessageId, boolean isDummyMsgId) {
		long count = 0;
		Integer fstMsgPrimaryKey = getFstMsgPrimaryKey(chatMessageId,
				isDummyMsgId);
		if (fstMsgPrimaryKey != null) {
			String rawQuery = "select COUNT(*) from (select * from "
					+ DbSqliteHelper.chatMessageTable + " where "
					+ DbSqliteHelper.chatMessageRoomId + " = '"
					+ chatMessageRoomId + "' AND "
					+ DbSqliteHelper.chatPrimaryKey + " < '" + fstMsgPrimaryKey
					+ "' AND " + DbSqliteHelper.chatIsRemoved + " = '" + ""
					+ "'" + " order by " + DbSqliteHelper.chatPrimaryKey
					+ " DESC limit " + 0 + ") order by "
					+ DbSqliteHelper.chatPrimaryKey + " ASC";
			Cursor cursor = sqlDatabase.rawQuery(rawQuery, null);
			if (cursor.moveToFirst()) {
				count = cursor.getLong(0);
			}

			cursor.close();
		}
		return count;
	}

}
