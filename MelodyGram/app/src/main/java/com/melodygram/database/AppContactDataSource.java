package com.melodygram.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.melodygram.model.ContactsModel;
/**
 * Created by LALIT on 15-06-2016.
 */
public class AppContactDataSource {
    private SQLiteDatabase sqlDatabase;
    private DbSqliteHelper sqlHelper;

    private String[] allColumns = {DbSqliteHelper.appContactsUserId,
            DbSqliteHelper.appContactsName,
            DbSqliteHelper.appLocalContactsName,
            DbSqliteHelper.appContactsCountryCode,
            DbSqliteHelper.appContactsCountry,
            DbSqliteHelper.appContactsMobileNo,
            DbSqliteHelper.appContactsLastSeen,
            DbSqliteHelper.appContactsProfilePic,
            DbSqliteHelper.appContactsStatus, DbSqliteHelper.appContactsGender,
            DbSqliteHelper.appProfilePicPrivacy,
            DbSqliteHelper.appLastSeenPrivacy, DbSqliteHelper.appStatusPrivacy,
            DbSqliteHelper.appOnlinePrivacy,
            DbSqliteHelper.appReadReceiptsPrivacy,
            DbSqliteHelper.appNotfication,
            DbSqliteHelper.appMuteChat};

    public AppContactDataSource(Context context) {
        sqlHelper = new DbSqliteHelper(context);

    }

    public void open() throws SQLException {
        sqlDatabase = sqlHelper.getWritableDatabase();
    }

    public void close() {
        sqlHelper.close();
    }

    public Long createContacts(ContentValues values) {
        Long insertId = sqlDatabase.insert(DbSqliteHelper.appContactTable,
                null, values);
        return insertId;
    }

    public int updateContacts(ContentValues values, String contactNumber) {


        int insertId = sqlDatabase.update(DbSqliteHelper.appContactTable,
                values, DbSqliteHelper.appContactsUserId + " = " + "'" + contactNumber
                        + "'", null);
        return insertId;
    }

    public LinkedHashMap<String, ContactsModel> getAllContacts() {
        LinkedHashMap<String, ContactsModel> appContactsList = new LinkedHashMap<String, ContactsModel>();
      Cursor cursor = sqlDatabase.query(DbSqliteHelper.appContactTable,
				allColumns, null, null, null, null,DbSqliteHelper.appLocalContactsName);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ContactsModel appContactModel = cursorToContactsList(cursor);
			appContactsList.put(cursor.getString(5), appContactModel);
			cursor.moveToNext();
		}
		cursor.close();
        return appContactsList;
    }

    public ArrayList<ContactsModel> getAllContacts(
            final LinkedHashMap<String, ContactsModel> contactsHashMap) {
        ArrayList<ContactsModel> appContactsList = new ArrayList<ContactsModel>();

        Cursor cursor = sqlDatabase.query(DbSqliteHelper.appContactTable,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ContactsModel appContactModel = cursorToContactsList(cursor);
            ContactsModel phContactModel = contactsHashMap.get(appContactModel
                    .getPhContactNumber());
            if (phContactModel != null) {
                appContactModel.setPhContactName(phContactModel
                        .getPhContactName());
            }

            appContactsList.add(appContactModel);
            cursor.moveToNext();
        }

        cursor.close();
        return appContactsList;
    }

    public LinkedHashMap<String, String> getContactsId() {
        LinkedHashMap<String, String> appContactsList = new LinkedHashMap<String, String>();
        String[] column = {DbSqliteHelper.appContactsUserId};
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.appContactTable,
                column, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            appContactsList.put(cursor.getString(0), cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();
        return appContactsList;
    }

    public List<ContactsModel> isContactAvailable(String appContactsMobileNo) {
        List<ContactsModel> appContactsList = new ArrayList<ContactsModel>();

        Cursor cursor = sqlDatabase.query(DbSqliteHelper.appContactTable,
                allColumns, DbSqliteHelper.appContactsMobileNo + " = " + "'"
                        + appContactsMobileNo + "'", null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ContactsModel appContactModel = cursorToContactsList(cursor);
            appContactsList.add(appContactModel);
            cursor.moveToNext();
        }

        cursor.close();
        return appContactsList;
    }

    public boolean isContactAvailableOrNot(String userId) {

            Cursor cursor = sqlDatabase.query(DbSqliteHelper.appContactTable,
                    new String[]{DbSqliteHelper.appContactsUserId},
                    DbSqliteHelper.appContactsUserId + "=?",
                    new String[]{userId}, null, null, null, null);
            if (cursor.getCount() > 0)
                return true;
            else
                return false;

    }

    public String getContactNumber(String userId) {
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.appContactTable,
                new String[]{DbSqliteHelper.appLocalContactsName},
                DbSqliteHelper.appContactsUserId + "=?",
                new String[]{userId}, null, null, null, null);
        String name = null;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);
        } else
            return name;
    }

    public boolean isApplicationUser(String phNumber) {
        Cursor cursor = sqlDatabase.query(DbSqliteHelper.appContactTable,
                new String[]{DbSqliteHelper.appContactsMobileNo},
                DbSqliteHelper.appContactsMobileNo + " = " + "'" + phNumber
                        + "'", null, null, null, null);
        if (cursor.getCount() > 0)
            return true;
        else
            return false;
    }


    public void deleteContactsLists() {
        sqlDatabase.delete(DbSqliteHelper.appContactTable, null, null);
    }

    public void updateChatDeleteFlag() {
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.CONTACT_DELETE_FLAG, "0");
        sqlDatabase.update(DbSqliteHelper.appContactTable, values, null, null);
    }

    public void deleteSeletedContactsList() {

        sqlDatabase.delete(DbSqliteHelper.appContactTable,
                DbSqliteHelper.CONTACT_DELETE_FLAG + " = " + "'" + 0 + "'",
                null);
    }

    private ContactsModel cursorToContactsList(Cursor cursor) {
        ContactsModel appContactModel = new ContactsModel();
        appContactModel.setAppContactsUserId(cursor.getString(0));
        appContactModel.setAppContactName(cursor.getString(1));
        appContactModel.setPhContactName(cursor.getString(2));
        appContactModel.setAppContactsCountryCode(cursor.getString(3));
        appContactModel.setPhContactNumber(cursor.getString(5));
        appContactModel.setContactType("app");
        appContactModel.setChatType("onetoone");
        appContactModel.setContactsLastSeen("");
        appContactModel.setAppContactsProfilePic(cursor.getString(7));
        appContactModel.setPhChatContactStatus(cursor.getString(8));
        appContactModel.setGender(cursor.getString(9));
        appContactModel.setProfilePicPrivacy(cursor.getString(10));
        appContactModel.setLastSeenPrivacy(cursor.getString(11));
        appContactModel.setStatusPrivacy(cursor.getString(12));
        appContactModel.setOnlinePrivacy(cursor.getString(13));
        appContactModel.setNotification(cursor.getString(14));
        appContactModel.setMuteChat(cursor.getString(15));
        return appContactModel;
    }

    public void updateDeleteFlag(String deleteFlag) {
        ContentValues values = new ContentValues();
        values.put(DbSqliteHelper.CONTACT_DELETE_FLAG, deleteFlag);
        sqlDatabase.update(DbSqliteHelper.appContactTable, values, null, null);

    }

}
