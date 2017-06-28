package com.melodygram.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.melodygram.model.ChatSticker;
import com.melodygram.model.ChatStickersListBean;

import java.util.ArrayList;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ChatStickersDataSource
{

	private SQLiteDatabase sqlDatabase;
	private DbSqliteHelper sqlHelper;
	private String[] allColumns =
	{ DbSqliteHelper.STICKERS_ID, DbSqliteHelper.STICKERS_NAME, DbSqliteHelper.STICKERS_PIC, DbSqliteHelper.STICKERS_CATEGORY_ID };

	public ChatStickersDataSource(Context context)
	{
		sqlHelper = new DbSqliteHelper(context);
	}

	public void open() throws SQLException
	{
		sqlDatabase = sqlHelper.getWritableDatabase();
	}

	public void close()
	{
		sqlHelper.close();
	}

	public Long createStickers(String stickersId, String stickersName, String stickersPic, String stickersCategoryId)
	{
		ContentValues values = new ContentValues();

		values.put(DbSqliteHelper.STICKERS_ID, stickersId);
		values.put(DbSqliteHelper.STICKERS_NAME, stickersName);
		values.put(DbSqliteHelper.STICKERS_PIC, stickersPic);
		values.put(DbSqliteHelper.STICKERS_CATEGORY_ID, stickersCategoryId);

		Long insertId = sqlDatabase.insert(DbSqliteHelper.TABLE_NAME_STICKERS, null, values);

		return insertId;
	}

	public Long createStickers(ContentValues values)
	{

		Long insertId = sqlDatabase.insert(DbSqliteHelper.TABLE_NAME_STICKERS, null, values);

		return insertId;
	}

	public ArrayList<ChatSticker> getAllStickersDetails(String catId)
	{
		ArrayList<ChatSticker> chatStickersArrayList = new ArrayList<ChatSticker>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_STICKERS, allColumns, DbSqliteHelper.STICKERS_CATEGORY_ID + " = " + "'" + catId + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ChatSticker chatStickersListBean = new ChatSticker();
			chatStickersListBean.setStickersId(cursor.getString(0));
			chatStickersListBean.setStickersName(cursor.getString(1));
			chatStickersListBean.setStickersPic(cursor.getString(2));
			chatStickersListBean.setCategoryId(cursor.getString(3));
			chatStickersListBean.setIsDownloaded(true);
			chatStickersArrayList.add(chatStickersListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return chatStickersArrayList;
	}

	public int updateStickers(String stickersId, String stickersName, String stickersPic, String stickersCategoryId)
	{
		ContentValues values = new ContentValues();

		values.put(DbSqliteHelper.STICKERS_ID, stickersId);
		values.put(DbSqliteHelper.STICKERS_NAME, stickersName);
		values.put(DbSqliteHelper.STICKERS_PIC, stickersPic);
		values.put(DbSqliteHelper.STICKERS_CATEGORY_ID, stickersCategoryId);

		int insertId = sqlDatabase.update(DbSqliteHelper.TABLE_NAME_STICKERS, values, DbSqliteHelper.STICKERS_ID + " = " + "'" + stickersId + "'", null);

		return insertId;
	}

	public ArrayList<ChatSticker> getAllStickerss()
	{
		ArrayList<ChatSticker> chatStickersArrayList = new ArrayList<ChatSticker>();
		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_STICKERS, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{

			ChatSticker chatStickersListBean = new ChatSticker();
			chatStickersListBean.setStickersId(cursor.getString(0));
			chatStickersListBean.setStickersName(cursor.getString(1));
			chatStickersListBean.setStickersPic(cursor.getString(2));
			chatStickersListBean.setCategoryId(cursor.getString(3));
			chatStickersArrayList.add(chatStickersListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return chatStickersArrayList;
	}

	public ArrayList<ChatStickersListBean> getAllFreeStickers(String stickersCategoryId)
	{
		ArrayList<ChatStickersListBean> chatStickersArrayList = new ArrayList<ChatStickersListBean>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_STICKERS, allColumns, DbSqliteHelper.STICKERS_CATEGORY_ID + " = " + "'" + stickersCategoryId + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ChatStickersListBean chatStickersListBean = cursorToContactsList(cursor);
			chatStickersArrayList.add(chatStickersListBean);
			cursor.moveToNext();
		}
		cursor.close();
		return chatStickersArrayList;
	}

	public ArrayList<ChatStickersListBean> isStickerAvailable(String stickersId)
	{
		ArrayList<ChatStickersListBean> chatStickersArrayList = new ArrayList<ChatStickersListBean>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_STICKERS, allColumns, DbSqliteHelper.STICKERS_ID + " = " + "'" + stickersId + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ChatStickersListBean chatStickersListBean = cursorToContactsList(cursor);
			chatStickersArrayList.add(chatStickersListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return chatStickersArrayList;
	}

	public void deleteAllStickers()
	{
		sqlDatabase.delete(DbSqliteHelper.TABLE_NAME_STICKERS, null, null);
	}

	private ChatStickersListBean cursorToContactsList(Cursor cursor)
	{
		ChatStickersListBean chatStickersListBean = new ChatStickersListBean();

		chatStickersListBean.setStickersId(cursor.getString(0));
		chatStickersListBean.setStickersName(cursor.getString(1));
		chatStickersListBean.setStickersPic(cursor.getString(2));
		chatStickersListBean.setCategoryId(cursor.getString(3));

		return chatStickersListBean;
	}
}
