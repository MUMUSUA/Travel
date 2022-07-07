package com.bignerdranch.android.travelrecord;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.travelrecord.Record.Record;
import com.bignerdranch.android.travelrecord.Record.RecordDbLab;
import com.bignerdranch.android.travelrecord.Record.RecordLab;
import com.zaaach.citypicker.CityPickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import static android.R.attr.name;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_CODE_PICK_CITY = 0;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private String username;
    private TextView mCityResult;
    private TextView mDate;
    private Button mChooseCity;
    private ImageButton mAddCity;
    private ImageView mPicture;
    private Uri imageUri;
    private Bitmap mBitmap;
    private Record mRecord;
    private RecordDbLab mRecordDbLab;
    private EditText mDesc;
    private UUID id;
    private CheckBox mCheckBox;
    private RecordLab mRecordLab=RecordLab.getInstance(getActivity());
    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Bundle bundle=new Bundle();
//
//        username=getArguments().getString("name");

        mRecordLab= RecordLab.getInstance(getActivity());
        Intent intent=getActivity().getIntent();
        id=(UUID)intent.getSerializableExtra("crime_id");
        Log.i("FirstFragment","------------------>"+id);
        if(id==null)
            mRecord=new Record();
        else
        mRecord=mRecordLab.getRecord(id);
        Log.i("FirstFragment","------------------>"+mRecord.getId());

        Log.i("FirstFragment","------------------>OK");
        mRecordDbLab= RecordDbLab.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first, container, false);
        mPicture=(ImageView)v.findViewById(R.id.travel_photo);
        mPicture.setImageResource(R.drawable.upload);
        mPicture.setOnClickListener(this);
        mChooseCity=(Button)v.findViewById(R.id.choose_city);
//        mChooseCity.setOnClickListener(this);
        mCityResult=(TextView)v.findViewById(R.id.choose_city_result);
        mCityResult.setOnClickListener(this);
        if(mRecord.getAddress()!=null)
        mCityResult.setText(mRecord.getAddress().toString());
        else
        mRecord.setAddress(mCityResult.getText().toString());

        mDate=(TextView)v.findViewById(R.id.info_date);
        mDate.setText(mRecord.getDate().toString());

        mDesc=(EditText)v.findViewById(R.id.desc);
        mDesc.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                mRecord.setDesc(c.toString());
                if(c.length()==0)
                    mAddCity.setEnabled(false);
                else
                    mAddCity.setEnabled(true);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank

            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        mCheckBox = (CheckBox)v.findViewById(R.id.ispublic);
        mCheckBox.setChecked(mRecord.isType());
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // set the crime's solved property
                mRecord.setType(isChecked);
            }
        });

        mAddCity=(ImageButton)v.findViewById(R.id.city_add);
        mAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
mRecord.setUser("LMT");

                Cursor cursor=mRecordDbLab.queryRecord(mRecord);
                cursor.moveToFirst();
                if(cursor.getCount()==0){
                    Log.i("RecordFragment","------------------>SendBefore");
                    mRecordDbLab.addRecord(mRecord);
                    Log.i("RecordFragment","------------------>SendAfter");}
                else{
                    Log.i("RecordFragment","------------------>UpdateBefore");
                    mRecordDbLab.updateRecord(mRecord);
                }
        }});
        return v;
    }


    private void setDialog() {
        Dialog mCameraDialog = new Dialog(this.getActivity(), R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this.getActivity()).inflate(
                R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
//        root.findViewById(R.id.btn_cancel).setOnClickListener(this);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.travel_photo:
                //弹出对话框
                setDialog();
                break;
            case R.id.choose_city_result:
                startActivityForResult(new Intent(getActivity(), CityPickerActivity.class),
                        REQUEST_CODE_PICK_CITY);

                break;
            case R.id.btn_choose_img:
                //选择照片按钮
                Toast.makeText(this.getActivity(), "请选择照片", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this.getActivity(), new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }

                break;
            case R.id.btn_open_camera:
                //拍照按钮
                Toast.makeText(getActivity(), "即将打开相机", Toast.LENGTH_SHORT).show();
                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(getContext().getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(outputImage);
                } else {
                    imageUri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.travelrecord.fileprovider", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;


        }
    }


    public Bitmap convertToBitmap(String base64String) {

        byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);

        Bitmap bitmapResult = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return bitmapResult;

    }


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(getActivity(), "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContext().getContentResolver().openInputStream(imageUri));
                        mPicture.setImageBitmap(bitmap);
                        mBitmap=bitmap;
                        mRecord.setPhotoId(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case REQUEST_CODE_PICK_CITY:
            if (resultCode == RESULT_OK){
                if (data != null){
                    String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                    mCityResult.setText(city);
                }
            }


            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            mBitmap=BitmapFactory.decodeFile(imagePath);
            mPicture.setImageBitmap(bitmap);
            mRecord.setPhotoId(mBitmap);

        } else {
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();

        }
    }


    public String convertToBase64(Bitmap bitmap) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG,100,os);

        byte[] byteArray = os.toByteArray();

        return Base64.encodeToString(byteArray, 0);

    }


}
