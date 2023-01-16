package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class KnowledgeTask {
    @Id(autoincrement = true)
    private Long id;

    private Long knowledgeId;
    private Long userId;

    private String date;

    private int status;
    //0:未背诵
    //1:已背诵

    @Generated(hash = 1742356343)
    public KnowledgeTask(Long id, Long knowledgeId, Long userId, String date,
            int status) {
        this.id = id;
        this.knowledgeId = knowledgeId;
        this.userId = userId;
        this.date = date;
        this.status = status;
    }

    @Generated(hash = 248503870)
    public KnowledgeTask() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKnowledgeId() {
        return this.knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
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
