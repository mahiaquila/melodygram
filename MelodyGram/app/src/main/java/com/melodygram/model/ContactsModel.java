package com.melodygram.model;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Random;

import android.graphics.Bitmap;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ContactsModel implements Serializable {

	public static final int aItem = 0;
	public static final int aSection = 1;

	public int getSectionType() {
		return sectionType;
	}

	public void setSectionType(int sectionType) {
		this.sectionType = sectionType;
	}

	public int getSectionData() {
		return sectionData;
	}

	public void setSectionData(int sectionData) {
		this.sectionData = sectionData;
	}

	public String getSectionText() {
		return sectionText;
	}

	public void setSectionText(String sectionText) {
		this.sectionText = sectionText;
	}

	public int sectionType, sectionData;
	public String sectionText;

	String phContactId, phContactName, phContactNumber, appContactName,
			phContactLabel, phChatContactStatus, phContactPicPath, lastSeen,
			contactType, contactServerName, avtarName, gender, mute,
			profilePicPrivacy, lastSeenPrivacy, statusPrivacy, onlinePrivacy,
			readReceiptsPrivacy, muteGame, notification, buzzControl, muteChat;

	public String getPhContactPicPath() {
		return phContactPicPath;
	}

	public String getContactServerName() {
		return contactServerName;
	}

	public void setContactServerName(String contactServerName) {
		this.contactServerName = contactServerName;
	}

	public void setPhContactPicPath(String phContactPicPath) {
		this.phContactPicPath = phContactPicPath;
	}

	public String getPhChatContactStatus() {
		return phChatContactStatus;
	}

	public void setPhChatContactStatus(String phChatContactStatus) {
		this.phChatContactStatus = phChatContactStatus;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	Bitmap contactPicBitmap;

	public Bitmap getContactPicBitmap() {
		return contactPicBitmap;
	}

	public void setContactPicBitmap(Bitmap contactPicBitmap) {
		this.contactPicBitmap = contactPicBitmap;
	}

	public String getPhContactId() {
		return phContactId;
	}

	public void setPhContactId(String phContactId) {
		this.phContactId = phContactId;
	}

	public String getPhContactName() {
		return phContactName;
	}

	public void setPhContactName(String phContactName) {
		this.phContactName = phContactName;
	}

	public String getPhContactNumber() {
		return phContactNumber;
	}

	public void setAppContactName(String appContactName) {
		this.appContactName = appContactName;
	}

	public String getAppContactNumber() {
		return appContactName;
	}

	public void setPhContactNumber(String phContactNumber) {
		this.phContactNumber = phContactNumber;
	}

	public String getPhContactLabel() {
		return phContactLabel;
	}

	public void setPhContactLabel(String phContactLabel) {
		this.phContactLabel = phContactLabel;
	}

	public static Comparator<ContactsModel> compareByName = new Comparator<ContactsModel>() {
		public int compare(ContactsModel one, ContactsModel other) {
			return one.getPhContactName().compareToIgnoreCase(
					other.getPhContactName());
		}
	};

	String appContactsUserId, appContactsCountry, appContactsCountryCode,
			appContactsStatus, appContactsProfilePic;
	String[] appContactsPicArray;

	public String getAppContactsUserId() {
		return appContactsUserId;
	}

	public void setAppContactsUserId(String appContactsUserId) {
		this.appContactsUserId = appContactsUserId;
	}

	public String getAppContactsCountry() {
		return appContactsCountry;
	}

	public void setAppContactsCountry(String appContactsCountry) {
		this.appContactsCountry = appContactsCountry;
	}

	public String getAppContactsCountryCode() {
		return appContactsCountryCode;
	}

	public void setAppContactsCountryCode(String appContactsCountryCode) {
		this.appContactsCountryCode = appContactsCountryCode;
	}

	public String getAppContactsStatus() {
		return appContactsStatus;
	}

	public void setAppContactsStatus(String appContactsStatus) {
		this.appContactsStatus = appContactsStatus;
	}

	public String getAppContactsProfilePic() {
		return appContactsProfilePic;
	}

	public void setAppContactsProfilePic(String appContactsProfilePic) {
		this.appContactsProfilePic = appContactsProfilePic;
	}

	public String getAppContactsPicArray() {
		String str = "";
		for (int i = 0; i < appContactsPicArray.length; i++) {
			str = str + appContactsPicArray[i] + ",";
		}
		str = str.substring(0, str.lastIndexOf(","));
		return str;
	}

	public void setAppContactsPicArray(String[] arr) {
		this.appContactsPicArray = arr;
	}

	public String getAppContactsRandomPics() {
		Random r = new Random();
		int max = appContactsPicArray.length;
		int index = r.nextInt(max) + 0;
		return appContactsPicArray[index];
	}

	public String getAppContactsZeroIndexPic() {
		return appContactsPicArray[0];
	}

	String chatType, contactsRoomId, fromLanguage, toLanguage, countryCode,
			isPrivate, blocked, isBlocked, timeStamp;



	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getChatType() {
		return chatType;
	}

	public void setChatType(String chatType) {
		this.chatType = chatType;
	}

	public String getContactsRoomId() {
		return contactsRoomId;
	}

	public void setContactsRoomId(String contactsRoomId) {
		this.contactsRoomId = contactsRoomId;
	}

	public String getContactsLastSeen() {
		return lastSeen;
	}

	public void setContactsLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public String getPrivateType() {
		return isPrivate;
	}

	public void setPrivateType(String isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getBlocked() {
		return blocked;
	}

	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}

	public String getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(String isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getAvtarName() {
		return avtarName;
	}

	public void setAvtarName(String avtarName) {
		this.avtarName = avtarName;
	}

	public String getMute() {
		return mute;
	}

	public void setMute(String mute) {
		this.mute = mute;
	}

	public String getProfilePicPrivacy() {
		return profilePicPrivacy;
	}

	public void setProfilePicPrivacy(String profilePicPrivacy) {
		this.profilePicPrivacy = profilePicPrivacy;
	}

	public String getLastSeenPrivacy() {
		return lastSeenPrivacy;
	}

	public void setLastSeenPrivacy(String lastSeenPrivacy) {
		this.lastSeenPrivacy = lastSeenPrivacy;
	}

	public String getStatusPrivacy() {
		return statusPrivacy;
	}

	public void setStatusPrivacy(String statusPrivacy) {
		this.statusPrivacy = statusPrivacy;
	}

	public String getOnlinePrivacy() {
		return onlinePrivacy;
	}

	public void setOnlinePrivacy(String onlinePrivacy) {
		this.onlinePrivacy = onlinePrivacy;
	}

	public String getReadReceiptsPrivacy() {
		return readReceiptsPrivacy;
	}


	public String getMuteGame() {
		return muteGame;
	}



	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getBuzzControl() {
		return buzzControl;
	}

	public void setBuzzControl(String buzzControl) {
		this.buzzControl = buzzControl;
	}

	public String getMuteChat() {
		return muteChat;
	}

	public void setMuteChat(String muteChat) {
		this.muteChat = muteChat;
	}

}
