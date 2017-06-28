package com.melodygram.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.melodygram.model.ChatSticker;
import com.melodygram.model.Music;
import com.melodygram.model.MusicCategory;

import java.util.ArrayList;

/**
 * Created by LALIT on 27-07-2016.
 */
public class MusicDataSource {

    private SQLiteDatabase sqlDatabase;
    private DbSqliteHelper sqlHelper;

    private String[] allColumns =
            { DbSqliteHelper.MUSIC_CATEGORY_ID, DbSqliteHelper.MUSIC_CATEGORY_NAME, DbSqliteHelper.MUSIC_CATEGORY_THUMB,DbSqliteHelper.MUSIC_SIZE};
    public MusicDataSource(Context context) {
        sqlHelper = new DbSqliteHelper(context);

    }
    public void open() throws SQLException {
        sqlDatabase = sqlHelper.getWritableDatabase();
    }
    public void close() {
        sqlHelper.close();
    }

    public Long createMusicCategory(ContentValues values)
    {
        Long insertId = sqlDatabase.insert(DbSqliteHelper.TABLE_NAME_MUSIC_CATEGORY, null, values);

        return insertId;
    }

    public Long createMusic(ContentValues values)
    {
        Long insertId = sqlDatabase.insert(DbSqliteHelper.TABLE_NAME_MUSIC, null, values);

        return insertId;
    }

    public boolean isCatAvailable(String id) {
        String[] catId = {DbSqliteHelper.MUSIC_CATEGORY_ID};
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_MUSIC_CATEGORY,
                catId, DbSqliteHelper.MUSIC_CATEGORY_ID + " = " + "'" + id
                        + "'", null, null, null, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }


    public ArrayList<MusicCategory> getAllCategory()
    {
        ArrayList<MusicCategory> catList = new ArrayList<>();
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_MUSIC_CATEGORY, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            MusicCategory musicCategory = new MusicCategory();
            musicCategory.setCatId(cursor.getString(0));
            musicCategory.setCatName(cursor.getString(1));
            musicCategory.setTumbImage(cursor.getString(2));
            musicCategory.setTotalTracks(cursor.getString(3));
            catList.add(musicCategory);
            cursor.moveToNext();
        }

        cursor.close();
        return catList;
    }

    private String[] musicColumns =
            { DbSqliteHelper.MUSIC_ID, DbSqliteHelper.MUSIC_NAME,DbSqliteHelper.PIC,DbSqliteHelper.MUSIC_LOCAL_PATH};
    public ArrayList<Music> getCatMusic(String catId)
    {
        ArrayList<Music> catList = new ArrayList<>();
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.TABLE_NAME_MUSIC, musicColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            Music musicCategory = new Music();
            musicCategory.setId(cursor.getString(0));
            musicCategory.setName(cursor.getString(1));
            musicCategory.setPhoto(cursor.getString(2));
            musicCategory.setLocalPath(cursor.getString(3));
            catList.add(musicCategory);
            cursor.moveToNext();
        }

        cursor.close();
        return catList;
    }
}
