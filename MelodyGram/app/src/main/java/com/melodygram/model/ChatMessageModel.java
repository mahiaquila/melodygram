package com.melodygram.model;

import java.io.File;
import java.util.Comparator;
import java.util.Random;

import android.graphics.Bitmap;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ChatMessageModel {
	String chatAudioTime,chatDisappear,chatEditMsg;

	public String getChatEditMsg() {
		return chatEditMsg;
	}

	public void setChatEditMsg(String chatEditMsg) {
		this.chatEditMsg = chatEditMsg;
	}

	public String getChatDisappear() {
		return chatDisappear;
	}
	public void setChatDisappear(String chatDisappear) {
		this.chatDisappear = chatDisappear;
	}
	public String getChatAudioTime() {
		return chatAudioTime;
	}
	public void setChatAudioTime(String chatAudioTime) {
		this.chatAudioTime = chatAudioTime;
	}



	
	String chatTempMessageId;
	
	public String getChatTempMessageId() {
		return chatTempMessageId;
	}
	public void setChatTempMessageId(String chatTempMessageId) {
		this.chatTempMessageId = chatTempMessageId;
	}
	
	int chatMsgPosition;
	
	public int getChatMsgPosition() {
		return chatMsgPosition;
	}
	public void setChatMsgPosition(int chatMsgPosition) {
		this.chatMsgPosition = chatMsgPosition;
	}
	String chatIsRemoved, chatIsSent;
	
	public String getChatIsRemoved() {
		return chatIsRemoved;
	}
	public void setChatIsRemoved(String chatIsRemoved) {
		this.chatIsRemoved = chatIsRemoved;
	}
	public String getChatIsSent() {
		return chatIsSent;
	}
	public void setChatIsSent(String chatIsSent) {
		this.chatIsSent = chatIsSent;
	}
	String chatStickerAudioBuzzId;
	public String getChatStickerAudioBuzzId() {
		return chatStickerAudioBuzzId;
	}
	public void setChatStickerAudioBuzzId(String chatStickerAudioBuzzId) {
		this.chatStickerAudioBuzzId = chatStickerAudioBuzzId;
	}
	String chatFileThumbnail;
	
	public String getChatFileThumbnail() {
		return chatFileThumbnail;
	}
	public void setChatFileThumbnail(String chatFileThumbnail) {
		this.chatFileThumbnail = chatFileThumbnail;
	}
	String chatMessageSeen, chatMessageSeenTime;
	
	public String getChatMessageSeen() {
		return chatMessageSeen;
	}
	public void setChatMessageSeen(String chatMessageSeen) {
		this.chatMessageSeen = chatMessageSeen;
	}
	public String getChatMessageSeenTime() {
		return chatMessageSeenTime;
	}
	public void setChatMessageSeenTime(String chatMessageSeenTime) {
		this.chatMessageSeenTime = chatMessageSeenTime;
	}
	String profileImgUrl;
	String actualMsg;
	String msgTime;
	String msgDate;
	String lastConvertationDate;
	
	String msgId;
	String userId;
	String senderName;
	
	String messageType;
	String fileData,fileName;
	String fileExtn;
	Bitmap fileBitmap;
	String postImgUrl;
	File fileAudio;
	
	String messageRoomId;
	String progressValue;
	int imageDownlaod;

	public int getImageDownlaod() {
		return imageDownlaod;
	}

	public void setImageDownlaod(int imageDownlaod) {
		this.imageDownlaod = imageDownlaod;
	}

	String newMessageCount,stickerId,buzzPicPath,buzzName;
	boolean newMsgFlag=false;
	public String getMessageRoomId() {
		return messageRoomId;
	}
	public void setMessageRoomId(String messageRoomId) {
		this.messageRoomId = messageRoomId;
	}
	File fileVideo;
	Bitmap videoThumb;
	
	boolean singleTickFlag = false;

	public String getProgressValue() {
		return progressValue;
	}

	public void setProgressValue(String progressValue) {
		this.progressValue = progressValue;
	}

	public boolean isSingleTickFlag() {
		return singleTickFlag;
	}
	public void setSingleTickFlag(boolean singleTickFlag) {
		this.singleTickFlag = singleTickFlag;
	}
	public Bitmap getVideoThumb() {
		return videoThumb;
	}
	public void setVideoThumb(Bitmap videoThumb) {
		this.videoThumb = videoThumb;
	}
	public File getFileVideo() {
		return fileVideo;
	}
	public void setFileVideo(File fileVideo) {
		this.fileVideo = fileVideo;
	}
	String contactName;
	String contactNumber;
	
	String latitude;
	String langitude;
	
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLangitude() {
		return langitude;
	}
	public void setLangitude(String langitude) {
		this.langitude = langitude;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public File getFileAudio() {
		return fileAudio;
	}
	public void setFileAudio(File fileAudio) {
		this.fileAudio = fileAudio;
	}
	public String getPostImgUrl() {
		return postImgUrl;
	}
	public void setPostImgUrl(String postImgUrl) {
		this.postImgUrl = postImgUrl;
	}
	public Bitmap getFileBitmap() {
		return fileBitmap;
	}
	public void setFileBitmap(Bitmap fileBitmap) {
		this.fileBitmap = fileBitmap;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getFileData() {
		return fileData;
	}
	public void setFileData(String fileData) {
		this.fileData = fileData;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileExtn() {
		return fileExtn;
	}
	public void setFileExtn(String fileExtn) {
		this.fileExtn = fileExtn;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	Boolean msgToFromFlag = false;
	Boolean msgDateFlag =false;
	
	public String getMsgDate() {
		return msgDate;
	}
	public void setMsgDate(String msgDate) {
		this.msgDate = msgDate;
	}
	public Boolean getMsgDateFlag() {
		return msgDateFlag;
	}
	public void setMsgDateFlag(Boolean msgDateFlag) {
		this.msgDateFlag = msgDateFlag;
	}
	public Boolean getMsgToFromFlag() {
		return msgToFromFlag;
	}
	public void setMsgToFromFlag(Boolean msgToFromFlag) {
		this.msgToFromFlag = msgToFromFlag;
	}
	
	public String getRandomProfileImageUrl(){
		String[] picsArr = profileImgUrl.split(",");
		Random r = new Random();
		int max = picsArr.length;
		int index = r.nextInt(max)+0;
		return picsArr[index];
	}
	public String getFriendsZeroIndexPicUrl(){
		String[] picsArr = profileImgUrl.split(",");
		return picsArr[0];
	}
	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}
	public String getActualMsg() {
		return actualMsg;
	}
	public void setActualMsg(String actualMsg) {
		this.actualMsg = actualMsg;
	}
	public String getMsgTime() {
		return msgTime;
	}
	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}
	public String getLastConvertationDate() {
		return lastConvertationDate;
	}
	public void setLastConvertationDate(String lastConvertationDate) {
		this.lastConvertationDate = lastConvertationDate;
	}
	
	public boolean getNewMsgFlag() {
		return newMsgFlag;
	}
	public void setNewMsgFlag(boolean newMsgFlag) {
		this.newMsgFlag = newMsgFlag;
	}
	
	public void setNewMessageCount(String newMessageCount) {
		this.newMessageCount = newMessageCount;
	}
	public String getNewMessageCount() {
		return newMessageCount;
	}
	
	public void setStickerId(String stickerId) {
		this.stickerId = stickerId;
	}
	public String getStickerId() {
		return stickerId;
	}
	
	
	public void setBuzzName(String buzzName) {
		this.buzzName = buzzName;
	}
	public String getBuzzName() {
		return buzzName;
	}
	
	public void setBuzzPicPath(String buzzPicPath) {
		this.buzzPicPath = buzzPicPath;
	}
	public String getBuzzPicPath() {
		return buzzPicPath;
	}
	String audiobuzzid,audiobuzzname,audiobuzzphoto,audiobuzzfile;
	public void setAudiobuzzid(String audiobuzzid) {
		this.audiobuzzid = audiobuzzid;
	}
	public String getAudiobuzzid() {
		return audiobuzzid;
	}
	public void setAudiobuzzfile(String audiobuzzfile) {
		this.audiobuzzfile = audiobuzzfile;
	}
	public String getAudiobuzzfile() {
		return audiobuzzfile;
	}
	
	
	
	
	
	public static final Comparator<ChatMessageModel> compareByPosition = new Comparator<ChatMessageModel>() {
        public int compare(ChatMessageModel d, ChatMessageModel d1) {
            return d.getChatMsgPosition() - d1.getChatMsgPosition();
        }
    };
   
}
