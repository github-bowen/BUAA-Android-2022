package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class KnowledgeLabel {

    /**
     * 实体的属性
     */
    @Id(autoincrement = true)
    private Long id;

    private Long knowledgeId;
    private Long labelId;
    @Generated(hash = 758956300)
    public KnowledgeLabel(Long id, Long knowledgeId, Long labelId) {
        this.id = id;
        this.knowledgeId = knowledgeId;
        this.labelId = labelId;
    }
    @Generated(hash = 917671971)
    public KnowledgeLabel() {
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
    public Long getLabelId() {
        return this.labelId;
    }
    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

}
