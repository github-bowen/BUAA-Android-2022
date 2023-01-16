package com.example.success;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.success.entity.Label;
import com.example.success.entity.Word;
import com.example.success.translate.TranslateAPI;
import com.example.success.view.WheelView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Upload_Word extends Activity {

    private final int TAKE_PHOTO_ORC = 1;
    private final int CHOOSE_PHOTO_OCR = 2;
    private DatabaseInterface db = MainActivity.db;

    private final int CHOOSE_TXT = 3;
    private final int TAKE_PHOTO = 4;
    private final int CHOOSE_PHOTO = 5;
    private Uri imageUri;
    private int requestCode = 0;
    private ImageView picture;
    private EditText english_word;
    private EditText chinese;
    private WheelView wheelView;
    private int visible = View.INVISIBLE;
    private List<String> lists = new ArrayList<>();
    private EditText label1;
    //private EditText label2;
    private String oldEnglish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload_word);
        english_word = findViewById(R.id.english_word);
        chinese = findViewById(R.id.chinese_word);
        //TODO （单词）导入所有标签
        lists.clear();
        lists.addAll(db.getAllLabel().stream().map
                (Label::getLabel).collect(Collectors.toList()));
//        for (int i = 0; i < 20; i++) {
//            lists.add("test:" + i);
//        }
        wheelView = (WheelView) findViewById(R.id.select_wheel2);
        if (lists.size() >= 3) {
            wheelView.lists(lists)
                    .fontSize(65)
                    .showCount(3)
                    .select(0)
                    .listener(new WheelView.OnWheelViewItemSelectListener() {
                        @Override
                        public void onItemSelect(int index) {
                            Log.d("cc", "current select:" + wheelView.getSelectItem() + " index :" + index + ",result=" + lists.get(index));
                        }
                    })
                    .build();
        } else {
            lists.add("雅思");
            lists.add("托福");
            lists.add("四级");
            wheelView.lists(lists)
                    .fontSize(35)
                    .showCount(3)
                    .select(0)
                    .listener(new WheelView.OnWheelViewItemSelectListener() {
                        @Override
                        public void onItemSelect(int index) {
                            Log.d("cc", "current select:" + wheelView.getSelectItem() + " index :" + index + ",result=" + lists.get(index));
                        }
                    })
                    .build();
        }
        wheelView.setVisibility(visible);
        label1 = findViewById(R.id.word_label1);
        //label2 = findViewById(R.id.word_label2);
        picture = findViewById(R.id.image_help2);
        Long id = getIntent().getExtras().getLong("id");
        if (id != null) {
            String chinese = null;
            if ((chinese = getIntent().getExtras().getString("Chinese")) != null) {
                this.chinese.setText(chinese);
            }
            String english = null;
            if ((english = getIntent().getExtras().getString("English")) != null) {
                this.english_word.setText(english);
                oldEnglish = english;
            }
            byte[] image = null;
            if ((image = ShowTask.bytePicture) != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                this.picture.setImageBitmap(bitmap);
            }
            ArrayList<String> labels = null;
            if ((labels = getIntent().getExtras().getStringArrayList("label")) != null) {
                if(labels != null && labels.size() == 1) {
                    this.label1.setText(labels.get(0));
                }
            }
        }
    }

    public void select1(View view) {
        if (visible == View.INVISIBLE) {
            visible = View.VISIBLE;
        } else {
            visible = View.INVISIBLE;
            label1.setText(lists.get(wheelView.getSelectItem()));
        }
        wheelView.setVisibility(visible);
    }

//    public void select2(View view) {
//        if (visible == View.INVISIBLE) {
//            visible = View.VISIBLE;
//        } else {
//            visible = View.INVISIBLE;
//            label2.setText(lists.get(wheelView.getSelectItem()));
//        }
//        wheelView.setVisibility(visible);
//    }

    public void translate(View view) {
        String a = TranslateAPI.getChinese(english_word.getText().toString());
        if (a.length() > 70) {
            a = a.substring(0, 70);
            int i;
            for (i = 69; i >= 0; i--) {
                if (a.charAt(i) == '；' || a.charAt(i) == '。') {
                    break;
                }
            }
            a = a.substring(0, i + 1);
        }
        chinese.setText(a);
        if (a.equals("")) {
            Toast.makeText(Upload_Word.this, "获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    public void add_photo(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请选择照片");
        builder.setPositiveButton("打开相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCode = CHOOSE_PHOTO;
                choosePhoto();
            }
        });
        builder.setNegativeButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCode = TAKE_PHOTO;
                takePhoto();
            }
        });
        builder.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (this.requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        //System.out.println("TAKE_PHOTO" + bitmap);
                        picture.setImageBitmap(bitmap);
                        //System.out.println("picture" + ((BitmapDrawable)picture.getDrawable()).getBitmap());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO_OCR:
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
            default:
                break;
        }
    }

    public void takePhoto() {

        // 创建File对象，用于存储拍照后的图片，将之存放在手机SD卡的应用关联缓存目录下
        // 调用getExternalCacheDir()方法可以得到该目录

        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
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
            imageUri = FileProvider.getUriForFile(this, "com.example.cameraalbumtest.fileprovider", outputImage);

        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        System.out.println("imageUri:::" + imageUri.toString());
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
        } else {
            openCamera();
        }

        // 启动相机程序
    }


    public void choosePhoto() {

        // 在点击事件里动态申请WRITE_EXTERNAL_STORAGE这个危险权限。表示同时授予程序对SD卡读和写的能力
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("image/*");
            startActivityForResult(intent, requestCode);
        }

    }

    private void openAlbum() {
        // 给intent设置一些必要的参数
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    public void chooseTxt(View view) {
        //测试，把txt拷贝到手机中
        Assets.extractAssets(Upload_Word.this);
        requestCode = CHOOSE_TXT;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            intoFileManager();
        }
    }

    private void intoFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");//无类型限制
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, requestCode);
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

        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document的id处理
            String docId = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            // 如果Uri的authority是meida的格式的话，document需要再进行一次解析，通过字符串分割的方式取出真正的数字id
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                // 解析出数字格式的id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            }

        } else {
            assert uri != null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果是content类型的uri，则使用普通方式
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
        }
        if (BitmapFactory.decodeFile(imagePath) == null) {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        } else {
            picture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        if (BitmapFactory.decodeFile(imagePath) == null) {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        } else {
            picture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public void save_word(View view) {
        //TODO 保存英文单词
        int result;
        String word = english_word.getText().toString();
        String chinese = this.chinese.getText().toString();
        Bitmap bitmap = null;
        if (picture.getDrawable() != null) {
            bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        }
        System.out.println("bitmap" + bitmap);
        String labelContent = String.valueOf(label1.getText());
        //System.out.println("label:" + labelContent);
        Intent intent = getIntent();
        if (intent.getBooleanExtra("fromEdit", false)) {
            //TODO: 编辑
            //System.out.println(bitmap);
//            byte[] data = new byte[3];
//            if (bitmap != null) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                data = baos.toByteArray();
//            }
            Intent ret = new Intent();
            //Bundle bundle = new Bundle();
            result = db.updateWord(MainActivity.name, oldEnglish, chinese, word, bitmap, labelContent);
            if(result == 0) {
                Toast.makeText(this, "单词英文不能为空", Toast.LENGTH_SHORT).show();
            } else if (result == 2) {
                Toast.makeText(this, "已有相同单词，修改失败", Toast.LENGTH_SHORT).show();
            } else {
                setResult(RESULT_OK, ret);
                MainActivity.updateData();
                Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
                finish();
            }


        } else {
            result = db.insertWord(MainActivity.name, chinese, word, bitmap);
            if(result == 3) {
                Toast.makeText(this, "单词英文不能为空", Toast.LENGTH_SHORT).show();
            } else if (result == 0){
                Toast.makeText(this, "已有相同单词，添加失败", Toast.LENGTH_SHORT).show();
            } else{
                Word word1 = db.getWordByEnglish(MainActivity.name, word);
                db.wordAddLabel(word1, labelContent);
                MainActivity.updateData();
                Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
                finish();
            }

        }

    }

    public void back2(View view) {
        finish();
    }
}