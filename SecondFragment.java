package com.bignerdranch.android.travelrecord;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.bignerdranch.android.travelrecord.Record.Record;
import com.bignerdranch.android.travelrecord.Record.RecordDbLab;
import com.bignerdranch.android.travelrecord.Record.RecordLab;
import com.bignerdranch.android.travelrecord.Record.RecordListActivity;
import java.util.Date;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {

    private RecyclerView mRecordRecyclerView;
    private RecordDbLab mRecordDbLab;
    private MediaPlayer mMediaPlayer;
    private SwipeRefreshLayout swipeRefresh;
    private RecordLab mRecordLab=RecordLab.getInstance(this.getActivity());
    private Button mViewRecords;
    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecordDbLab=RecordDbLab.getInstance(getActivity());
        setHasOptionsMenu(true);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_second, container, false);
        mViewRecords=(Button)v.findViewById(R.id.view_records);
        mViewRecords.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mRecordLab.getRecords().clear();
                Log.i("RecordFragment","------------------>mCrimeLab.getCrimes().size():"+mRecordLab.getRecords().size());
                Cursor cursor=mRecordDbLab.queryRecords(null,null);
                if(cursor==null)
                    Toast.makeText(getActivity(),"cursor is null!",Toast.LENGTH_SHORT).show();
                else{

                    //  Toast.makeText(getActivity(),"cursor's size is"+cursor.getCount(),Toast.LENGTH_SHORT).show();
                    cursor.moveToFirst();
                    while(!cursor.isAfterLast()){


                        Log.i("RecordFragment","------------------>date:"+cursor.getLong(cursor.getColumnIndex("date")));
                        Record c=new Record();
                        c.setId(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
                        c.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                        c.setUser(cursor.getString(cursor.getColumnIndex("user")));
                        c.setDate(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
                        c.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
                        c.setPhotoId(convertToBitmap(cursor.getString(cursor.getColumnIndex("photo"))));
                        c.setHint(cursor.getInt(cursor.getColumnIndex("hint")));
                        c.setType(cursor.getInt(cursor.getColumnIndex("type"))!=0);
                        c.setLike(cursor.getInt(cursor.getColumnIndex("like")));


                        cursor.moveToNext();
                        mRecordLab.getRecords().add(c);
                    }
                }
                startActivity(new Intent(getActivity(), RecordListActivity.class));
            }

        });


        return v;
    }






    public Bitmap convertToBitmap(String base64String) {

        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);

        Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bitmapResult;

    }

}