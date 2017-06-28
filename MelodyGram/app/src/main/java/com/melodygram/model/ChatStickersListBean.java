package com.melodygram.model;

import java.io.Serializable;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ChatStickersListBean implements Serializable{
	String categoryId, stickersId, stickersName, stickersDescription, stickersPic, stickersIsFree, stickersIsActive, stickersCost, stickersTimestamp, stickersIsPurchesed;

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getStickersIsPurchesed() {
		return stickersIsPurchesed;
	}

	public void setStickersIsPurchesed(String stickersIsPurchesed) {
		this.stickersIsPurchesed = stickersIsPurchesed;
	}

	public String getStickersId() {
		return stickersId;
	}

	public void setStickersId(String stickersId) {
		this.stickersId = stickersId;
	}

	public String getStickersName() {
		return stickersName;
	}

	public void setStickersName(String stickersName) {
		this.stickersName = stickersName;
	}

	public String getStickersDescription() {
		return stickersDescription;
	}

	public void setStickersDescription(String stickersDescription) {
		this.stickersDescription = stickersDescription;
	}

	public String getStickersPic() {
		return stickersPic;
	}

	public void setStickersPic(String stickersPic) {
		this.stickersPic = stickersPic;
	}

	public String getStickersIsFree() {
		return stickersIsFree;
	}

	public void setStickersIsFree(String stickersIsFree) {
		this.stickersIsFree = stickersIsFree;
	}

	public String getStickersIsActive() {
		return stickersIsActive;
	}

	public void setStickersIsActive(String stickersIsActive) {
		this.stickersIsActive = stickersIsActive;
	}

	public String getStickersCost() {
		return stickersCost;
	}

	public void setStickersCost(String stickersCost) {
		this.stickersCost = stickersCost;
	}

	public String getStickersTimestamp() {
		return stickersTimestamp;
	}

	public void setStickersTimestamp(String stickersTimestamp) {
		this.stickersTimestamp = stickersTimestamp;
	}
}
