package com.example.success;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;

public class showCorrectKnowledge extends Activity {
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_correct_knowledge);
        Intent knowledgeDetail = getIntent();
        Bundle bundle = knowledgeDetail.getExtras();
        String content = bundle.getString("content");
        System.out.println("Content:   :::" + content);
        String title = bundle.getString("title");
        ArrayList<String> blanks = bundle.getStringArrayList("blank");
        boolean formShowTask = bundle.getBoolean("ShowTask");
        ArrayList<String> labels = bundle.getStringArrayList("label");
        SwitchCompat showBlank = (SwitchCompat) findViewById(R.id.showBlank);
        String replaced = new String(content);
        for (String blank : blanks) {
            System.out.println(blank);
            replaced = replaced.replace(blank, "___");
        }
        String finalReplaced = replaced;
        SpannableStringBuilder spannableString = new SpannableStringBuilder(content);
        int lastPos = 0;
        for (int i = 0; i < blanks.size(); i++) {
            System.out.println(blanks.get(i));
            System.out.println(content.contains(blanks.get(i)));
            System.out.println(content.indexOf(blanks.get(i)));
            int index = content.indexOf(blanks.get(i));
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#fff799"));
            spannableString.setSpan(backgroundColorSpan, index, index + blanks.get(i).length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            UnderlineSpan underlineSpan = new UnderlineSpan();
            spannableString.setSpan(underlineSpan, index, index + blanks.get(i).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);//粗体
            StyleSpan styleSpan_I = new StyleSpan(Typeface.ITALIC);//斜体
            spannableString.setSpan(styleSpan_B, index, index + blanks.get(i).length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(styleSpan_I, index, index + blanks.get(i).length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            lastPos = index + blanks.get(i).length();
        }
        TextView knowledge = (TextView) findViewById(R.id.knowledge_correct);
        knowledge.setText(spannableString);
        ImageView photo = (ImageView) findViewById(R.id.photo_knowledge_c);
        ImageView home = (ImageView) findViewById(R.id.backToHome_correct);
        TextView knowledgeTitle = (TextView) findViewById(R.id.title_knowledge_correct);
        knowledgeTitle.setText(title);
        showBlank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) { //当前在隐藏
                    knowledge.setText(finalReplaced);
                    //TODO：对每个挖空内容画横线展示，一个空___
                    showBlank.setText("显示记忆点");
                } else {
                    knowledge.setText(spannableString);
                    showBlank.setText("隐藏记忆点");
                }
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button correctButton = (Button) findViewById(R.id.next_correct);
        correctButton.getBackground().setAlpha(160);
        Button deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.getBackground().setAlpha(160);
        if (formShowTask) {
            byte[] image = ShowTask.bytePicture;
            if (image == null) {
                photo.setImageResource(R.drawable.ice);
            } else {
                photo.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            showBlank.setVisibility(View.VISIBLE);
            correctButton.setText("编辑");
            correctButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Long id = bundle.getLong("id");
//                    System.out.println("Id" + id);
                    Intent edit = new Intent(showCorrectKnowledge.this, Upload.class);
                    Long id = bundle.getLong("id");
                    edit.putExtra("id", id);
                    edit.putExtra("fromEdit", true);
                    edit.putExtra("content", bundle.getString("content"));
                    edit.putExtra("title",bundle.getString("title"));
                    //edit.putExtra("blank", bundle.getStringArrayList("blank"));
                    edit.putExtra("label",bundle.getStringArrayList("label"));
                    //labels.add("雅思");
                    startActivityForResult(edit, 1);
                    finish();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(showCorrectKnowledge.this);
                    dialog.setMessage("确认删除知识点<" + bundle.getString("title") + ">吗?");
                    dialog.setIcon(R.mipmap.ic_launcher);
                    dialog.setCancelable(false);            //点击对话框以外的区域是否让对话框消失

                    //设置正面按钮
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.db.deleteKnowledge(MainActivity.name, bundle.getString("content"));
                            MainActivity.updateData();
                            // 删除单词
                            Toast.makeText(showCorrectKnowledge.this, "已删除",
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
                            Toast.makeText(showCorrectKnowledge.this, "你点击了取消", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        } else {
            byte[] image = ChineseTest.bytePicture;
            if (image == null) {
                photo.setImageResource(R.drawable.ice);
            } else {
                photo.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            }
            showBlank.setVisibility(View.GONE);
            deleteButton.setVisibility(View.INVISIBLE);
            correctButton.setText("下一个");
            correctButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity.updateTest();
                    if (MainActivity.knowledgeTestIndex < MainActivity.knowledgeTestSum) {
                        Intent intent = new Intent(showCorrectKnowledge.this, ChineseTest.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(showCorrectKnowledge.this, "考查任务已完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
            home.setVisibility(View.INVISIBLE);
        }
        ListView listView = (ListView) findViewById(R.id.knowledge_label);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                showCorrectKnowledge.this, android.R.layout.simple_list_item_1,
                labels);
        listView.setAdapter(adapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
//                    Intent intent = new Intent(showCorrectKnowledge.this,showCorrectKnowledge.class);
//                    Bundle bundle = data.getExtras();
//                    bundle.putBoolean("ShowTask",getIntent().getExtras().getBoolean("ShowTask"));
//                    intent.putExtras(bundle);
//                    startActivity(intent);
//                    finish();
                }
                break;
            default:
        }
    }

}
