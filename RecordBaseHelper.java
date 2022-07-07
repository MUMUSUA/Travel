package com.bignerdranch.android.travelrecord.Record;

/**
 * Created by ASUS on 2022/6/19.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class RecordBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION=1;
    private static final String DATABASE_NAME="Travel.db";
    public RecordBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
        Log.i("RecordBaseHelper","------------------>RecordBaseHelper");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("Database","------------------>Before");
        sqLiteDatabase.execSQL("create table record(_id integer primary key " +
                "autoincrement,uuid text,address text,user text,date text,desc text,photo text,hint integer,type integer,like integer)"
        );

        Log.i("Database","------------------>After");

    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}