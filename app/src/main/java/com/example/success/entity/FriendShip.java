package com.example.success.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 两个用户之间的多对多关系，需要单独一个表来记录
 */
@Entity
public class FriendShip {

    @Id(autoincrement = true)
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private int status;  // 0: 申请中,  1: 已接受

    @Generated(hash = 2049880380)
    public FriendShip(Long id, Long user1Id, Long user2Id, int status) {
        this.id = id;
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.status = status;
    }

    @Generated(hash = 1658247339)
    public FriendShip() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser1Id() {
        return this.user1Id;
    }

    public void setUser1Id(Long user1Id) {
        this.user1Id = user1Id;
    }

    public Long getUser2Id() {
        return this.user2Id;
    }

    public void setUser2Id(Long user2Id) {
        this.user2Id = user2Id;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
