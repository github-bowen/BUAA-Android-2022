package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class WordLabel {

    /**
     * 实体的属性
     */
    @Id(autoincrement = true)
    private Long id;

    private Long wordId;
    private Long labelId;
    @Generated(hash = 1642081578)
    public WordLabel(Long id, Long wordId, Long labelId) {
        this.id = id;
        this.wordId = wordId;
        this.labelId = labelId;
    }
    @Generated(hash = 1934010050)
    public WordLabel() {
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
    public Long getLabelId() {
        return this.labelId;
    }
    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

}
