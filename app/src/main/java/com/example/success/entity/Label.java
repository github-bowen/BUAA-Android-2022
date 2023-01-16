package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Label {
    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Unique
    private String label;

    @Generated(hash = 879612901)
    public Label(Long id, @NotNull String label) {
        this.id = id;
        this.label = label;
    }

    @Generated(hash = 2137109701)
    public Label() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
