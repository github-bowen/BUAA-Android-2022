package com.example.success;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<String> textName;
    private final MutableLiveData<String> textPasswd;
    private final MutableLiveData<Integer> wordSum; //单词总数
    private final MutableLiveData<Integer> knowledgeSum; //任务总数
    private final MutableLiveData<Integer> wordIndex; //当前单词index
    private final MutableLiveData<Integer> knowledgeIndex; //当前任务index

    private final MutableLiveData<Integer> wordTestSum;
    private final MutableLiveData<Integer> knowledgeTestSum;
    private final MutableLiveData<Integer> wordTestIndex;
    private final MutableLiveData<Integer> knowledgeTestIndex;

    public MainViewModel() {
        textName = new MutableLiveData<>();
        textPasswd = new MutableLiveData<>();
        wordSum = new MutableLiveData<>();
        knowledgeSum = new MutableLiveData<>();
        wordIndex = new MutableLiveData<>();
        knowledgeIndex = new MutableLiveData<>();
        wordTestSum = new MutableLiveData<>();
        wordTestIndex = new MutableLiveData<>();
        knowledgeTestIndex = new MutableLiveData<>();
        knowledgeTestSum = new MutableLiveData<>();
    }

    public LiveData<String> getName() {
        return textName;
    }
    public LiveData<String> getPasswd() {
        return textPasswd;
    }

    public MutableLiveData<Integer> getKnowledgeIndex() {
        return knowledgeIndex;
    }

    public MutableLiveData<Integer> getKnowledgeSum() {
        return knowledgeSum;
    }

    public MutableLiveData<Integer> getKnowledgeTestIndex() {
        return knowledgeTestIndex;
    }

    public MutableLiveData<Integer> getKnowledgeTestSum() {
        return knowledgeTestSum;
    }

    public MutableLiveData<Integer> getWordIndex() {
        return wordIndex;
    }

    public MutableLiveData<Integer> getWordTestSum() {
        return wordTestSum;
    }

    public MutableLiveData<Integer> getWordSum() {
        return wordSum;
    }

    public MutableLiveData<Integer> getWordTestIndex() {
        return wordTestIndex;
    }

    public void setTextName(String name) {
        textName.setValue(name);
    }
    public void setTextPasswd(String passwd) {
        textPasswd.setValue(passwd);
    }

    public void setKnowledgeIndex(Integer value) {
         knowledgeIndex.setValue(value);
    }

    public void setKnowledgeSum(Integer value) {
         knowledgeSum.setValue(value);
    }

    public void setKnowledgetestIndex(Integer value) {
         knowledgeTestIndex.setValue(value);
    }

    public void setKnowledgeTestSum(Integer value) {
         knowledgeTestSum.setValue(value);
    }

    public void setWordIndex(Integer value) {
         wordIndex.setValue(value);
    }

    public void setWordTestSum(Integer value) {
         wordTestSum.setValue(value);
    }

    public void setWordSum(Integer value) {
         wordSum.setValue(value);
    }

    public void setWordTestIndex(Integer value) {
         wordTestIndex.setValue(value);
    }
    
    
}
