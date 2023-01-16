package com.example.success.entity;


import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import com.example.success.generatedDao.DaoSession;
import com.example.success.generatedDao.UserDao;
import com.example.success.generatedDao.KnowledgeDao;
import com.example.success.generatedDao.WordDao;

@Entity
public class User {

    /**
     * 实体的属性
     */
    @Id(autoincrement = true)
    private Long id;

    private int taskNum;  // 任务数量

    private int wordTaskNum = 20; //背诵单词数量，默认20

    private int knowledgeTaskNum = 20; //背诵知识点数量，默认20

    @Unique
    private String name;

    private String password;

    private int noteTime;  // 提醒背诵的时间

    private byte[] userPhoto;  // 头像

    /**
     * 一对多关系
     */
    // 一对多关系：一个用户有许多单词
    @ToMany(referencedJoinProperty = "userId") // Word 中的 userId
    private List<Word> wordList;

    // 一对多关系：一个用户有许多知识点
    @ToMany(referencedJoinProperty = "userId") // Knowledge 中的 userId
    private List<Knowledge> knowledgeList;

    /**
     * 多对多关系
     */
    @ToMany
    @JoinEntity(entity = FriendShip.class,
            sourceProperty = "user1Id",
            targetProperty = "user2Id")
    private List<User> friends;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    @Generated(hash = 1824102260)
    public User(Long id, int taskNum, int wordTaskNum, int knowledgeTaskNum, String name,
            String password, int noteTime, byte[] userPhoto) {
        this.id = id;
        this.taskNum = taskNum;
        this.wordTaskNum = wordTaskNum;
        this.knowledgeTaskNum = knowledgeTaskNum;
        this.name = name;
        this.password = password;
        this.noteTime = noteTime;
        this.userPhoto = userPhoto;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTaskNum() {
        return this.taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNoteTime() {
        return this.noteTime;
    }

    public void setNoteTime(int noteTime) {
        this.noteTime = noteTime;
    }

    public byte[] getUserPhoto() {
        return this.userPhoto;
    }

    public void setUserPhoto(byte[] userPhoto) {
        this.userPhoto = userPhoto;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1051378117)
    public List<Word> getWordList() {
        if (wordList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WordDao targetDao = daoSession.getWordDao();
            List<Word> wordListNew = targetDao._queryUser_WordList(id);
            synchronized (this) {
                if (wordList == null) {
                    wordList = wordListNew;
                }
            }
        }
        return wordList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1532061811)
    public synchronized void resetWordList() {
        wordList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1940512952)
    public List<Knowledge> getKnowledgeList() {
        if (knowledgeList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            KnowledgeDao targetDao = daoSession.getKnowledgeDao();
            List<Knowledge> knowledgeListNew = targetDao
                    ._queryUser_KnowledgeList(id);
            synchronized (this) {
                if (knowledgeList == null) {
                    knowledgeList = knowledgeListNew;
                }
            }
        }
        return knowledgeList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 459174405)
    public synchronized void resetKnowledgeList() {
        knowledgeList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1515850988)
    public List<User> getFriends() {
        if (friends == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            List<User> friendsNew = targetDao._queryUser_Friends(id);
            synchronized (this) {
                if (friends == null) {
                    friends = friendsNew;
                }
            }
        }
        return friends;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1638260638)
    public synchronized void resetFriends() {
        friends = null;
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
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

    public int getWordTaskNum() {
        return this.wordTaskNum;
    }

    public void setWordTaskNum(int wordTaskNum) {
        this.wordTaskNum = wordTaskNum;
    }

    public int getKnowledgeTaskNum() {
        return this.knowledgeTaskNum;
    }

    public void setKnowledgeTaskNum(int knowledgeTaskNum) {
        this.knowledgeTaskNum = knowledgeTaskNum;
    }

}
