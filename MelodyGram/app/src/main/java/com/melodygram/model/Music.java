package com.melodygram.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LALIT on 27-07-2016.
 */
public class Music implements Parcelable{

    private String id;
    private String catId;
    private String name;
    private String photo;
    private String music;
    private String localPath;

    public Music() {

    }

    protected Music(Parcel in) {
        id = in.readString();
        catId = in.readString();
        name = in.readString();
        photo = in.readString();
        music = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(catId);
        dest.writeString(name);
        dest.writeString(photo);
        dest.writeString(music);
    }
}
