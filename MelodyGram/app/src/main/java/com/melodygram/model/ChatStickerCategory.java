package com.melodygram.model;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * Created by LALIT on 15-06-2016.
 */
public class ChatStickerCategory implements Serializable
{
	String categoryId, categoryName, thumbImage, isFree, cost, isPurchesed,thumImageinactive;
	boolean isDownloaded;
	ArrayList<ChatSticker> chatStickerList;

	public String getThumImageinactive() {
		return thumImageinactive;
	}

	public void setThumImageinactive(String thumImageinactive) {
		this.thumImageinactive = thumImageinactive;
	}

	public String getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(String categoryId)
	{
		this.categoryId = categoryId;
	}

	public String getCategoryName()
	{
		return categoryName;
	}

	public void setCategoryName(String categoryName)
	{
		this.categoryName = categoryName;
	}

	public String getThumbImage()
	{
		return thumbImage;
	}

	public void setThumbImage(String thumbImage)
	{
		this.thumbImage = thumbImage;
	}

	public String getIsFree()
	{
		return isFree;
	}

	public void setIsFree(String isFree)
	{
		this.isFree = isFree;
	}

	public String getCost()
	{
		return cost;
	}

	public void setCost(String cost)
	{
		this.cost = cost;
	}

	public String getIsPurchesed()
	{
		return isPurchesed;
	}

	public void setIsPurchesed(String isPurchesed)
	{
		this.isPurchesed = isPurchesed;
	}

	public ArrayList<ChatSticker> getStickerList()
	{
		return chatStickerList;
	}

	public void setStickerList(ArrayList<ChatSticker> chatStickerList)
	{
		this.chatStickerList = chatStickerList;
	}

	public boolean getIsDownloaded()
	{
		return isDownloaded;
	}

	public void setIsDownloaded(boolean isDownloaded)
	{
		this.isDownloaded = isDownloaded;
	}

}
