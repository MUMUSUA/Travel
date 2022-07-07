package com.bignerdranch.android.travelrecord.Record;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import java.io.ByteArrayOutputStream;

/**
 * Created by ASUS on 2022/6/21.
 */

public class RecordDbLab {
    private static RecordDbLab ourInstance;
    private SQLiteDatabase mDatabase;

    public static RecordDbLab getInstance(Context context) {
        if(ourInstance==null){
            ourInstance=new RecordDbLab(context);
        }

        return ourInstance;
    }

    private RecordDbLab(Context context) {
        mDatabase=new RecordBaseHelper(context.getApplicationContext()).getWritableDatabase();
    }

    public void addRecord(Record record){
        Log.i("CrimeDatabase","------------------>addStart");
        ContentValues values=new ContentValues();

        values.put("uuid",record.getId().toString());
        values.put("address",record.getAddress().toString());
        values.put("user",record.getUser().toString());
        values.put("date",record.getDate().getTime());
        values.put("desc",record.getDesc());
        values.put("photo",convertToBase64(record.getPhotoId()));
        values.put("type",record.isType()?1:0);
        values.put("hint",record.getHint());
        values.put("like",record.getLike());

        Log.i("RecordTable","------------------>addBefore");
        mDatabase.insert("record",null,values);
        Log.i("RecordTable","------------------>addAfter");

    }


    public Cursor queryRecords(String whereClause,String[] whereArgs){
        Cursor cursor=mDatabase.query("record",
                new String[]{"uuid","address","user","date","desc","photo","hint","type","like"},
                whereClause,whereArgs,
                null,null,null
                );


        return cursor;

    }


    public Cursor queryRecord(Record record){
        Cursor cursor=mDatabase.query("record",
                new String[]{"uuid","address","user","date","desc","photo","hint","type","like"},
                "uuid=?",
                new String[]{record.getId().toString()},
                null,null,null
        );




        return cursor;

    }

    public Cursor searchRecord(String sql){

//        String sql="select * from record where address like \"%"+s+"%\" or desc like \"%"+s+"%\""
//                +" or user like \"%" + s + "%\"";
        Cursor cursor=mDatabase.rawQuery(sql,null);

        return cursor;

    }



    public void updateRecord(Record record){

        String uuidString=record.getId().toString();

        ContentValues values=new ContentValues();
        values.put("address",record.getAddress().toString());
        values.put("user",record.getUser().toString());
        values.put("date",record.getDate().getTime());
        values.put("desc",record.getDesc());
        values.put("photo",convertToBase64(record.getPhotoId()));
        values.put("type",record.isType()?1:0);
        values.put("hint",record.getHint());
        values.put("like",record.getDesc());
        
        mDatabase.update("record",values,"uuid=?",new String[]{uuidString});
    }

    public void deleteRecord(String id){

        mDatabase.delete("record","uuid=?",new String[]{id});
    }


    public String convertToBase64(Bitmap bitmap) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100,os);

        byte[] byteArray = os.toByteArray();

        return Base64.encodeToString(byteArray, 0);

    }


}
