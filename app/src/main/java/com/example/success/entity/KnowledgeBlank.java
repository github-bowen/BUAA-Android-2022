package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class KnowledgeBlank {

    /**
     * 实体的属性
     */
    @Id(autoincrement = true)
    private Long id;
    private Long knowledgeId;

    private String blank;

    @Generated(hash = 1563236863)
    public KnowledgeBlank(Long id, Long knowledgeId, String blank) {
        this.id = id;
        this.knowledgeId = knowledgeId;
        this.blank = blank;
    }

    @Generated(hash = 1637462949)
    public KnowledgeBlank() {
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

    public String getBlank() {
        return this.blank;
    }

    public void setBlank(String blank) {
        this.blank = blank;
    }


}
