package com.example.success;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.googlecode.tesseract.android.TessBaseAPI;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class takePhoto extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CHOOSE_TXT = 3;
    private ImageView picture;
    private TextView process;
    private Uri imageUri;
    private boolean initBase = false;
    private final MutableLiveData<String> progress = new MutableLiveData<>();
    private TessBaseAPI baseAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        picture = findViewById(R.id.get_imageView);
        process = findViewById(R.id.process);
        Assets.extractAssets(takePhoto.this);
        baseAPI = new TessBaseAPI(progressValues -> {
            progress.postValue("Progress: " + progressValues.getPercent() + " %");
        });
    }

    public void takePhoto(View view) {

        // 创建File对象，用于存储拍照后的图片，将之存放在手机SD卡的应用关联缓存目录下
        // 调用getExternalCacheDir()方法可以得到该目录

        File outputImage = new File(getExternalCacheDir(),"output_image.jpg");
        System.out.println(getExternalCacheDir().toString());
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 进行判断，如果运行设备版本低于Android 7.0就调用Uri的fromFile()方法将File对象转换成Uri对象
        // 否则，就调用FileProvider的getUriForFile()方法将File对象转换成一个封装过的Uri对象
        // 该方法接收三个参数，第一个要求为Context对象，第二个为任意字符串，第三为刚创建的File对象
        // FileProvider是一种特殊的内容提供器，可以选择性地将封装过的Uri共享给外部，从而提高的应用的安全性
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(this,"com.example.cameraalbumtest.fileprovider",outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        System.out.println("imageUri:::" + imageUri.toString());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},2);
        } else {
            openCamera();
        }

        // 启动相机程序

    }

    private void ocr(Bitmap bitmap) {
        try {
            // 将拍摄的照片显示出来
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            //TODO 拍照读取图片为bitmap类型
            if (!initBase) {
                try {
                    baseAPI.init(Assets.getTessDataPath(takePhoto.this),
                            Assets.getLanguage(), TessBaseAPI.OEM_TESSERACT_ONLY);
                    initBase = true;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Cannot initialize Tesseract:", e);

                }
            }
            baseAPI.setDebug(true);
            Bitmap finalBitmap = bitmap;
            new Thread(() -> {
                baseAPI.setImage(finalBitmap);
                baseAPI.getHOCRText(0);
                String text = baseAPI.getUTF8Text();
                //TODO 处理识别出来的文本
                System.out.println(text);
                progress.postValue("");
            }).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    picture.setImageBitmap(bitmap); //展示图片
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // 判断手机版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        assert data != null;
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case CHOOSE_TXT:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String path = FileChooseUtil.getPath(takePhoto.this, uri);
                    File file = new File(path);
                    InputStreamReader reader = null;
                    String content = "";
                    try {
                        reader = new InputStreamReader(new FileInputStream(file));
                        BufferedReader in = new BufferedReader(reader);
                        String line;
                        while((line = in.readLine()) != null) {
                            content = content + line;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(content);
                    //TODO 把content放入文本框。
                }
                break;
            default:
                break;
        }
    }



    public void choosePhoto(View view) {

        // 在点击事件里动态申请WRITE_EXTERNAL_STORAGE这个危险权限。表示同时授予程序对SD卡读和写的能力
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        } else {
            openAlbum();
        }

    }

    public void chooseTxt(View view) {
        //测试，把txt拷贝到手机中
        Assets.extractAssets(takePhoto.this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        } else {
            intoFileManager();
        }
    }

    private void intoFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");//无类型限制
//        有类型限制是这样的:
//        intent.setType(“image/*”);//选择图片
//        intent.setType(“audio/*”); //选择音频
//        intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//        intent.setType(“video/*;image/*”);//同时选择视频和图片
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CHOOSE_TXT);
    }

    private void openAlbum() {
        // 给intent设置一些必要的参数
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    private void openCamera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {

        String imagePath = null;
        Uri uri = data.getData();

        if (DocumentsContract.isDocumentUri(this,uri)) {
            // 如果是document类型的Uri，则通过document的id处理
            String docId = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            // 如果Uri的authority是meida的格式的话，document需要再进行一次解析，通过字符串分割的方式取出真正的数字id
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                // 解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri,null);
            }

        } else {
            assert uri != null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果是content类型的uri，则使用普通方式
                imagePath = getImagePath(uri,null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
        }
        displayImage(imagePath);

    }

    private void handleImageBeforeKitKat(Intent data) {

        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);

    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    // 将图片显示在界面上
    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //TODO 相册选择图片为bitmap类型
            picture.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this,"failed to get image",Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        progress.observe(this, progress -> {
            process.setText(progress);
        });
    }
}