package com.melodygram.model;

import java.util.Comparator;
import java.util.Random;
/**
 * Created by LALIT on 15-06-2016.
 */
public class FriendsModel {
	String friendsChatBlocked, friendsIsBlocked, friendsIsDeleted, groupUsers,
			seenCount, mute, profilePicPrivacy, lastSeenPrivacy, statusPrivacy,
			onlinePrivacy, readReceiptsPrivacy,isBlocked;

	public String getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(String isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String getFriendsChatBlocked() {
		return friendsChatBlocked;
	}

	public void setFriendsChatBlocked(String friendsChatBlocked) {
		this.friendsChatBlocked = friendsChatBlocked;
	}

	public String getFriendsIsBlocked() {
		return friendsIsBlocked;
	}

	public void setFriendsIsBlocked(String friendsIsBlocked) {
		this.friendsIsBlocked = friendsIsBlocked;
	}

	public String getFriendsIsDeleted() {
		return friendsIsDeleted;
	}

	public void setFriendsIsDeleted(String friendsIsDeleted) {
		this.friendsIsDeleted = friendsIsDeleted;
	}

	String friendsLastSeen, friendsUnreadMSG;

	public String getFriendsUnreadMSG() {
		return friendsUnreadMSG;
	}

	public void setFriendsUnreadMSG(String friendsUnreadMSG) {
		this.friendsUnreadMSG = friendsUnreadMSG;
	}

	public String getFriendsLastSeen() {
		return friendsLastSeen;
	}

	public void setFriendsLastSeen(String friendsLastSeen) {
		this.friendsLastSeen = friendsLastSeen;
	}

	String friendsCountryCode;

	public String getFriendsCountryCode() {
		return friendsCountryCode;
	}

	public void setFriendsCountryCode(String friendsCountryCode) {
		this.friendsCountryCode = friendsCountryCode;
	}

	public static Comparator<FriendsModel> compareByName = new Comparator<FriendsModel>() {
		public int compare(FriendsModel one, FriendsModel other) {
			return one.getFriendName().compareToIgnoreCase(
					other.getFriendName());
		}
	};

	String friendsLastChatTime;
	String friendsChatType;

	public String getFriendsLastChatTime() {
		return friendsLastChatTime;
	}

	public void setFriendsLastChatTime(String friendsLastChatTime) {
		this.friendsLastChatTime = friendsLastChatTime;
	}

	public String getFriendsChatType() {
		return friendsChatType;
	}

	public void setFriendsChatType(String friendsChatType) {
		this.friendsChatType = friendsChatType;
	}

	boolean isPrivate;

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	String[] friendsCaptionUrl;
	String friendName, friendAppName,friendsPicIconUrl;
	String friendsStatus;
	String friendLastChatTime;
	String friendsPhoneNumber;

	String friendsUserId;
	String friendsCountry;

	String friendsRoomId;
	String isFavfriends;

	String friendsLastchat;

	String friendsChatMessageTranslated, friendsChatMessageFromLanguage,
			friendsChatMessageToLanguage;

	String friendsLatitude, friendsLongitude;
	String friendIsAdmin, friendGroupAdminUserId, friendIsOnline;

	public String getFriendsLatitude() {
		return friendsLatitude;
	}

	public void setFriendsLatitude(String friendsLatitude) {
		this.friendsLatitude = friendsLatitude;
	}

	public String getFriendsLongitude() {
		return friendsLongitude;
	}

	public void setFriendsLongitude(String friendsLongitude) {
		this.friendsLongitude = friendsLongitude;
	}

	public String getFriendsChatMessageTranslated() {
		return friendsChatMessageTranslated;
	}

	public void setFriendsChatMessageTranslated(
			String friendsChatMessageTranslated) {
		this.friendsChatMessageTranslated = friendsChatMessageTranslated;
	}

	public String getFriendsChatMessageFromLanguage() {
		return friendsChatMessageFromLanguage;
	}

	public void setFriendsChatMessageFromLanguage(
			String friendsChatMessageFromLanguage) {
		this.friendsChatMessageFromLanguage = friendsChatMessageFromLanguage;
	}

	public String getFriendsChatMessageToLanguage() {
		return friendsChatMessageToLanguage;
	}

	public void setFriendsChatMessageToLanguage(
			String friendsChatMessageToLanguage) {
		this.friendsChatMessageToLanguage = friendsChatMessageToLanguage;
	}

	public String getFriendsLastchat() {
		return friendsLastchat;
	}

	public void setFriendsLastchat(String friendsLastchat) {
		this.friendsLastchat = friendsLastchat;
	}

	public String getIsFavfriends() {
		return isFavfriends;
	}

	public void setIsFavfriends(String isFavfriends) {
		this.isFavfriends = isFavfriends;
	}

	public String getFriendsRoomId() {
		return friendsRoomId;
	}

	public void setFriendsRoomId(String friendsRoomId) {
		this.friendsRoomId = friendsRoomId;
	}

	public String getFriendsUserId() {
		return friendsUserId;
	}

	public void setFriendsUserId(String friendsUserId) {
		this.friendsUserId = friendsUserId;
	}

	public String getFriendsCountry() {
		return friendsCountry;
	}

	public void setFriendsCountry(String friendsCountry) {
		this.friendsCountry = friendsCountry;
	}

	public String getFriendsPhoneNumber() {
		return friendsPhoneNumber;
	}

	public void setFriendsPhoneNumber(String friendsPhoneNumber) {
		this.friendsPhoneNumber = friendsPhoneNumber;
	}

	public String getFriendsPicIconUrl() {
		return friendsPicIconUrl;
	}

	public void setFriendsPicIconUrl(String url) {
		this.friendsPicIconUrl = url;
	}

	public String getFriendsCationArray() {
		String str = "";
		for (int i = 0; i < friendsCaptionUrl.length; i++) {
			str = str + friendsCaptionUrl[i] + ",";
		}
		str = str.substring(0, str.lastIndexOf(","));
		return str;
	}



	public String getFriendsOicIconUrls() {
		return friendsPicIconUrl;
	}



	public String getFriendsZeroIndexCaption() {
		return friendsCaptionUrl[0];
	}

	public String getFriendsRandomCaption() {
		Random r = new Random();
		int max = friendsCaptionUrl.length;
		int index = r.nextInt(max) + 0;
		return friendsCaptionUrl[index];
	}


	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public String getFriendAppName() {
		return friendAppName;
	}

	public void setFriendAppName(String friendAppName) {
		this.friendAppName = friendAppName;
	}

	public String getFriendsStatus() {
		return friendsStatus;
	}

	public void setFriendsStatus(String friendsStatus) {
		this.friendsStatus = friendsStatus;
	}

	public String getFriendLastChatTime() {
		return friendLastChatTime;
	}

	public void setFriendLastChatTime(String friendLastChatTime) {
		this.friendLastChatTime = friendLastChatTime;
	}

	public String getFriendIsGroupAdmin() {
		return friendIsAdmin;
	}

	public void setFriendsIsGroupAdmin(String friendIsAdmin) {
		this.friendIsAdmin = friendIsAdmin;
	}

	public String getFriendsGroupAdminUserId() {
		return friendGroupAdminUserId;
	}

	public void setFriendsGroupAdminUserId(String friendGroupAdminUserId) {
		this.friendGroupAdminUserId = friendGroupAdminUserId;
	}

	public String getFriendsOnline() {
		return friendIsOnline;
	}

	public void setFriendsOnline(String friendIsOnline) {
		this.friendIsOnline = friendIsOnline;
	}

	public String getGroupUsers() {
		return groupUsers;
	}

	public void setGroupUsers(String groupUsers) {
		this.groupUsers = groupUsers;
	}

	public String getSeenCount() {
		return seenCount;
	}

	public void setSeenCount(String seenCount) {
		this.seenCount = seenCount;
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

	public void setReadReceiptsPrivacy(String readReceiptsPrivacy) {
		this.readReceiptsPrivacy = readReceiptsPrivacy;
	}
}
