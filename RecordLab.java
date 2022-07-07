package com.bignerdranch.android.travelrecord.Record;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ASUS on 2022/7/6.
 */




public class RecordLab {

    private List<Record> mRecords;
    private static  RecordLab sRecordLab;
    public static RecordLab getInstance(Context context) {
        if(sRecordLab==null){
            sRecordLab=new RecordLab(context);
        }

        return sRecordLab;
    }

    public static RecordLab getInstance() {

        return sRecordLab;
    }

    private RecordLab(Context context) {
        mRecords=new ArrayList<>();
    }

    public List<Record> getRecords() {
        return mRecords;
    }

    public  Record getRecord(UUID id){
        for(Record record:mRecords){
            if(record.getId().equals(id)){
                return record;
            }
        }
        return new Record();
    }

}
