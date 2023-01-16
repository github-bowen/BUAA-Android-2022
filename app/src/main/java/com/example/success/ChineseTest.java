package com.example.success;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.success.entity.Knowledge;
import com.example.success.entity.KnowledgeBlank;
import com.example.success.entity.Label;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ChineseTest extends Activity implements View.OnClickListener {
    private Knowledge knowledge;
    private String content;
    private ArrayList<String> blanks = new ArrayList<>();
    private String title;
    static byte[] bytePicture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        knowledge = MainActivity.knowledgeTest.get(MainActivity.knowledgeTestIndex);
        content = knowledge.getKnowledgeContent();
        blanks = new ArrayList<>();
        title = knowledge.getTitle();

        String replaced = new String(knowledge.getKnowledgeContent());

        for (KnowledgeBlank blank : knowledge.getKnowledgeBlankList()) {
            blanks.add(blank.getBlank());
        }

        for (String blank : blanks) {
            //System.out.println(blank);
            replaced = replaced.replace(blank, "___");
        }
        setContentView(R.layout.chinese_test);
        //TODO:得到当前知识点的内容
        ImageView photo = (ImageView) findViewById(R.id.photo_knowledge);
        byte[] image = knowledge.getKnowledgePhoto();
        if (image == null) {
            photo.setImageResource(R.drawable.ice);
        } else {
            photo.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        ImageView home = (ImageView) findViewById(R.id.backToHome);
        home.setOnClickListener(this);
        TextView knowledge = (TextView) findViewById(R.id.knowledgeWithLine);
        knowledge.setText(replaced);
        TextView titleView = (TextView) findViewById(R.id.title_knowledge);
        titleView.setText(title);
        ScrollView scroller = (ScrollView) findViewById(R.id.scroll_test);
        scroller.setAlpha((float) 0.6);
        Button correctButton = (Button) findViewById(R.id.correct_knowledge);
        Button vagueButton = (Button) findViewById(R.id.vague_knowledge);
        Button forgetButton = (Button) findViewById(R.id.forget_knowledge);
        correctButton.setOnClickListener(this);
        vagueButton.setOnClickListener(this);
        forgetButton.setOnClickListener(this);
        correctButton.getBackground().setAlpha(100);
        vagueButton.getBackground().setAlpha(100);
        forgetButton.getBackground().setAlpha(100);
    }

    public void onClick(View view) {
        DatabaseInterface db = new DatabaseInterface(this);
        ArrayList<Label> labelList = (ArrayList<Label>) db.getKnowledgeLabel(knowledge);
        ArrayList<String> labels = new ArrayList<>();
        if(labelList != null && labelList.size() != 0) {
            if(labelList.size() != 1) {
                System.err.println("in ChineseTest 知识点和label应该1对1");
            }
            labels.add(labelList.get(0).getLabel());
        }
        switch (view.getId()) {
            case R.id.correct_knowledge:
                db.addCorrectTimeForKnowledge(knowledge);
                MainActivity.updateTest();
                System.out.println(MainActivity.knowledgeTestIndex + " ksum: " + MainActivity.knowledgeTestSum);
                if (MainActivity.knowledgeTestIndex < MainActivity.knowledgeTestSum) {
                    Intent intent = new Intent(ChineseTest.this, ChineseTest.class);
                    startActivity(intent);
                    finish(); //设置考察不能向前回退
                } else {
                    Toast.makeText(this, "考查任务已完成", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;

            case R.id.vague_knowledge:
                db.addVagueTimeForKnowledge(knowledge);
                //TODO:模糊次数++ 使用上面方法
                Intent intent2 = new Intent(ChineseTest.this, showCorrectKnowledge.class);
                //todo:放入当前的image，content，title，blank信息
                Bundle bundle = new Bundle();
                bytePicture = knowledge.getKnowledgePhoto();
                //bundle.putByteArray("image", image);
                bundle.putString("content", content);
                bundle.putString("title", title);
                bundle.putStringArrayList("blank", blanks);
                bundle.putBoolean("ShowTask", false);
                //labels.add("雅思");
                bundle.putStringArrayList("label", labels);
                intent2.putExtras(bundle);
                startActivity(intent2);
                finish(); //设置考察不能向前回退
                break;

            case R.id.forget_knowledge:
                db.addWrongTimeForKnowledge(knowledge);
                //TODO:错误次数++ 使用上面方法
                Intent intent3 = new Intent(ChineseTest.this, showCorrectKnowledge.class);
                bundle = new Bundle();
                //bundle.putByteArray("image", image);
                bytePicture = knowledge.getKnowledgePhoto();
                bundle.putString("content", content);
                bundle.putString("title", title);
                bundle.putStringArrayList("blank", blanks);
                bundle.putBoolean("ShowTask", false);
                //labels.add("6系");
                bundle.putStringArrayList("label", labels);
                intent3.putExtras(bundle);
                startActivity(intent3);
                finish(); //设置考察不能向前回退
                break;
            case R.id.backToHome:
                finish();
                break;
        }
    }
}
