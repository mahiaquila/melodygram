package com.melodygram.database;

import java.util.LinkedHashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import com.melodygram.model.ContactsModel;
import com.melodygram.utils.PhoneContactUtil;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ContactDataSource
{
	private Context context;

	public ContactDataSource(Context context)
	{
		this.context = context;
	}

	public LinkedHashMap<String, ContactsModel> getAllContacts() {
		
		LinkedHashMap<String, ContactsModel> chatContactsArrayList = new LinkedHashMap<String, ContactsModel>();

		 String[] PROJECTION = new String[] {
	        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
	        Contacts.DISPLAY_NAME,
	        ContactsContract.CommonDataKinds.Phone.NUMBER
	    };
		 String where = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("+ Contacts.DISPLAY_NAME + " != '' ))";
		 ContentResolver cr = context.getContentResolver();
	        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, where, null, null);
	        if (cursor != null) {
	            try {
	                final int nameIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
	                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	                final int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
	                while (cursor.moveToNext()) {
	                	ContactsModel chatContactsListBean = new ContactsModel();
	                	chatContactsListBean.setPhContactId(cursor.getString(idIndex));
	            		chatContactsListBean.setPhContactName(cursor.getString(nameIndex));
	            		chatContactsListBean.setSectionType(0);
	            		chatContactsListBean.setPhContactNumber(PhoneContactUtil.returnOnlyNumber(cursor.getString(numberIndex).trim()));
	            		chatContactsListBean.setContactType("phone");
	            		chatContactsArrayList.put(PhoneContactUtil.returnOnlyNumber(cursor.getString(numberIndex)),
	        					chatContactsListBean);
	                }
	            } finally {
	                cursor.close();
	            }
	        }
		return chatContactsArrayList;
	}
	
	
public LinkedHashMap<String, ContactsModel> getAllNotAppContacts() {
		
		LinkedHashMap<String, ContactsModel> chatContactsArrayList = new LinkedHashMap<String, ContactsModel>();

		 String[] PROJECTION = new String[] {
	        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
	        Contacts.DISPLAY_NAME,
	        ContactsContract.CommonDataKinds.Phone.NUMBER
	    };
		 String where = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("+ Contacts.HAS_PHONE_NUMBER + "=1) AND ("+ Contacts.DISPLAY_NAME + " != '' ))";

		 ContentResolver cr = context.getContentResolver();
	        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, where, null, null);
	        if (cursor != null) {
	            try {
	                final int nameIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
	                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	                final int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

	           
	                while (cursor.moveToNext()) {
	                	
	                	ContactsModel chatContactsListBean = new ContactsModel();
	                	chatContactsListBean.setPhContactId(cursor.getString(idIndex));
	            		chatContactsListBean.setPhContactName(cursor.getString(nameIndex));
	            		chatContactsListBean.setSectionType(0);
	            		chatContactsListBean.setPhContactNumber(PhoneContactUtil.returnOnlyNumber(cursor.getString(numberIndex).trim()));
	            		chatContactsListBean.setContactType("phone");
	            		chatContactsArrayList.put(PhoneContactUtil.returnOnlyNumber(cursor.getString(numberIndex)),
	        					chatContactsListBean);
	                }
	            } finally {
	                cursor.close();
	            }
	        }
		return chatContactsArrayList;
	}
	
	
	
	
	
	
	public boolean isContactAvailable(String contactIdToChk) {
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(contactIdToChk));
		String[] mPhoneNumberProjection = { PhoneLookup._ID,
				PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,
				mPhoneNumberProjection, null, null, null);
		try {
			if (cur.moveToFirst()) {
				return true;
			}
		} finally {
			if (cur != null)
				cur.close();
		}
		return false;
	}	
	public String getContactName(String mobileNumber) {
		
		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(mobileNumber));
		String[] mPhoneNumberProjection = {PhoneLookup.DISPLAY_NAME };
		Cursor cursor = context.getContentResolver().query(lookupUri,
				mPhoneNumberProjection, null, null, null);
		String contactName = null;
		try {
			if (cursor.moveToFirst()) {
				 contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	
		return contactName;
	}
	public String getContactNoWithColon() {

		String allContactNos = null;
		 String[] PROJECTION = new String[] {
	        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
	        Contacts.DISPLAY_NAME,
	        ContactsContract.CommonDataKinds.Phone.NUMBER
	    };

		 ContentResolver cr = context.getContentResolver();
	        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
	        if (cursor != null) {
	            try {
	                final int nameIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
	                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	                final int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

	           
	                while (cursor.moveToNext()) {
	            		if (cursor.getString(numberIndex).contains(";")) {
	        				String[] contactNoTemp = cursor.getString(numberIndex).split(";");
	        				for (String contactsString : contactNoTemp) {
	        					if (allContactNos != null) {
	        						allContactNos = allContactNos + ":"
	        								+ PhoneContactUtil.returnOnlyNumber(contactsString);
	        					} else {
	        						allContactNos = PhoneContactUtil.returnOnlyNumber(contactsString);
	        					}
	        				}
	        			} else {
	        				if (allContactNos != null) {
	        					allContactNos = allContactNos + ":"
	        							+ PhoneContactUtil.returnOnlyNumber(cursor.getString(numberIndex));
	        				} else {
	        					allContactNos = PhoneContactUtil.returnOnlyNumber(cursor.getString(numberIndex));
	        				}
	        			}
	                }
	            } 
	            catch(Exception e)
	            {
	            	e.printStackTrace();
	            }
	            finally {
	                cursor.close();
	            }
	        }
		return allContactNos;
	}
}
