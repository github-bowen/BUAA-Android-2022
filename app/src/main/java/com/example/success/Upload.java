package com.example.success;

import static android.view.View.INVISIBLE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.success.entity.Knowledge;
import com.example.success.entity.KnowledgeBlank;
import com.example.success.entity.Label;
import com.example.success.view.WheelView;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Upload extends AppCompatActivity {

    private final int TAKE_PHOTO_ORC = 1;
    private final int CHOOSE_PHOTO_OCR = 2;
    private DatabaseInterface db = MainActivity.db;

    private final int CHOOSE_TXT = 3;
    private final int TAKE_PHOTO = 4;
    private final int CHOOSE_PHOTO = 5;
    private Uri imageUri;
    private int requestCode = 0;
    private ImageView picture;
    private boolean initBase = false;
    private TessBaseAPI baseAPI;
    private EditText content;
    private EditText blank;
    private final MutableLiveData<String> progress = new MutableLiveData<>();
    private TextView process1;
    private ArrayList<String> blanks = new ArrayList<>();
    private WheelView wheelView;
    private List<String> lists = new ArrayList<>();
    private int visible = View.INVISIBLE;
    private EditText label_text;
    private EditText title;
    private Long id;
    private String oldContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        DatabaseInterface db = MainActivity.db;
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_upload);
        picture = findViewById(R.id.image_help);
        Button orc_button = findViewById(R.id.orc_photo);
        //orc_button.getBackground().setAlpha(30);
        content = findViewById(R.id.editTextTextMultiLine);
        process1 = findViewById(R.id.process1);
        blank = findViewById(R.id.blank);
        baseAPI = new TessBaseAPI();
        Assets.extractAssets(Upload.this);
        baseAPI = new TessBaseAPI(progressValues -> {
            progress.postValue("Progress: " + progressValues.getPercent() + " %");
        });
        title = findViewById(R.id.upload_tittle);
        // 导入所有标签
        lists.clear();
        lists.addAll(db.getAllLabel().stream().map
                (Label::getLabel).collect(Collectors.toList()));
//        for (int i = 0; i < 20; i++) {
//            lists.add("test:" + i);
//        }
        wheelView = (WheelView) findViewById(R.id.select);
        if (lists.size() >= 3) {
            wheelView.lists(lists)
                    .fontSize(45)
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
            lists.add("英语");
            lists.add("政治");
            lists.add("数学");
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
        }
        wheelView.setVisibility(visible);
        label_text = findViewById(R.id.select_label);
        if (getIntent() != null && getIntent().getExtras() != null) {
            id = getIntent().getExtras().getLong("id");  //获取知识点id
            if (id != null) {
                String title = null;
                if ((title = getIntent().getExtras().getString("title")) != null) {
                    this.title.setText(title);
                }
                String content = null;
                if ((content = getIntent().getExtras().getString("content")) != null) {
                    this.content.setText(content);
                    oldContent = content;
                }
                byte[] image = null;
                if ((image = ShowTask.bytePicture) != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    this.picture.setImageBitmap(bitmap);
                }
                ArrayList<String> labels = null;
                if ((labels = getIntent().getExtras().getStringArrayList("label")) != null) {
                    if(labels != null && labels.size() == 1) {
                        this.label_text.setText(labels.get(0));
                    }
                }
            }
        }
    }

    public void select_label(View view) {
        if (visible == View.INVISIBLE) {
            visible = View.VISIBLE;
        } else {
            visible = View.INVISIBLE;
            label_text.setText(lists.get(wheelView.getSelectItem()));
        }
        wheelView.setVisibility(visible);
    }

    public void ocr_click(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("请选择照片");
        builder.setPositiveButton("打开相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCode = CHOOSE_PHOTO_OCR;
                choosePhoto();
            }
        });
        builder.setNegativeButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestCode = TAKE_PHOTO_ORC;
                takePhoto();
            }
        });
        builder.show();
    }

    public void add_blank(View view) {
        if (!content.getText().toString().contains(blank.getText().toString())) {
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.
                    Builder(Upload.this);
            dialog.setMessage("记忆点<" + blank.getText() + ">在源文本内容中不存在\n请检查");
            dialog.show();
        } else {
            if(blank.getText().toString().isEmpty()) {
                Toast.makeText(Upload.this,
                        "记忆点不能为空" , Toast.LENGTH_SHORT).show();
            } else {
                blanks.add(blank.getText().toString());
                Toast.makeText(Upload.this,
                        "已成功添加记忆点<" + blank.getText().toString() + ">", Toast.LENGTH_SHORT).show();
                blank.setText("");
            }

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
            case TAKE_PHOTO_ORC:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        ocr(bitmap);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
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
                        try {
                            handleImageOnKitKat(data);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            handleImageBeforeKitKat(data);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case CHOOSE_TXT:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    System.out.println(uri.getAuthority());
                    String path = FileChooseUtil.getPath(Upload.this, uri);
                    File file = new File(path);
                    InputStreamReader reader = null;
                    String content = "";
                    try {
                        reader = new InputStreamReader(new FileInputStream(file));
                        BufferedReader in = new BufferedReader(reader);
                        String line;
                        while ((line = in.readLine()) != null) {
                            content = content + line;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.content.setText(content);
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

    private void ocr(Bitmap bitmap) throws InterruptedException {
        progress.observe(this, progress -> {
            process1.setText(progress);
        });
        // 将拍摄的照片显示出来
        //TODO 拍照读取图片为bitmap类型
        if (true) {
            try {
                baseAPI.init(Assets.getTessDataPath(Upload.this),
                        Assets.getLanguage(), TessBaseAPI.OEM_TESSERACT_ONLY);
                initBase = true;
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Cannot initialize Tesseract:", e);
            }
        }
        baseAPI.setDebug(true);
        AtomicReference<String> text = new AtomicReference<>();
        Thread a = new Thread(() -> {
            baseAPI.setImage(bitmap);
            baseAPI.getHOCRText(0);
            text.set(baseAPI.getUTF8Text());
            progress.postValue("");
        });
        a.start();
        a.join();
        content.setText(text.get());
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
        Assets.extractAssets(Upload.this);
        requestCode = CHOOSE_TXT;
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        intoFileManager();

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
    private void handleImageOnKitKat(Intent data) throws InterruptedException {

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
            if (requestCode == CHOOSE_PHOTO_OCR) {
                ocr(BitmapFactory.decodeFile(imagePath));
            } else {
                picture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            }
        }
    }

    private void handleImageBeforeKitKat(Intent data) throws InterruptedException {

        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        if (BitmapFactory.decodeFile(imagePath) == null) {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        } else {
            if (requestCode == CHOOSE_PHOTO_OCR) {
                ocr(BitmapFactory.decodeFile(imagePath));
            } else {
                picture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            }
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

    public void save_knowledge(View view) {
        //TODO 保存content，blanks，picture，label。
        String content = this.content.getText().toString();
        ArrayList<String> blanks = this.blanks;
        blanks.removeIf(blank -> !content.contains(blank));
        Bitmap bitmap = null;
        int result;
        if (picture.getDrawable() != null) {
            bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        }
        String label = label_text.getText().toString();
        String title = this.title.getText().toString();
        Intent intent = getIntent();
        if (intent.getBooleanExtra("fromEdit", false)) {
            //TODO: 编辑知识
//            byte[] image = new byte[3];
//            if (bitmap != null) {
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                image = baos.toByteArray();
//            }
//
//            Bundle bundle = new Bundle();
//            bundle.putString("content", content);
//            bundle.putByteArray("image", image);
//            bundle.putString("title", title);
//            bundle.putStringArrayList("blank", blanks);
//            ArrayList<String> labels = new ArrayList<>();
//            labels.add(label);
//            bundle.putStringArrayList("label", labels);
//            ret.putExtras(bundle);
            Intent ret = new Intent();

            result = db.updateKnowledge(MainActivity.name, oldContent, content, title, bitmap, label);
            if(result == 0) {
                Toast.makeText(this, "已有相同内容，修改失败！", Toast.LENGTH_SHORT).show();
            } else if (result == 2) {
                Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            } else if (result == 3) {
                Toast.makeText(this, "知识点内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                Knowledge knowledge = db.getKnowledgeByContent(MainActivity.name, content);
                db.insertKnowledgeBlank(knowledge, blanks);
                MainActivity.updateData();
                Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, ret);
                finish();
            }
        } else {
            result = db.insertKnowledge(MainActivity.name, content, title, bitmap);
            if(result == 0) {
                Toast.makeText(this, "已有相同内容，添加失败！", Toast.LENGTH_SHORT).show();
            } else if (result == 3) {
                Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
            } else if (result == 4) {
                Toast.makeText(this, "知识点内容不能为空", Toast.LENGTH_SHORT).show();
            } else {
                Knowledge knowledge = db.getKnowledgeByContent(MainActivity.name, content);
                db.knowledgeAddLabel(knowledge, label);
                db.insertKnowledgeBlank(knowledge, blanks);
                MainActivity.updateData();
                Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
                finish();
            }

        }


    }

    public void back(View view) {
        finish();
    }

}
