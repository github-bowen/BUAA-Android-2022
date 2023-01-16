package com.example.success;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.success.entity.Knowledge;
import com.example.success.entity.KnowledgeBlank;
import com.example.success.entity.Label;

import java.util.ArrayList;
import java.util.List;

public class ShowTask extends Activity {
    private List<Word> words = new ArrayList<>();
    private List<KnowledgeDetail> knowledges = new ArrayList<>();
    static byte[] bytePicture;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_task_show);
        initTask();
        WordAdapter wordAdapter = new WordAdapter(ShowTask.this,
                R.layout.word_show, words);
        KnowledgeAdapter knowledgeAdapter = new KnowledgeAdapter(ShowTask.this,
                R.layout.knowledge_show, knowledges);
        ListView listView = (ListView) findViewById(R.id.today_task);
        listView.setAdapter(wordAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Word word = words.get(position);
                Intent callDetailPage = new Intent(ShowTask.this, showCorrectAnswer.class);
                Bundle bundle = new Bundle();
                bytePicture = word.getImageBitMap();
                //bundle.putByteArray("image", word.getImageBitMap());
                bundle.putString("English", word.getEnglish());
                bundle.putString("Chinese", word.getChinese());
                bundle.putBoolean("ShowTask", true);
                bundle.putStringArrayList("label", word.getLabels());
                callDetailPage.putExtras(bundle);
                startActivity(callDetailPage);
            }
        });
        SwitchCompat switchWord = (SwitchCompat) findViewById(R.id.wordKnowledgeSwitch);
        switchWord.setText("切换为知识点");
        SwitchCompat languageSwitch = (SwitchCompat) findViewById(R.id.taskLanguageSwitch);
        languageSwitch.setText("点击隐藏中文");
        ImageView picture = (ImageView) findViewById(R.id.wordOrKnowledge);
        picture.setImageResource(R.drawable.word2);
        switchWord.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) { //当前仍为单词状态
                    if(words.size()==0) {
                        ImageView nothing = (ImageView) findViewById(R.id.nothing);
                        nothing.setVisibility(View.VISIBLE);
                    }
                    languageSwitch.setVisibility(View.INVISIBLE);
                    listView.setAdapter(knowledgeAdapter);
                    picture.setImageResource(R.drawable.knowledge);

                    switchWord.setText("切换为单词");
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            KnowledgeDetail knowledgeDetail = knowledges.get(position);
                            Intent callDetailPage = new Intent(ShowTask.this, showCorrectKnowledge.class);
                            Bundle bundle = new Bundle();
                            bytePicture = knowledgeDetail.getPhotoId();
                            //bundle.putByteArray("image", knowledgeDetail.getPhotoId());
                            bundle.putString("content", knowledgeDetail.getContent());
                            bundle.putString("title", knowledgeDetail.getTitle());
                            bundle.putStringArrayList("blank", knowledgeDetail.getBlank());
                            bundle.putBoolean("ShowTask", true);
                            bundle.putStringArrayList("label", knowledgeDetail.getLabels());
                            callDetailPage.putExtras(bundle);
                            startActivity(callDetailPage);
                        }
                    });
                } else {
                    languageSwitch.setVisibility(View.VISIBLE);
                    listView.setAdapter(wordAdapter);
                    picture.setImageResource(R.drawable.word2);
                    switchWord.setText("切换为知识点");
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            Word word = words.get(position);
                            Intent callDetailPage = new Intent(ShowTask.this, showCorrectAnswer.class);
                            Bundle bundle = new Bundle();
                            bytePicture = word.getImageBitMap();
                            //bundle.putByteArray("image", word.getImageBitMap());
                            bundle.putString("English", word.getEnglish());
                            bundle.putString("Chinese", word.getChinese());
                            bundle.putBoolean("ShowTask", true);
                            bundle.putStringArrayList("label", word.getLabels());
                            callDetailPage.putExtras(bundle);
                            startActivity(callDetailPage);
                        }
                    });
                }
            }
        });
        languageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) { //当前有中文
                    int childCount = listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View chinese = listView.getChildAt(i).findViewById(R.id.wordChinese);
                        chinese.setVisibility(View.INVISIBLE);
                    }
                    languageSwitch.setText("点击显示中文");
                } else {
                    int childCount = listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View chinese = listView.getChildAt(i).findViewById(R.id.wordChinese);
                        chinese.setVisibility(View.VISIBLE);
                    }
                    languageSwitch.setText("点击隐藏中文");
                }
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Word word = words.get(position);
                        Intent callDetailPage = new Intent(ShowTask.this, showCorrectAnswer.class);
                        Bundle bundle = new Bundle();
                        bytePicture = word.getImageBitMap();
                        //bundle.putByteArray("image", word.getImageBitMap());
                        bundle.putString("English", word.getEnglish());
                        bundle.putString("Chinese", word.getChinese());
                        bundle.putStringArrayList("label",word.getLabels());
                        bundle.putBoolean("ShowTask", true);
                        bundle.putStringArrayList("label", word.getLabels());
                        callDetailPage.putExtras(bundle);
                        startActivity(callDetailPage);
                    }
                });
            }
        });
    }

    public void initTask() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boolean fromSearch = bundle.getBoolean("fromSearch");
        List<com.example.success.entity.Word> wordList;
        List<Knowledge> knowledgeList;
        if(fromSearch) {
            String keyOrLabel = bundle.getString("keyOrLabel");
            wordList = MainActivity.db.searchWord(MainActivity.name, keyOrLabel);
            knowledgeList = MainActivity.db.searchKnowledge(MainActivity.name, keyOrLabel);
            //TODO:此处数据库返回根据keyOrLabel得到的Knowledge和word
        } else {
            wordList = MainActivity.wordTask;
            knowledgeList = MainActivity.knowledgeTask;

            //TODO:说明是当日任务，取当日背诵任务赋到两个数组里
        }
        for (com.example.success.entity.Word wordEntity : wordList) {
            Word word = new Word(wordEntity.getWordEnglish(), wordEntity.getWordChinese(), wordEntity.getWordPhoto());
            ArrayList<Label> labelList = (ArrayList<Label>) MainActivity.db.getWordLabel(wordEntity);
            ArrayList<String> labels = new ArrayList<>();
            if(labelList != null && labelList.size() != 0) {
                if(labelList.size() != 1) {
                    System.err.println("in ShowTask 单词和label应该1对1");
                }
                labels.add(labelList.get(0).getLabel());
            }
            word.setLabels(labels);
            words.add(word);
        }
        for (Knowledge knowledge : knowledgeList) {
            ArrayList<String> blankList = new ArrayList<>();
            String title;
            for (KnowledgeBlank blank : knowledge.getKnowledgeBlankList()) {
                blankList.add(blank.getBlank());
            }
            ArrayList<Label> labelList = (ArrayList<Label>) MainActivity.db.getKnowledgeLabel(knowledge);
            ArrayList<String> labels = new ArrayList<>();
            if(labelList != null && labelList.size() != 0) {
                if(labelList.size() != 1) {
                    System.err.println("in ShowTask 知识点和label应该1对1");
                }
                labels.add(labelList.get(0).getLabel());
            }
            title = knowledge.getTitle();

            KnowledgeDetail detail = new KnowledgeDetail(knowledge.getKnowledgeContent(),
                    blankList, knowledge.getKnowledgePhoto(), title);
            detail.setLabels(labels);
            //System.out.println(knowledge.getKnowledgePhoto());
            knowledges.add(detail);
        }


        //knowledges.add(knowledge);
    }
}
