package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class WordHistory {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private Long wordId;
    private String date;
    private String status;
    @Generated(hash = 666962630)
    public WordHistory(Long id, @NotNull Long wordId, String date, String status) {
        this.id = id;
        this.wordId = wordId;
        this.date = date;
        this.status = status;
    }
    @Generated(hash = 531696398)
    public WordHistory() {
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
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
