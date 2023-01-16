package com.example.success;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.success.entity.Word;

public class EnglishMemory extends Activity implements View.OnTouchListener, GestureDetector.OnGestureListener{
    private GestureDetector mGestureDetector;
    private Word word;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.english_memory);
        SwitchCompat languageSwitch = (SwitchCompat) findViewById(R.id.visibleCh);
        TextView chinese = (TextView) findViewById(R.id.chinese_memory);
        TextView english = (TextView) findViewById(R.id.English_memory);
        if(MainActivity.wordIndex >= MainActivity.wordSum) {
            word = MainActivity.wordTask.get(MainActivity.wordSum - 1);
        } else {
            word = MainActivity.wordTask.get(MainActivity.wordIndex);
        }
        chinese.setText(word.getWordChinese());
        english.setText(word.getWordEnglish());
        ImageView photo = (ImageView) findViewById(R.id.wordPicture_memory);
        byte[] image = word.getWordPhoto();
        if(image==null) {
            photo.setImageResource(R.drawable.beautiful);
        } else {
            photo.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
        }

        mGestureDetector = new GestureDetector(this);

        languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) { //当前仍为单词状态
                    chinese.setVisibility(View.INVISIBLE);
                    languageSwitch.setText("显示中文");
                } else {
                    chinese.setVisibility(View.VISIBLE);
                    languageSwitch.setText("隐藏中文");
                }
            }
        });
        ImageButton imageButton = (ImageButton) findViewById(R.id.backToMain);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(EnglishMemory.this,MainActivity.class);
//                startActivity(intent);
                MainActivity.updateMainView();
                finish();
            }
        });
    }


    public boolean dispatchTouchEvent(MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event)){
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

        @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

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
        final int FLING_MIN_DISTANCE=100;
        final int FLING_MIN_VELOCITY=200;
//        //从左向右
//        if(e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
//            Intent intent = new Intent(ResultActivity.this,MessageActivity.class);
//            startActivity(intent);
//        }
        //从右向左
        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
            MainActivity.wordIndex++;
            System.out.println(MainActivity.wordIndex + "sum" + MainActivity.wordSum);
            int index = 3;
            int englishNum = 2; // index代表当前记忆的指针，englishNum代表一共有多少条英文记录
            // index>englishNum就应该唤醒ChineseTest
            if(MainActivity.wordIndex < MainActivity.wordSum) {
                Intent intent = new Intent(EnglishMemory.this,EnglishMemory.class);
                startActivity(intent);
                finish();
            } else {
                if(MainActivity.knowledgeSum > 0) {
                    Intent intent = new Intent(EnglishMemory.this, ChineseMemory.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    MainActivity.wordIndex = 0;
                    MainActivity.knowledgeIndex = 0;
                    Toast.makeText(this, "背诵完成，回到主页面", Toast.LENGTH_LONG).show();
                    MainActivity.updateData();
                    finish();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
