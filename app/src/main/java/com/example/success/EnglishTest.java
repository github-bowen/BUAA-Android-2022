package com.example.success;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.example.success.entity.Label;
import com.example.success.entity.Word;

import java.util.ArrayList;
import java.util.Arrays;

public class EnglishTest extends Activity
        implements View.OnClickListener {
    private Word word;
    static byte[] bytePicture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseInterface db = new DatabaseInterface(this);
        setContentView(R.layout.english_test);
        Button correctButton = (Button) findViewById(R.id.correct);
        Button vagueButton = (Button) findViewById(R.id.vague);
        Button forgetButton = (Button) findViewById(R.id.forget);
        SwitchCompat switchLanguage = (SwitchCompat) findViewById(R.id.showAns);
        ImageView home = (ImageView) findViewById(R.id.backToHome_english);
        home.setOnClickListener(this);
        correctButton.setOnClickListener(this);
        vagueButton.setOnClickListener(this);
        forgetButton.setOnClickListener(this);

        word = MainActivity.wordTest.get(MainActivity.wordTestIndex);
        TextView english = (TextView) findViewById(R.id.English);
        english.setText(word.getWordEnglish());
        english.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        ImageView photo = (ImageView) findViewById(R.id.wordPicture);
        byte[] image = word.getWordPhoto();
        if(image==null) {
            photo.setImageResource(R.drawable.beautiful);
        } else {
            photo.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
        }
        //Word currentWord = MainActivity.wordTask.get(MainActivity.wordIndex); //TODO:指向当前页面单词，相关信息可以从currentWord中获取，需要判断是否越界
        switchLanguage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) { //说明当前还是英文

//                english.setText("adj.美丽的，漂亮的；令人愉悦的，美妙的；\n" +
//                        "        （完成得）出色的，极好的；仁慈的");
                english.setText(word.getWordChinese());
                english.setTextSize(20);
                switchLanguage.setText("En");
                } else {
                    TextView english = (TextView) findViewById(R.id.English);
//                    english.setText("Beautiful");
                    english.setText(word.getWordEnglish());
                    english.setTextSize(65);
                    switchLanguage.setText("Ch");
                }
            }
        });
    }

    public void onClick(View view) {
        DatabaseInterface db = new DatabaseInterface(this);
        int index = 1; //TODO:可以改为 MainActivity.wordIndex
        int englishNum = 2; //TODO: 可以改为 MainActivity.wordSum
        //Word word = MainActivity.wordTask.get(MainActivity.wordIndex); //TODO:获取当前单词
        ArrayList<Label> labelList = (ArrayList<Label>) db.getWordLabel(word);
        ArrayList<String> labels = new ArrayList<>();
        if(labelList != null && labelList.size() != 0) {
            if(labelList.size() != 1) {
                System.err.println("in EnglishTest 单词和label应该1对1");
            }
            labels.add(labelList.get(0).getLabel());
        }
        //MainActivity.wordTestIndex++; //TODO:使全局指针+1 （需要测试），不确定在此处+1是否影响页面展示内容
        // index代表当前记忆的指针，englishNum代表一共有多少条英文记录
        // index>englishNum就应该唤醒ChineseTest
        switch (view.getId()) {
            case R.id.correct:
                db.addCorrectTimeForWord(word);
                MainActivity.updateTest();
//                System.out.println(MainActivity.wordTestIndex + "sum:" + MainActivity.wordTestSum);
//                System.out.println(MainActivity.knowledgeTestIndex + "ksum:" + MainActivity.knowledgeTestSum);
                //TODO:正确次数++ 使用上面方法
                if (MainActivity.wordTestIndex < MainActivity.wordTestSum) {
                    Intent intent = new Intent(EnglishTest.this, EnglishTest.class);
                    startActivity(intent);
                    finish(); //设置考察不能向前回退
                } else {
                    if(MainActivity.knowledgeTestIndex < MainActivity.knowledgeTestSum) {
                        Intent intent = new Intent(EnglishTest.this, ChineseTest.class);
                        startActivity(intent);
                        finish(); //设置考察不能向前回退
                    } else {
                        Toast.makeText(this, "考查任务已完成", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
                break;

            case R.id.vague:
                db.addVagueTimeForWord(word);
                //TODO:模糊次数++ 使用上面方法
                Intent intent = new Intent(EnglishTest.this, showCorrectAnswer.class);
                //TODO:获取当前页面的单词信息：english，Chinese，image
                String english = word.getWordEnglish();
                String chinese = word.getWordChinese();
                bytePicture = word.getWordPhoto();
                //byte[] image = word.getWordPhoto();
                Bundle bundle = new Bundle();
                //bundle.putByteArray("image",image);
                bundle.putString("English",english);
                bundle.putString("Chinese",chinese);
                bundle.putBoolean("ShowTask",false);

                //labels.add("雅思");
                bundle.putStringArrayList("label",labels);
                intent.putExtras(bundle);
                startActivity(intent);
                finish(); //设置考察不能向前回退
                break;

            case R.id.forget:
                db.addWrongTimeForWord(word);
                //TODO:错误次数++ 使用上面方法
                Intent intent2 = new Intent(EnglishTest.this, showCorrectAnswer.class);
                english = word.getWordEnglish();
                chinese = word.getWordChinese();
                bytePicture = word.getWordPhoto();
                bundle = new Bundle();
                //bundle.putByteArray("image",image);
                bundle.putString("English",english);
                bundle.putString("Chinese",chinese);
                bundle.putBoolean("ShowTask",false);
                //labels.add("雅思");
                bundle.putStringArrayList("label",labels);
                intent2.putExtras(bundle);
                startActivity(intent2);
                finish(); //设置考察不能向前回退
                break;
            case R.id.backToHome_english:
                finish();
        }
    }
// implements View.OnTouchListener, GestureDetector.OnGestureListener,
//    @Override
//    public boolean onDown(MotionEvent motionEvent) {
//        return false;
//    }
//
//    @Override
//    public void onShowPress(MotionEvent motionEvent) {
//
//    }
//
//    @Override
//    public boolean onSingleTapUp(MotionEvent motionEvent) {
//        return false;
//    }
//
//    @Override
//    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
//        return false;
//    }
//
//    @Override
//    public void onLongPress(MotionEvent motionEvent) {
//
//    }
//
//    @Override
//    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//        final int FLING_MIN_DISTANCE=100;
//        final int FLING_MIN_VELOCITY=200;
//
////        //左
////        if(e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
////            Intent intent = new Intent(ResultActivity.this,MessageActivity.class);
////            startActivity(intent);
////        }
//        //右
//        if(e1.getX() - e2.getX() < FLING_MIN_DISTANCE && Math.abs(velocityX) < FLING_MIN_VELOCITY){
//            int index = 1;
//            int englishNum = 2; // index代表当前记忆的指针，englishNum代表一共有多少条英文记录
//            // index>englishNum就应该唤醒ChineseTest
//            if(index<englishNum) {
//                Intent intent = new Intent(EnglishTest.this,Sign_In.class);
//                startActivity(intent);
//                finish(); //设置考察不能向前回退
//            } else {
//                Intent intent = new Intent(EnglishTest.this,ChineseTest.class);
//                startActivity(intent);
//                finish(); //设置考察不能向前回退
//            }
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        return false;
//    }
}
