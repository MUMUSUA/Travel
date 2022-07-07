package com.bignerdranch.android.travelrecord;


import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bignerdranch.android.travelrecord.Record.Record;
import com.bignerdranch.android.travelrecord.Record.RecordDbLab;
import com.bignerdranch.android.travelrecord.Record.RecordLab;
import com.bignerdranch.android.travelrecord.Record.RecordListActivity;
import com.bignerdranch.android.travelrecord.Record.addRecordActivity;
import com.bignerdranch.android.travelrecord.Users.LoginActivity;
import com.bignerdranch.android.travelrecord.Users.User;
import com.bignerdranch.android.travelrecord.Users.UserDBLab;


import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;

import static android.R.attr.id;
import static com.mob.MobSDK.getContext;
import static com.mob.commons.v.s;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static String name;
    public static Integer userid;
    private TextView username;
    private ImageView profile;
    private User mUser;
    private UserDBLab mUserDBLab;
    private Fragment fragment;
    private ViewFlipper flipper;
    private EditText mSql;
    private ImageButton search;
    private RecordDbLab mRecordDbLab;
    private RecordLab mRecordLab=RecordLab.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

        mRecordDbLab=RecordDbLab.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userid=getIntent().getIntExtra("id",0);
        name=getIntent().getStringExtra("name");
        Log.i("HomeActivity--->","id:"+id+"name:"+name);
        mUser = new User();
        mUser.setId(userid);
        mUserDBLab = UserDBLab.getInstance(getContext());
        mUserDBLab.query(mUser);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        flipper.startFlipping();

        mSql=(EditText)findViewById(R.id.sql_EditText);

        search=(ImageButton)findViewById(R.id.search_ImageButton);
search.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        mRecordLab.getRecords().clear();
        Log.i("RecordFragment", "------------------>mCrimeLab.getCrimes().size():" + mRecordLab.getRecords().size());
        if (mSql.getText().toString().trim().length() == 0)
            Toast.makeText(HomeActivity.this, "输入不可为空！", Toast.LENGTH_SHORT).show();
        else {
         String sql="select * from record where address like \"%"+mSql.getText().toString()+"%\" or desc like \"%"+mSql.getText().toString()+"%\""
                    +" or user like \"%" + mSql.getText().toString() + "%\"";
        Cursor cursor = mRecordDbLab.searchRecord(sql);
        if (cursor.getCount() == 0)
            Toast.makeText(HomeActivity.this, "暂无匹配项!", Toast.LENGTH_SHORT).show();
        else {

            //  Toast.makeText(getActivity(),"cursor's size is"+cursor.getCount(),Toast.LENGTH_SHORT).show();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {


                Log.i("RecordFragment", "------------------>date:" + cursor.getLong(cursor.getColumnIndex("date")));
                Record c = new Record();
                c.setId(UUID.fromString(cursor.getString(cursor.getColumnIndex("uuid"))));
                c.setAddress(cursor.getString(cursor.getColumnIndex("address")));
                c.setUser(cursor.getString(cursor.getColumnIndex("user")));
                c.setDate(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
                c.setDesc(cursor.getString(cursor.getColumnIndex("desc")));
                c.setPhotoId(convertToBitmap(cursor.getString(cursor.getColumnIndex("photo"))));
                c.setHint(cursor.getInt(cursor.getColumnIndex("hint")));
                c.setType(cursor.getInt(cursor.getColumnIndex("type")) != 0);
                c.setLike(cursor.getInt(cursor.getColumnIndex("like")));


                cursor.moveToNext();
                mRecordLab.getRecords().add(c);
            }
            Intent intent2 = new Intent(HomeActivity.this, RecordListActivity.class);
            intent2.putExtra("name",name);
            startActivity(intent2);

        }
    }
    }
});

        Log.i("mUserDBLab--->","id:"+mUser.getId());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, addRecordActivity.class);
                intent.putExtra("name",name);
                startActivity(intent);
//                startActivity(new Intent(HomeActivity.this,addRecordActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View nav=navigationView.inflateHeaderView(R.layout.nav_header_home);
        profile=(ImageView) nav.findViewById(R.id.imageView);
        username=(TextView) nav.findViewById(R.id.username_TextView);
        username.setText("用户名:"+name);
        profile.setImageBitmap(mUser.getPhotoId());
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onBackPressed() {
        LinearLayout layout= (LinearLayout) findViewById(R.id.content_main);

        layout.setVisibility(View.VISIBLE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public  boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            fragment=new SecondFragment();
        } else if (id == R.id.nav_gallery) {
            fragment=new ThirdFragment();
        } else if (id == R.id.nav_slideshow) {
            fragment=new SecondFragment();
        } else if (id == R.id.nav_manage) {
            fragment=new SecondFragment();
        } else if (id == R.id.nav_share) {
            fragment=new SecondFragment();
        } else if (id == R.id.nav_send) {
            fragment=new SecondFragment();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putInt("id", userid);
            bundle.putString("name", name);
            Log.i("Args------------->","id:"+userid+"name:"+name);
            fragment.setArguments(bundle);
            //方法一传参数
            fragment.instantiate(this, ThirdFragment.class.getName(), bundle);
            transaction.addToBackStack(null);
//            transaction.replace(R.id.content_main,fragment);
            transaction.replace(R.id.drawer_layout, fragment);

            transaction.commit();
        }
        LinearLayout layout= (LinearLayout) findViewById(R.id.content_main);

        layout.setVisibility(View.GONE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public Bitmap convertToBitmap(String base64String) {

        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);

        Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bitmapResult;

    }




}
