package com.example.success.entity;


import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;
import com.example.success.generatedDao.DaoSession;
import com.example.success.generatedDao.KnowledgeBlankDao;
import com.example.success.generatedDao.KnowledgeDao;
import com.example.success.generatedDao.KnowledgeLabelDao;

@Entity
public class Knowledge {

    /**
     * 实体的属性
     */
    @Id(autoincrement = true)
    private Long id;
    private Long userId;

    private String knowledgeContent;  // knowledge content

    private byte[] knowledgePhoto;

    private int times;  // 背诵次数

    private String title; //知识点标题，由用户设定

    private Date knowledgeDate;  // 上次背诵的日期，没背诵过为 null

    private int knowledgeCorrect;  // 正确次数

    private int knowledgeVague;  // 模糊次数

    /**
     * 实体的一对多关系
     */
    // 一对多关系：一个知识点有多个需要挖空的字符串
    @ToMany(referencedJoinProperty = "knowledgeId") // KnowledgeBlank 中的 knowledgeId
    private List<KnowledgeBlank> knowledgeBlankList;

    // 一对多关系：一个知识点有多个标签
    @ToMany(referencedJoinProperty = "knowledgeId") // KnowledgeLabel 中的 knowledgeId
    private List<KnowledgeLabel> knowledgeLabelList;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1142006765)
    private transient KnowledgeDao myDao;

    @Generated(hash = 1436850164)
    public Knowledge(Long id, Long userId, String knowledgeContent,
                     byte[] knowledgePhoto, int times, String title, Date knowledgeDate,
                     int knowledgeCorrect, int knowledgeVague) {
        this.id = id;
        this.userId = userId;
        this.knowledgeContent = knowledgeContent;
        this.knowledgePhoto = knowledgePhoto;
        this.times = times;
        this.title = title;
        this.knowledgeDate = knowledgeDate;
        this.knowledgeCorrect = knowledgeCorrect;
        this.knowledgeVague = knowledgeVague;
    }

    @Generated(hash = 2109785241)
    public Knowledge() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKnowledgeContent() {
        return this.knowledgeContent;
    }

    public void setKnowledgeContent(String knowledgeContent) {
        this.knowledgeContent = knowledgeContent;
    }

    public byte[] getKnowledgePhoto() {
        return this.knowledgePhoto;
    }

    public void setKnowledgePhoto(byte[] knowledgePhoto) {
        this.knowledgePhoto = knowledgePhoto;
    }

    public int getTimes() {
        return this.times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public Date getKnowledgeDate() {
        return this.knowledgeDate;
    }

    public void setKnowledgeDate(Date knowledgeDate) {
        this.knowledgeDate = knowledgeDate;
    }

    public int getKnowledgeCorrect() {
        return this.knowledgeCorrect;
    }

    public void setKnowledgeCorrect(int knowledgeCorrect) {
        this.knowledgeCorrect = knowledgeCorrect;
    }

    public int getKnowledgeVague() {
        return this.knowledgeVague;
    }

    public void setKnowledgeVague(int knowledgeVague) {
        this.knowledgeVague = knowledgeVague;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1114163982)
    public List<KnowledgeBlank> getKnowledgeBlankList() {
        if (knowledgeBlankList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KnowledgeBlankDao targetDao = daoSession.getKnowledgeBlankDao();
            List<KnowledgeBlank> knowledgeBlankListNew = targetDao
                    ._queryKnowledge_KnowledgeBlankList(id);
            synchronized (this) {
                if (knowledgeBlankList == null) {
                    knowledgeBlankList = knowledgeBlankListNew;
                }
            }
        }
        return knowledgeBlankList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1533220951)
    public synchronized void resetKnowledgeBlankList() {
        knowledgeBlankList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 510336872)
    public List<KnowledgeLabel> getKnowledgeLabelList() {
        if (knowledgeLabelList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KnowledgeLabelDao targetDao = daoSession.getKnowledgeLabelDao();
            List<KnowledgeLabel> knowledgeLabelListNew = targetDao
                    ._queryKnowledge_KnowledgeLabelList(id);
            synchronized (this) {
                if (knowledgeLabelList == null) {
                    knowledgeLabelList = knowledgeLabelListNew;
                }
            }
        }
        return knowledgeLabelList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1579803425)
    public synchronized void resetKnowledgeLabelList() {
        knowledgeLabelList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1464204294)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getKnowledgeDao() : null;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAccuracy(){
        if(times == 0) {
            return 1;
        }
        return (double) knowledgeCorrect / (double) times;
    }

}
