package com.bignerdranch.android.travelrecord;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bignerdranch.android.travelrecord.Users.User;
import com.bignerdranch.android.travelrecord.Users.UserDBLab;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdFragment extends Fragment implements View.OnClickListener{


    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;

    private EditText mUsername;
    private EditText mPassword;
    private Button mButtonAlter;
    private Button mButtonDel;
    private ImageView picture;
    private Uri imageUri;
    private Bitmap b;
    private TextView mDialog;
    private Bitmap photoId;

    private Integer id;

    private String name;
    private Boolean alter = true;

    private User mUser;
    private UserDBLab mUserDBLab;
    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=new Bundle();
//        id= bundle.getInt("id");
        name=getArguments().getString("name");
        id=getArguments().getInt("id");
        Log.i("UserInfo------------->","id:"+id+"name:"+name);
        mUser = new User();
        mUser.setId(id);

        mUserDBLab = UserDBLab.getInstance(getContext());

        mUserDBLab.query(mUser);
        Log.i("UserInfo------------->","name:"+mUser.getUsername());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_third, container, false);




        mUserDBLab = UserDBLab.getInstance(getContext());
        mUsername = (EditText) v.findViewById(R.id.username1);
        mPassword = (EditText)v.findViewById(R.id.password1);
        mButtonAlter =(Button)v. findViewById(R.id.button_alter);
        mButtonDel = (Button) v.findViewById(R.id.button_del);
        picture = (ImageView) v.findViewById(R.id.imageView);

        mDialog=(TextView)v.findViewById(R.id.bottom_dialog);
        mDialog.setOnClickListener(this);

        mUsername.setText(mUser.getUsername().toString());
        mPassword.setText(mUser.getPassword().toString());

        mDialog.setEnabled(false);
        mUsername.setEnabled(false);
        mPassword.setEnabled(false);

        mButtonAlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alter) {
                    mDialog.setEnabled(true);
                    mPassword.setEnabled(true);
                    mButtonAlter.setText("保存");
                    alter = false;
                } else {
                    mUser.setPassword(mPassword.getText().toString());
                    mUserDBLab.update(mUser);
                    Toast.makeText(getActivity(), "修改成功！", Toast.LENGTH_LONG).show();
                    mDialog.setEnabled(false);
                    mPassword.setEnabled(false);
                    mButtonAlter.setText("修改");
                    alter = true;
                }
            }
        });

        mButtonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserDBLab.delete(mUser);
                getActivity().finish();
            }
        });

        if(mUser.getPhotoId()==null)
        {  picture.setImageResource(R.drawable.upload);
            picture.buildDrawingCache();
            b=picture.getDrawingCache();
            b=((BitmapDrawable)picture.getDrawable()).getBitmap();
            mUser.setPhotoId(b);}
        else
            picture.setImageBitmap(mUser.getPhotoId());

        return v;
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_dialog:
                //弹出对话框
                setDialog();
                break;
            case R.id.btn_choose_img:
                //选择照片按钮
                Toast.makeText(getActivity(), "请选择照片", Toast.LENGTH_SHORT).show();
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }

                break;
            case R.id.btn_open_camera:
                //拍照按钮
                Toast.makeText(getActivity(), "即将打开相机", Toast.LENGTH_SHORT).show();
                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(getActivity().getApplicationContext().getExternalCacheDir(), "output_image.jpg");
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

    private void setDialog() {
        Dialog mCameraDialog = new Dialog(getActivity(), R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getActivity()).inflate(
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
                        Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getApplicationContext().getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                        mUser.setPhotoId(bitmap);

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
            picture.setImageBitmap(bitmap);
            mUser.setPhotoId(bitmap);
        } else {
            Toast.makeText(getActivity(), "failed to get image", Toast.LENGTH_SHORT).show();

        }
    }


}
