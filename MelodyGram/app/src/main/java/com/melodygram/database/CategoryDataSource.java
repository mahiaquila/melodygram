package com.melodygram.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.melodygram.model.CategoryModel;
import com.melodygram.model.ChatSticker;
import com.melodygram.model.ChatStickerCategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
/**
 * Created by LALIT on 15-06-2016.
 */
public class CategoryDataSource
{
	private SQLiteDatabase sqlDatabase;
	private DbSqliteHelper sqlHelper;
	Context context;
	private String[] allColumns =
	{ DbSqliteHelper.CATEGORY_ID, DbSqliteHelper.CATEGORY_NAME, DbSqliteHelper.CATEGORY_THUMB, DbSqliteHelper.CATEGORY_IS_FREE, DbSqliteHelper.CATEGORY_COST, DbSqliteHelper.CATEGORY_IS_PRUCHESED };

	public CategoryDataSource(Context context)
	{
		this.context = context;
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

	public Long createCategory(String categoryId, String categoryName, String categoryThumb, String categoryIsFree, String categoryCost, String categoryIsPurchesed)
	{
		ContentValues values = new ContentValues();

		values.put(DbSqliteHelper.CATEGORY_ID, categoryId);
		values.put(DbSqliteHelper.CATEGORY_NAME, categoryName);
		values.put(DbSqliteHelper.CATEGORY_THUMB, categoryThumb);
		values.put(DbSqliteHelper.CATEGORY_IS_FREE, categoryIsFree);
		values.put(DbSqliteHelper.CATEGORY_COST, categoryCost);
		values.put(DbSqliteHelper.CATEGORY_IS_PRUCHESED, categoryIsPurchesed);

		Long insertId = sqlDatabase.insert(DbSqliteHelper.TABLE_NAME_CATEGORY, null, values);

		return insertId;
	}

	public Long createCategory(ContentValues values)
	{

		Long insertId = sqlDatabase.insert(DbSqliteHelper.TABLE_NAME_CATEGORY, null, values);

		return insertId;
	}

	public int updateCategory(String categoryId, String categoryName, String categoryThumb, String categoryIsFree, String categoryCost, String categoryIsPurchesed)
	{
		ContentValues values = new ContentValues();

		values.put(DbSqliteHelper.CATEGORY_ID, categoryId);
		values.put(DbSqliteHelper.CATEGORY_NAME, categoryName);
		values.put(DbSqliteHelper.CATEGORY_THUMB, categoryThumb);
		values.put(DbSqliteHelper.CATEGORY_IS_FREE, categoryIsFree);
		values.put(DbSqliteHelper.CATEGORY_COST, categoryCost);
		values.put(DbSqliteHelper.CATEGORY_IS_PRUCHESED, categoryIsPurchesed);

		int insertId = sqlDatabase.update(DbSqliteHelper.TABLE_NAME_CATEGORY, values, DbSqliteHelper.CATEGORY_ID + " = " + "'" + categoryId + "'", null);

		return insertId;
	}

	public ArrayList<CategoryModel> getAllCategory()
	{
		ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_CATEGORY, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			CategoryModel categoryListBean = cursorToContactsList(cursor);
			categoryList.add(categoryListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return categoryList;
	}

	public ArrayList<CategoryModel> getAllFreeCategory()
	{
		ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_CATEGORY, allColumns, DbSqliteHelper.CATEGORY_IS_PRUCHESED + " = " + "'" + "yes" + "' OR " + DbSqliteHelper.CATEGORY_IS_FREE
				+ " = " + "'" + "yes" + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			CategoryModel categoryListBean = cursorToContactsList(cursor);
			categoryList.add(categoryListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return categoryList;
	}

	public ArrayList<CategoryModel> getAllToDownloadCategory()
	{
		ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_CATEGORY, allColumns, DbSqliteHelper.CATEGORY_IS_PRUCHESED + " = " + "'" + "no" + "' AND " + DbSqliteHelper.CATEGORY_IS_FREE
				+ " = " + "'" + "no" + "' AND " + DbSqliteHelper.CATEGORY_COST + " = " + "'" + "" + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			CategoryModel categoryListBean = cursorToContactsList(cursor);
			categoryList.add(categoryListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return categoryList;
	}

	public ArrayList<CategoryModel> getAllToPurchseCategory()
	{
		ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_CATEGORY, allColumns, DbSqliteHelper.CATEGORY_IS_PRUCHESED + " = " + "'" + "no" + "' AND " + DbSqliteHelper.CATEGORY_IS_FREE
				+ " = " + "'" + "no" + "' AND " + DbSqliteHelper.CATEGORY_COST + " != " + "'" + "" + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			CategoryModel categoryListBean = cursorToContactsList(cursor);
			categoryList.add(categoryListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return categoryList;
	}

	public ArrayList<CategoryModel> getAllNotFreeCategory()
	{
		ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_CATEGORY, allColumns, DbSqliteHelper.CATEGORY_IS_PRUCHESED + " = " + "'" + "no" + "' AND " + DbSqliteHelper.CATEGORY_IS_FREE
				+ " = " + "'" + "no" + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			CategoryModel categoryListBean = cursorToContactsList(cursor);
			categoryList.add(categoryListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return categoryList;
	}

	public ArrayList<CategoryModel> isCategoryAvailable(String categoryId)
	{
		ArrayList<CategoryModel> categoryList = new ArrayList<CategoryModel>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_CATEGORY, allColumns, DbSqliteHelper.CATEGORY_ID + " = " + "'" + categoryId + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			CategoryModel categoryListBean = cursorToContactsList(cursor);
			categoryList.add(categoryListBean);
			cursor.moveToNext();
		}

		cursor.close();
		return categoryList;
	}

	public void deleteAllCategory()
	{
		sqlDatabase.delete(DbSqliteHelper.TABLE_NAME_CATEGORY, null, null);
	}

	private CategoryModel cursorToContactsList(Cursor cursor)
	{
		CategoryModel categoryListBean = new CategoryModel();

		categoryListBean.setCategoryId(cursor.getString(0));
		categoryListBean.setCategoryName(cursor.getString(1));
		categoryListBean.setCategoryThumb(cursor.getString(2));
		categoryListBean.setCategoryIsFree(cursor.getString(3));
		categoryListBean.setCategoryCost(cursor.getString(4));
		categoryListBean.setCategoryIsPurchesed(cursor.getString(5));

		categoryListBean.setSectionType(0);

		return categoryListBean;
	}

	public LinkedHashMap<String, ChatStickerCategory> getAllStickersCategory()
	{
		LinkedHashMap<String, ChatStickerCategory> categoryDetailsList = new LinkedHashMap<String, ChatStickerCategory>();

		Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_CATEGORY, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			ChatStickerCategory categoryListBean = new ChatStickerCategory();
			categoryListBean.setCategoryId(cursor.getString(0));
			categoryListBean.setCategoryName(cursor.getString(1));
			categoryListBean.setThumbImage(cursor.getString(2));
			categoryListBean.setIsFree(cursor.getString(3));
			categoryListBean.setCost(cursor.getString(4));
			categoryListBean.setIsPurchesed(cursor.getString(5));
			categoryListBean.setIsDownloaded(true);
			ChatStickersDataSource chatStickersDataSource = new ChatStickersDataSource(context);
			chatStickersDataSource.open();
			categoryListBean.setStickerList(getChatStickers(chatStickersDataSource, cursor.getString(0)));
			chatStickersDataSource.close();
			categoryDetailsList.put(cursor.getString(0), categoryListBean);
			cursor.moveToNext();
		}
		cursor.close();
		return categoryDetailsList;
	}

	public ArrayList<ChatSticker> getChatStickers(ChatStickersDataSource chatStickersDataSource, String id)
	{

		return chatStickersDataSource.getAllStickersDetails(id);

	}

}
