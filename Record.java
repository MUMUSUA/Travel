package com.bignerdranch.android.travelrecord.Record;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.UUID;

/**
 * Created by ASUS on 2022/7/5.
 */
//title text,date text,desc text,photo text,hint integer,type integer,like
public class Record {
    private UUID mId;
    private String mAddress;
    private  String mUser;
    private Date mDate;
    private String mDesc;
    private Bitmap photoId;
    private int hint;
    private int like;
    private boolean type;

    public Record() {
        mId = UUID.randomUUID();
        mDate = new Date();
        this.hint = 0;
        this.like = 0;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }



    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public Bitmap getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Bitmap photoId) {
        this.photoId = photoId;
    }

    public int getHint() {
        return hint;
    }

    public void setHint(int hint) {
        this.hint = hint;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
