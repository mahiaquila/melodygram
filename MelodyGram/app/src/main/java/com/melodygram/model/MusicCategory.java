package com.melodygram.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by LALIT on 27-07-2016.
 */
public class MusicCategory implements Parcelable{

    private String catId;
    private String catName;
    private String tumbImage;
    private String isFree;
    private String cost;
    private String isPurchased;
    private String totalTracks;
    private ArrayList<Music> musicList;



    public MusicCategory() {

    }

    protected MusicCategory(Parcel in) {
        catId = in.readString();
        catName = in.readString();
        tumbImage = in.readString();
        isFree = in.readString();
        cost = in.readString();
        isPurchased = in.readString();
        totalTracks = in.readString();
    }

    public static final Creator<MusicCategory> CREATOR = new Creator<MusicCategory>() {
        @Override
        public MusicCategory createFromParcel(Parcel in) {
            return new MusicCategory(in);
        }

        @Override
        public MusicCategory[] newArray(int size) {
            return new MusicCategory[size];
        }
    };

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getTumbImage() {
        return tumbImage;
    }

    public void setTumbImage(String tumbImage) {
        this.tumbImage = tumbImage;
    }

    public String getIsFree() {
        return isFree;
    }

    public void setIsFree(String isFree) {
        this.isFree = isFree;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTotalTracks() {
        return totalTracks;
    }

    public void setTotalTracks(String totalTracks) {
        this.totalTracks = totalTracks;
    }





    public String getIsPurchased() {
        return isPurchased;
    }

    public void setIsPurchased(String isPurchased) {
        this.isPurchased = isPurchased;
    }

    public ArrayList<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(ArrayList<Music> musicList) {
        this.musicList = musicList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(catId);
        dest.writeString(catName);
        dest.writeString(tumbImage);
        dest.writeString(isFree);
        dest.writeString(cost);
        dest.writeString(isPurchased);
        dest.writeString(totalTracks);


    }
}
