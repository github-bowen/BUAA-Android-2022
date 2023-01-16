package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WordTask {
    @Id(autoincrement = true)
    private Long id;

    private Long wordId;
    private Long userId;

    private String date;

    private int status;
    //0:未背诵
    //1:已背诵

    @Generated(hash = 1024677438)
    public WordTask(Long id, Long wordId, Long userId, String date, int status) {
        this.id = id;
        this.wordId = wordId;
        this.userId = userId;
        this.date = date;
        this.status = status;
    }

    @Generated(hash = 834212994)
    public WordTask() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWordId() {
        return this.wordId;
    }

    public void setWordId(Long wordId) {
        this.wordId = wordId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
