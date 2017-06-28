package com.melodygram.model;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ChatSticker implements Serializable
{
	String stickersId, categoryId, stickersName, stickersPic;
	boolean isDownloaded;
	Drawable stickerPath;
	public String getStickersId()
	{
		return stickersId;
	}

	public void setStickersId(String stickersId)
	{
		this.stickersId = stickersId;
	}

	public String getStickersName()
	{
		return stickersName;
	}

	public void setStickersName(String stickersName)
	{
		this.stickersName = stickersName;
	}

	public String getStickersPic()
	{
		return stickersPic;
	}

	public void setStickersPic(String stickersPic)
	{
		this.stickersPic = stickersPic;
	}

	public String getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	public boolean getIsDownloaded()
	{
		return isDownloaded;
	}

	public void setIsDownloaded(boolean isDownloaded)
	{
		this.isDownloaded = isDownloaded;
	}
	
	public Drawable getStickerPath()
	{
		return stickerPath;
	}

	public void setStickerPath(Drawable path)
	{
		this.stickerPath = path;
	}
}
