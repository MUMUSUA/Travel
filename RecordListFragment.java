package com.bignerdranch.android.travelrecord.Record;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.travelrecord.R;
import com.bignerdranch.android.travelrecord.SecondFragment;
import com.bignerdranch.android.travelrecord.ThirdFragment;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class RecordListFragment extends Fragment {

    private RecyclerView mRecordRecyclerView;
    private RecordDbLab mRecordDbLab;
    private SwipeRefreshLayout swipeRefresh;

    public RecordListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_record_list, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        mRecordRecyclerView=(RecyclerView)v.findViewById(R.id.records_recycler_view);
       mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mRecordRecyclerView.setAdapter(new RecordAdapter(RecordLab.getInstance().getRecords(),this.getActivity()));
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

//        updateUI();
        return v;
    }







    private class RecordHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView mTitleTextView;
        private TextView mDetailTextView;
        private ImageView mImageView;
        private Button share;
        private Button readMore;
        public TextView getTitleTextView() {
            return mTitleTextView;
        }

        public TextView getDetailTextView() {
            return mDetailTextView;
        }

        public ImageView getImageView() {
            return mImageView;
        }


        public RecordHolder(View itemView){

            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            mTitleTextView=(TextView)itemView.findViewById(R.id.painting_title);
            mDetailTextView=(TextView)itemView.findViewById(R.id.painting_desc);
            mImageView = (ImageView) itemView.findViewById(R.id.painting_photo);
//            share = (Button) itemView.findViewById(R.id.btn_share);
            readMore = (Button) itemView.findViewById(R.id.btn_more);

            // 设置TextView背景为半透明
            mTitleTextView.setBackgroundColor(Color.argb(20, 0, 0, 0));
        }
    }



    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder>{
        private List<Record> mRecords;
        private Context context;
        //    public RecordAdapter(List<Record> crimes){
//        mCrimes=crimes;
//    }
        public RecordAdapter(List<Record> records, Context context) {
            super();
            this.mRecords = records;
            this.context = context;
        }
        @Override
        public int getItemCount() {
            return mRecords.size();
        }

        @Override
        public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
//        View view=layoutInflater.inflate(R.layout.list_item_record,parent,false);

            View view = LayoutInflater.from(context).inflate(R.layout.list_item_record,
                    null);
            RecordHolder nvh = new RecordHolder(view);

//        return new CrimeHolder(view);
            return  nvh;
        }

        @Override
        public void onBindViewHolder(RecordHolder holder, final int position) {
            final int j = position;
            Record crime=mRecords.get(position);
//            holder.mTitleTextView.setText(crime.getTitle());
            holder.mDetailTextView.setText(crime.getAddress()+"\n"+crime.getUser());
            holder.mImageView.setImageBitmap(crime.getPhotoId());
//        holder.mSolvedCheckBox.setChecked(crime.isSolved());

            holder.cardView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
//                Intent intent=new Intent(getActivity(),ShowActivity.class);
                    mRecords.get(position).setHint(mRecords.get(position).getHint()+1);
                    mRecordDbLab.updateRecord(mRecords.get(position));
                    Intent intent=new Intent(getActivity(),EnjoyActivity.class);
//                Intent intent=new Intent(getActivity(),CrimeActivity.class);
                    intent.putExtra("crime_id",mRecords.get(position).getId());
//                intent.putExtra("Painting",mCrimes.get(j));
                    startActivity(intent);
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder mDialog = new AlertDialog.Builder(RecordListFragment.this.getActivity());
                    mDialog.setTitle("删除");
                    mDialog.setMessage("确定要删除吗?");
                    mDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Log.i("RecordListFragment","------------------>position:"+position);
                            Log.i("RecordListFragment","------------------>..............");
                            Toast.makeText(getActivity(),"删除成功!",Toast.LENGTH_SHORT).show();
                            Log.i("RecordListFragment","------------------>uuid:"+mRecords.get(position).getId());
                            mRecordDbLab.deleteRecord(mRecords.get(position).getId().toString());
                            mRecords.remove(position);
                            // startActivity(new Intent(getActivity(),RecordListActivity.class));
                        }
                    });
                    mDialog.setNegativeButton("取消", null);
                    mDialog.show();
                    return true;
                }
            });


//            holder.share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
//                    intent.putExtra(Intent.EXTRA_TEXT, mRecords.get(j).getDesc());
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    context.startActivity(Intent.createChooser(intent, mRecords
////                            .get(j).getTitle()));
//                }
//            });

            holder.readMore
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getActivity(),EnjoyActivity.class);
//                Intent intent=new Intent(getActivity(),RecordActivity.class);
                            intent.putExtra("crime_id",mRecords.get(position).getId());
//                intent.putExtra("Painting",mCrimes.get(j));
                            startActivity(intent);
                        }
                    });
        }


    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.fragment_record_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.menu_item0:
//                startActivity(new Intent(getActivity(),CrimeActivity.class));
//
//                break;
//            case R.id.menu_item1:
//                Toast.makeText(getActivity(),"Music stop!",Toast.LENGTH_SHORT).show();
//                getActivity().stopService(new Intent(getActivity(),MusicPlayService.class));
////                mMediaPlayer.stop();
//                break;
//            case R.id.menu_item5:
//                Toast.makeText(getActivity(),"Music play!",Toast.LENGTH_SHORT).show();
////                mMediaPlayer=MediaPlayer.create(getActivity(),R.raw.melody);
//                getActivity().startService(new Intent(getActivity(),MusicPlayService.class));
////                mMediaPlayer.start();
//                break;
//
//            default:break;
//
//        }
        return super.onOptionsItemSelected(item);

    }
    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(new Intent(getActivity(),RecordListActivity.class));
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public Bitmap convertToBitmap(String base64String) {

        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);

        Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bitmapResult;

    }
}
