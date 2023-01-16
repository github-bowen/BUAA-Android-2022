package com.example.success.entity;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;

import com.example.success.generatedDao.DaoSession;
import com.example.success.generatedDao.WordDao;
import com.example.success.generatedDao.WordLabelDao;

@Entity
public class Word {

    /**
     * 实体的属性
     */
    @Id(autoincrement = true)
    private Long id;
    private Long userId;

    private String wordChinese;  // 中文单词
    private String wordEnglish;  // 英文单词

    private Date wordDate;  // 上次背诵的日期，没背诵过为 null

    private int wordTimes;  // 背诵次数
    private int wordCorrect;  // 正确的次数
    private int wordVague;  // 模糊的次数

    private byte[] wordPhoto;  // 辅助图片

    /**
     * 实体的一对多关系
     */
    // 一对多关系：一个单词有许多标签
    @ToMany(referencedJoinProperty = "wordId") // WordLabel 中的 wordId
    private List<WordLabel> wordLabelList;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 768131649)
    private transient WordDao myDao;

    @Generated(hash = 3512393)
    public Word(Long id, Long userId, String wordChinese, String wordEnglish,
                Date wordDate, int wordTimes, int wordCorrect, int wordVague,
                byte[] wordPhoto) {
        this.id = id;
        this.userId = userId;
        this.wordChinese = wordChinese;
        this.wordEnglish = wordEnglish;
        this.wordDate = wordDate;
        this.wordTimes = wordTimes;
        this.wordCorrect = wordCorrect;
        this.wordVague = wordVague;
        this.wordPhoto = wordPhoto;
    }

    @Generated(hash = 3342184)
    public Word() {
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

    public String getWordChinese() {
        return this.wordChinese;
    }

    public void setWordChinese(String wordChinese) {
        this.wordChinese = wordChinese;
    }

    public String getWordEnglish() {
        return this.wordEnglish;
    }

    public void setWordEnglish(String wordEnglish) {
        this.wordEnglish = wordEnglish;
    }

    public Date getWordDate() {
        return this.wordDate;
    }

    public void setWordDate(Date wordDate) {
        this.wordDate = wordDate;
    }

    public int getWordTimes() {
        return this.wordTimes;
    }

    public void setWordTimes(int wordTimes) {
        this.wordTimes = wordTimes;
    }

    public int getWordCorrect() {
        return this.wordCorrect;
    }

    public void setWordCorrect(int wordCorrect) {
        this.wordCorrect = wordCorrect;
    }

    public int getWordVague() {
        return this.wordVague;
    }

    public void setWordVague(int wordVague) {
        this.wordVague = wordVague;
    }

    public byte[] getWordPhoto() {
        return this.wordPhoto;
    }

    public void setWordPhoto(byte[] wordPhoto) {
        this.wordPhoto = wordPhoto;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1025402869)
    public List<WordLabel> getWordLabelList() {
        if (wordLabelList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            WordLabelDao targetDao = daoSession.getWordLabelDao();
            List<WordLabel> wordLabelListNew = targetDao
                    ._queryWord_WordLabelList(id);
            synchronized (this) {
                if (wordLabelList == null) {
                    wordLabelList = wordLabelListNew;
                }
            }
        }
        return wordLabelList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1340255977)
    public synchronized void resetWordLabelList() {
        wordLabelList = null;
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

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 2107838493)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWordDao() : null;
    }

    public double getAccuracy(){
        if(wordTimes == 0) {
            return 1;
        }
        return (double) wordCorrect / (double) wordTimes;
    }
}
