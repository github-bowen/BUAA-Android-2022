package com.example.success;

import java.util.ArrayList;

public class Word {
    private String english;
    private String chinese;
    private byte[] imageBitMap; // 对应的图片资源byte[]
    private ArrayList<String> labels;

    public Word(String english,String chinese,byte[] imageId,ArrayList<String> labels) {
        this.chinese = chinese;
        this.english = english;
        this.imageBitMap = imageId;
        this.labels = labels;
    }

    public Word(String english,String chinese,byte[] imageId) {
        this.chinese = chinese;
        this.english = english;
        this.imageBitMap = imageId;
        this.labels = new ArrayList<>();
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public byte[] getImageBitMap() {
        return imageBitMap;
    }

    public void setImageBitMap(byte[] imageBitMap) {
        this.imageBitMap = imageBitMap;
    }
}
