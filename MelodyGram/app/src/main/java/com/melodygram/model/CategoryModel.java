package com.melodygram.model;
/**
 * Created by LALIT on 15-06-2016.
 */
public class CategoryModel {
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
	
	String categoryId, categoryName, categoryThumb, categoryIsFree, categoryCost, categoryIsPurchesed;

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryThumb() {
		return categoryThumb;
	}

	public void setCategoryThumb(String categoryThumb) {
		this.categoryThumb = categoryThumb;
	}

	public String getCategoryIsFree() {
		return categoryIsFree;
	}

	public void setCategoryIsFree(String categoryIsFree) {
		this.categoryIsFree = categoryIsFree;
	}

	public String getCategoryCost() {
		return categoryCost;
	}

	public void setCategoryCost(String categoryCost) {
		this.categoryCost = categoryCost;
	}

	public String getCategoryIsPurchesed() {
		return categoryIsPurchesed;
	}

	public void setCategoryIsPurchesed(String categoryIsPurchesed) {
		this.categoryIsPurchesed = categoryIsPurchesed;
	}
}
