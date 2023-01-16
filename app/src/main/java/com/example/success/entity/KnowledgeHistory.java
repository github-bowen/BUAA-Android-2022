package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class KnowledgeHistory {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    private Long knowledgeId;
    private String date;
    private String status;
    @Generated(hash = 1065282685)
    public KnowledgeHistory(Long id, @NotNull Long knowledgeId, String date,
            String status) {
        this.id = id;
        this.knowledgeId = knowledgeId;
        this.date = date;
        this.status = status;
    }
    @Generated(hash = 1227676592)
    public KnowledgeHistory() {
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
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
