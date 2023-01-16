package com.example.success;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class showCorrectAnswer extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_correct_ans);
        Button next = (Button) findViewById(R.id.nextWord);
        next.getBackground().setAlpha(160);
        TextView chinese = (TextView)findViewById(R.id.chinese_ans);
        TextView english = (TextView) findViewById(R.id.English_ans);
        ImageView imageView = (ImageView) findViewById(R.id.wordPicture_ans);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        chinese.setText(bundle.getString("Chinese"));
        System.out.println(bundle.getString("Chinese"));
        english.setText(bundle.getString("English"));

        Button deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.getBackground().setAlpha(160);
        if(bundle.getBoolean("ShowTask")) {
            byte[] image = ShowTask.bytePicture;
            if(image==null) {
                imageView.setImageResource(R.drawable.beautiful);
            } else {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
            }
            next.setText("编辑");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Long id = bundle.getLong("id");
                    Intent edit = new Intent(showCorrectAnswer.this, Upload_Word.class);
                    edit.putExtra("id", id);
                    edit.putExtra("Chinese", bundle.getString("Chinese"));
                    edit.putExtra("English", bundle.getString("English"));
                    edit.putExtra("image", bundle.getByteArray("image"));
                    edit.putExtra("label", bundle.getStringArrayList("label"));
                    edit.putExtra("fromEdit",true);
                    startActivityForResult(edit,1);
                    finish();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(showCorrectAnswer.this);
                    dialog.setMessage("确认删除单词<" + bundle.getString("English") + ">吗?");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(false);            //点击对话框以外的区域是否让对话框消失

                    //设置正面按钮
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.db.deleteWord(MainActivity.name, bundle.getString("English"));
                            MainActivity.updateData();
                            // 删除单词
                            Toast.makeText(showCorrectAnswer.this, "已删除",
                                    Toast.LENGTH_LONG).show();
                            //TODO:数据库删除单词

                            dialog.dismiss();
                            finish();
                        }
                    });
                    //设置反面按钮
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(showCorrectAnswer.this, "你点击了取消", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        } else {
            byte[] image = EnglishTest.bytePicture;
            if(image==null) {
                imageView.setImageResource(R.drawable.beautiful);
            } else {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
            }
            deleteButton.setVisibility(View.INVISIBLE);
            next.setText("下一个");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.updateTest();
                    if (MainActivity.wordTestIndex < MainActivity.wordTestSum) {
                        Intent intent = new Intent(showCorrectAnswer.this, EnglishTest.class);
                        startActivity(intent);
                        finish(); //设置考察不能向前回退
                    } else {
                        if(MainActivity.knowledgeTestIndex < MainActivity.knowledgeTestSum) {
                            Intent intent = new Intent(showCorrectAnswer.this, ChineseTest.class);
                            startActivity(intent);
                            finish(); //设置考察不能向前回退
                        } else {
                            Toast.makeText(showCorrectAnswer.this, "考查任务已完成", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            });
        }
        System.out.println(bundle.getStringArrayList("label"));
        ListView listView = (ListView) findViewById(R.id.showWordLabel);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
               showCorrectAnswer.this, android.R.layout.simple_list_item_1,
                bundle.getStringArrayList("label"));
        listView.setAdapter(adapter);
        ImageView backToHome = (ImageView) findViewById(R.id.backToHome_word);
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(!bundle.getBoolean("ShowTask")) {
            backToHome.setVisibility(View.INVISIBLE);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    Bundle bundle = data.getExtras();
//                    TextView chinese = (TextView)findViewById(R.id.chinese_ans);
//                    TextView english = (TextView) findViewById(R.id.English_ans);
//                    ImageView imageView = (ImageView) findViewById(R.id.wordPicture_ans);
//                    chinese.setText(bundle.getString("Chinese"));
//                    System.out.println(bundle.getString("Chinese"));
//                    english.setText(bundle.getString("English"));
//                    byte[] image = bundle.getByteArray("image");
//                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
                }
                break;
            default:
        } }
}
