package com.example.success;

import android.app.Activity;
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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.success.entity.Knowledge;
import com.example.success.entity.KnowledgeBlank;

import java.util.ArrayList;


public class ChineseMemory extends Activity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private GestureDetector mGestureDetector;
    private Knowledge knowledgeEntity;
    private String title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_correct_knowledge);
        mGestureDetector = new GestureDetector(this);
        if (MainActivity.knowledgeIndex >= MainActivity.knowledgeSum) {
            knowledgeEntity = MainActivity.knowledgeTask.get(MainActivity.knowledgeSum - 1);
        } else {
            knowledgeEntity = MainActivity.knowledgeTask.get(MainActivity.knowledgeIndex);
        }

        String content = knowledgeEntity.getKnowledgeContent();
        ArrayList<String> blankList = new ArrayList<>();

        String replaced = new String(knowledgeEntity.getKnowledgeContent());

        for (KnowledgeBlank blank : knowledgeEntity.getKnowledgeBlankList()) {
            blankList.add(blank.getBlank());
        }

        if (knowledgeEntity.getTitle() == null) {
            title = knowledgeEntity.getKnowledgeContent().substring(0, 2);
        } else {
            title = knowledgeEntity.getTitle();
        }

        for (String blank : blankList) {
            //System.out.println(blank);
            replaced = replaced.replace(blank, "___");
        }
        //System.out.println(replaced);
        /*String content = "遵守计组课程中所提到的寄存器使用法则。\n" +
                "s0-s7用于进行局部变量的分配，每次进入新的函数前都需要进行保护\n" +
                "而t0~t9用于进行临时变量的分配，在调用函数前后需要进行存取栈操作\n" +
                "由于$a0会进行一些系统调用操作，因此函数调用使用的寄存器是$a1-$a3，多的部分通过栈进行传递";*/
        //String[] blanks = {"s0-s7", "t0~t9", "$a0", "$a1-$a3", "栈"};
        String[] blanks = (String[]) blankList.toArray(new String[0]);
        SpannableStringBuilder spannableString = new SpannableStringBuilder(content);
        int lastPos = 0;
        for (int i = 0; i < blanks.length; i++) {
            System.out.println(content.contains(blanks[i]));
            System.out.println(content.indexOf(blanks[i]));
            int index = content.indexOf(blanks[i]);
            BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#fff799"));
            spannableString.setSpan(backgroundColorSpan, index, index + blanks[i].length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            UnderlineSpan underlineSpan = new UnderlineSpan();
            spannableString.setSpan(underlineSpan, index, index + blanks[i].length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            StyleSpan styleSpan_B = new StyleSpan(Typeface.BOLD);//粗体
            StyleSpan styleSpan_I = new StyleSpan(Typeface.ITALIC);//斜体
            spannableString.setSpan(styleSpan_B, index, index + blanks[i].length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(styleSpan_I, index, index + blanks[i].length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            lastPos = index + blanks[i].length();
        }
        TextView knowledge = (TextView) findViewById(R.id.knowledge_correct);
        knowledge.setText(spannableString);
        ImageView photo = (ImageView) findViewById(R.id.photo_knowledge_c);
        byte[] image = knowledgeEntity.getKnowledgePhoto();
        if (image == null) {
            photo.setImageResource(R.drawable.ice);
        } else {
            photo.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        TextView titleView = (TextView) findViewById(R.id.title_knowledge_correct);
        titleView.setText(title);
        ImageView home = (ImageView) findViewById(R.id.backToHome_correct);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.updateMainView();
                finish();
            }
        });
        Button correctButton = (Button) findViewById(R.id.next_correct);
        correctButton.setVisibility(View.GONE);
        SwitchCompat showBlank = (SwitchCompat) findViewById(R.id.showBlank);
        String finalReplaced = replaced;
        showBlank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //TODO：得到挖完空后的str，一个空以___代替
//                    knowledge.setText("遵守计组课程中所提到的寄存器使用法则。\n" +
//                            "____用于进行局部变量的分配，每次进入新的函数前都需要进行保护\n" +
//                            "而____用于进行临时变量的分配，在调用函数前后需要进行存取栈操作\n" +
//                            "由于____会进行一些系统调用操作，因此函数调用使用的寄存器是____，多的部分通过____进行传递");
                    knowledge.setText(finalReplaced);
                    showBlank.setText("显示知识点");
                } else {
                    knowledge.setText(spannableString);
                    showBlank.setText("隐藏知识点");
                }
            }
        });

    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final int FLING_MIN_DISTANCE = 100;
        final int FLING_MIN_VELOCITY = 200;

//        //左
//        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
//            Intent intent = new Intent(ResultActivity.this,MessageActivity.class);
//            startActivity(intent);
//        }
        //右
        if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
            MainActivity.knowledgeIndex++;//TODO：全部背完之后的欢迎页面
            if (MainActivity.knowledgeIndex < MainActivity.knowledgeSum) {
                Intent intent = new Intent(ChineseMemory.this, ChineseMemory.class);
                startActivity(intent);
                finish(); //设置考察不能向前回退
            } else {
                MainActivity.wordIndex = 0;
                MainActivity.knowledgeIndex = 0;
                Toast.makeText(this, "背诵完成，回到主页面", Toast.LENGTH_LONG).show();
                MainActivity.updateData();
                finish();
            }

//                }
        }

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

}
