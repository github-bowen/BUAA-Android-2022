package com.example.success;

import java.util.ArrayList;

public class KnowledgeDetail {
    private String content;
    private ArrayList<String> blank;
    private byte[] photoId;
    private String title;
    private ArrayList<String> labels;

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public KnowledgeDetail(String content, ArrayList<String> blank, byte[] photoId,
                           String title, ArrayList<String> labels) {
        this.content=content;
        this.blank = blank;
        this.photoId = photoId;
        this.title = title;
        this.labels = labels;
    }

    public KnowledgeDetail(String content, ArrayList<String> blank, byte[] photoId,
                           String title) {
        this.content=content;
        this.blank = blank;
        this.photoId = photoId;
        this.title = title;
        this.labels = new ArrayList<>();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getBlank() {
        return blank;
    }

    public void setBlank(ArrayList<String> blank) {
        this.blank = blank;
    }

    public byte[] getPhotoId() {
        return photoId;
    }

    public void setPhotoId(byte[] photoId) {
        this.photoId = photoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
