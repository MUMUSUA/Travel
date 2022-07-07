package com.bignerdranch.android.travelrecord.Record;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bignerdranch.android.travelrecord.R;

public class RecordListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        Log.i("RecordListActivity","------------------>CrimeListActivityCrimeListActivity");
                FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_list_Container);

        if (fragment == null) {
            fragment = new RecordListFragment();
            manager.beginTransaction()
                    .add(R.id.fragment_list_Container, fragment)
                    .commit();
        }
    }
}
