package com.example.success;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.success.entity.FriendShip;
import com.example.success.entity.Knowledge;
import com.example.success.entity.KnowledgeBlank;
import com.example.success.entity.KnowledgeHistory;
import com.example.success.entity.KnowledgeLabel;
import com.example.success.entity.KnowledgeTask;
import com.example.success.entity.Label;
import com.example.success.entity.User;
import com.example.success.entity.Word;
import com.example.success.entity.WordHistory;
import com.example.success.entity.WordLabel;
import com.example.success.entity.WordTask;
import com.example.success.generatedDao.DaoMaster;
import com.example.success.generatedDao.DaoSession;
import com.example.success.generatedDao.FriendShipDao;
import com.example.success.generatedDao.KnowledgeBlankDao;
import com.example.success.generatedDao.KnowledgeDao;
import com.example.success.generatedDao.KnowledgeHistoryDao;
import com.example.success.generatedDao.KnowledgeLabelDao;
import com.example.success.generatedDao.KnowledgeTaskDao;
import com.example.success.generatedDao.LabelDao;
import com.example.success.generatedDao.UserDao;
import com.example.success.generatedDao.WordDao;
import com.example.success.generatedDao.WordHistoryDao;
import com.example.success.generatedDao.WordLabelDao;
import com.example.success.generatedDao.WordTaskDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 数据库各种操作的接口：在 Activity 中调用
 */
public class DatabaseInterface {

    private final DaoSession daoSession;
    private final Context context;
    private final DaoMaster.DevOpenHelper helper;
    private final SQLiteDatabase db;
    private final DaoMaster daoMaster;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public DatabaseInterface(Context context) {
        this.context = context;
        this.helper = new DaoMaster.DevOpenHelper(context, "test.db");
        this.db = helper.getWritableDatabase();
        this.daoMaster = new DaoMaster(db);
        this.daoSession = daoMaster.newSession();
    }

    /**
     * User 相关操作
     */
    // 用户名，旧密码，新密码
    // 如果旧密码不对返回false
    // 否则返回 true
    public boolean changedPassword(String username, String oldPassword, String newPassword) {
        if (checkUserPassword(username, oldPassword)) {
            return false;
        }
        User user = getUserByName(username);
        user.setPassword(newPassword);
        daoSession.update(user);
        return true;
    }

    public boolean changedPasswordWithoutOld(String username, String newPassword) {
        User user = getUserByName(username);
        user.setPassword(newPassword);
        daoSession.update(user);
        return true;
    }

    public boolean usernameExists(String name) {
        return getUserByName(name) != null;
    }

    public boolean checkUserPassword(String name, String password) {
        User user = getUserByName(name);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }

    public User getUserByName(String name) {
        List<User> userList = daoSession.queryBuilder(User.class).where(
                UserDao.Properties.Name.eq(name)).list();
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    public User getUserById(Long id) {
        List<User> userList = daoSession.queryBuilder(User.class).where(
                UserDao.Properties.Id.eq(id)).list();
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        return userList.get(0);
    }

    /**
     * @param name     用户名
     * @param password 密码
     * @param noteTime 提醒时间
     * @return int(1 for success, 0 for false)
     */
    public int insertUser(String name, String password, int noteTime) {
        if (usernameExists(name)) {
            return 0;
        }
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setNoteTime(noteTime);
        daoSession.insert(user);
        return 1;
    }

    /**
     * @param name     用户名
     * @param password 密码
     * @return int(1 for success, 0 for no such user, 2 for wrong password)
     */
    public int userSignIn(String name, String password) {
        if (!usernameExists(name)) {
            return 0;
        }
        User user = getUserByName(name);
        if (user.getPassword().equals(password)) {
            return 1;
        }
        return 2;
    }

    // 用户头像类型： Bitmap
    public void insertUserPhoto(String name, Bitmap userPhotoBitmap) {
        User user = getUserByName(name);
        if (user != null) {
            user.setUserPhoto(bitmapToByteArray(userPhotoBitmap));
            daoSession.update(user);
        }
    }

    public void updateUser(User user) {
        daoSession.update(user);
    }

    // 删除用户，需要删除相应的 knowledge, word, knowledgeLabel, knowledgeBlank, wordLabel
    public void deleteUserByName(String name) {
        User user = getUserByName(name);
        if (user != null) {
            List<Knowledge> knowledgeList = user.getKnowledgeList();
            List<Word> wordList = user.getWordList();

            for (Knowledge knowledge : knowledgeList) {
                deleteKnowledge(knowledge);
            }
            for (Word word : wordList) {
                deleteWord(word);
            }
            daoSession.delete(user);
        }
    }

    public void deleteUser(User user) {
        if (user != null) {
            List<Knowledge> knowledgeList = user.getKnowledgeList();
            List<Word> wordList = user.getWordList();

            for (Knowledge knowledge : knowledgeList) {
                deleteKnowledge(knowledge);
            }
            for (Word word : wordList) {
                deleteWord(word);
            }
            daoSession.delete(user);
        }
    }

    /**
     * Label 相关操作
     */

    public void initLabel() {
        List<Label> labels = daoSession.queryBuilder(Label.class).list();
        if (labels == null || labels.size() == 0) {
            Label label = new Label();
            label.setLabel("#数学");
            daoSession.insert(label);
            label = new Label();
            label.setLabel("#计算机组成");
            daoSession.insert(label);
            label = new Label();
            label.setLabel("#编译原理");
            daoSession.insert(label);
            label = new Label();
            label.setLabel("#雅思");
            daoSession.insert(label);
            label = new Label();
            label.setLabel("#托福");
            daoSession.insert(label);
            label = new Label();
            label.setLabel("#六级");
            daoSession.insert(label);
        }
    }

    public Label getLabelByContent(String content) {
        if (content.length() > 0 && content.charAt(0) != '#') {
            content = "#" + content;
        }
        List<Label> labels = daoSession.queryBuilder(Label.class)
                .where(LabelDao.Properties.Label.eq(content)).list();
        if (labels == null || labels.isEmpty()) {
            return null;
        }
        return labels.get(0);
    }

    public Label getLabelById(Long id) {
        List<Label> labels = daoSession.queryBuilder(Label.class)
                .where(LabelDao.Properties.Id.eq(id)).list();
        if (labels == null || labels.isEmpty()) {
            return null;
        }
        return labels.get(0);
    }

    public int addLabel(String content) {
        if (content.length() > 0 && content.charAt(0) != '#') {
            content = "#" + content;
        }
        if (getLabelByContent(content) != null) {
            return 0;//表示已有相同的label
        }
        Label label = new Label();
        label.setLabel(content);
        daoSession.insert(label);
        return 1;
    }

    public int wordAddLabel(Word word, String content) {
        String noSpace = content.trim();
        if (noSpace.length() == 0) {
            return 2;
        }
        if (content.length() > 0 && content.charAt(0) != '#') {
            content = "#" + content;
        }
        addLabel(content);
        Label label = getLabelByContent(content);
        List<Label> labels = getWordLabel(word);
        if (labels.contains(label)) {
            return 0;
        }
        WordLabel wordLabel = new WordLabel();
        wordLabel.setLabelId(label.getId());
        wordLabel.setWordId(word.getId());
        daoSession.insert(wordLabel);
        return 1;
    }

    public void wordDeleteLabel(Word word, String content) {
        Label label = getLabelByContent(content);
        List<Label> labels = getWordLabel(word);
        if (labels.contains(label)) {
            WordLabel wordLabel = daoSession.queryBuilder(WordLabel.class).
                    where(WordLabelDao.Properties.WordId.eq(word.getId()),
                            WordLabelDao.Properties.LabelId.eq(label.getId())).unique();

            daoSession.delete(wordLabel);
        }
    }

    public void wordDeleteLabel(Word word) {
        List<Label> labels = getWordLabel(word);
        if (labels.size() == 1) {
            WordLabel wordLabel = daoSession.queryBuilder(WordLabel.class).
                    where(WordLabelDao.Properties.WordId.eq(word.getId()),
                            WordLabelDao.Properties.LabelId.eq(labels.get(0).getId())).unique();

            daoSession.delete(wordLabel);
        } else {
            System.err.println("单词和label应该是1对1");
        }
    }

    public int knowledgeAddLabel(Knowledge knowledge, String content) {
        String noSpace = content.trim();
        if (noSpace.length() == 0) {
            return 2;
        }
        if (content.length() > 0 && content.charAt(0) != '#') {
            content = "#" + content;
        }
        addLabel(content);
        Label label = getLabelByContent(content);
        List<Label> labels = getKnowledgeLabel(knowledge);
        if (labels.contains(label)) {
            return 0;
        }
        KnowledgeLabel knowledgeLabel = new KnowledgeLabel();
        knowledgeLabel.setKnowledgeId(knowledge.getId());
        knowledgeLabel.setLabelId(label.getId());
        daoSession.insert(knowledgeLabel);
        return 1;
    }

    public void knowledgeDeleteLabel(Knowledge knowledge, String content) {
        Label label = getLabelByContent(content);
        List<Label> labels = getKnowledgeLabel(knowledge);
        if (labels.contains(label)) {
            KnowledgeLabel knowledgeLabel = daoSession.queryBuilder(KnowledgeLabel.class).
                    where(KnowledgeLabelDao.Properties.KnowledgeId.eq(knowledge.getId()),
                            KnowledgeLabelDao.Properties.LabelId.eq(label.getId())).unique();

            daoSession.delete(knowledgeLabel);
        }
    }

    public void knowledgeDeleteLabel(Knowledge knowledge) {
        List<Label> labels = getKnowledgeLabel(knowledge);
        if (labels.size() == 1) {
            KnowledgeLabel knowledgeLabel = daoSession.queryBuilder(KnowledgeLabel.class).
                    where(KnowledgeLabelDao.Properties.KnowledgeId.eq(knowledge.getId()),
                            KnowledgeLabelDao.Properties.LabelId.eq(labels.get(0).getId())).unique();

            daoSession.delete(knowledgeLabel);
        } else {
            System.err.println("知识点和label应该是一对一关系");
        }
    }

    public List<Label> getUserLabel(String userName) {
        HashSet<Label> labels = new HashSet<>();
        User user = getUserByName(userName);
        List<Word> wordList = getAllWordByUserName(userName);
        for (Word word : wordList) {
            labels.addAll(getWordLabel(word));
        }
        List<Knowledge> knowledgeList = getAllKnowledgeByUserName(userName);
        for (Knowledge knowledge : knowledgeList) {
            labels.addAll(getKnowledgeLabel(knowledge));
        }
        return new ArrayList<>(labels);
    }

    public List<Label> getAllLabel() {
        return daoSession.queryBuilder(Label.class).list();
    }

    public List<Label> getWordLabel(Word word) {
        List<WordLabel> wordLabelList = daoSession.queryBuilder(WordLabel.class).
                where(WordLabelDao.Properties.WordId.eq(word.getId())).list();

        List<Label> labels = new ArrayList<>();

        for (WordLabel wordLabel : wordLabelList) {
            Label label = getLabelById(wordLabel.getLabelId());
            labels.add(label);
        }
        return labels;
    }

    public List<Label> getWordDonHaveLabel(Word word) {
        List<Label> allLabels = getAllLabel();
        List<Label> labels = getWordLabel(word);
        allLabels.removeAll(labels);
        return allLabels;
    }

    public List<Label> getKnowledgeDonHaveLabel(Knowledge knowledge) {
        List<Label> allLabels = getAllLabel();
        List<Label> labels = getKnowledgeLabel(knowledge);
        allLabels.removeAll(labels);
        return allLabels;
    }


    public List<Label> getKnowledgeLabel(Knowledge knowledge) {
        List<KnowledgeLabel> knowledgeLabelList = daoSession.queryBuilder(KnowledgeLabel.class).
                where(KnowledgeLabelDao.Properties.KnowledgeId.eq(knowledge.getId())).list();

        List<Label> labels = new ArrayList<>();


        for (KnowledgeLabel knowledgeLabel : knowledgeLabelList) {
            Label label = getLabelById(knowledgeLabel.getLabelId());
            labels.add(label);
        }
        return labels;
    }

    public List<Word> getWordByLabel(String content, String userName) {
        Label label = getLabelByContent(content);
        User user = getUserByName(userName);
        if (label == null) {
            return null;
        }
        List<WordLabel> wordLabelList = daoSession.queryBuilder(WordLabel.class).
                where(WordLabelDao.Properties.LabelId.eq(label.getId())).list();

        List<Word> wordList = new ArrayList<>();

        for (WordLabel wordLabel : wordLabelList) {
            Word word = getWordById(wordLabel.getWordId());
            if (word.getUserId().equals(user.getId())) {
                wordList.add(word);
            }
        }
        return wordList;
    }

    public List<Word> getWordByLabel(Long id, String userName) {
        Label label = getLabelById(id);
        User user = getUserByName(userName);
        if (label == null) {
            return null;
        }
        List<WordLabel> wordLabelList = daoSession.queryBuilder(WordLabel.class).
                where(WordLabelDao.Properties.LabelId.eq(label.getId())).list();

        List<Word> wordList = new ArrayList<>();

        for (WordLabel wordLabel : wordLabelList) {
            Word word = getWordById(wordLabel.getWordId());
            if (word.getUserId().equals(user.getId())) {
                wordList.add(word);
            }
        }
        return wordList;
    }

    public List<Knowledge> getKnowledgeByLabel(String content, String userName) {
        Label label = getLabelByContent(content);
        User user = getUserByName(userName);
        if (label == null) {
            return null;
        }
        List<KnowledgeLabel> knowledgeLabelList = daoSession.queryBuilder(KnowledgeLabel.class).
                where(KnowledgeLabelDao.Properties.LabelId.eq(label.getId())).list();

        List<Knowledge> knowledgeList = new ArrayList<>();

        for (KnowledgeLabel knowledgeLabel : knowledgeLabelList) {
            Knowledge knowledge = getKnowledgeById(knowledgeLabel.getKnowledgeId());
            if (knowledge.getUserId().equals(user.getId())) {
                knowledgeList.add(knowledge);
            }
        }
        return knowledgeList;
    }

    public List<Knowledge> getKnowledgeByLabel(Long id, String userName) {
        Label label = getLabelById(id);
        User user = getUserByName(userName);
        if (label == null) {
            return null;
        }
        List<KnowledgeLabel> knowledgeLabelList = daoSession.queryBuilder(KnowledgeLabel.class).
                where(KnowledgeLabelDao.Properties.LabelId.eq(id)).list();

        List<Knowledge> knowledgeList = new ArrayList<>();

        for (KnowledgeLabel knowledgeLabel : knowledgeLabelList) {
            Knowledge knowledge = getKnowledgeById(knowledgeLabel.getKnowledgeId());
            if (knowledge.getUserId().equals(user.getId())) {
                knowledgeList.add(knowledge);
            }
        }
        return knowledgeList;
    }

    /**
     * User、Knowledge 相关操作
     */


    public Knowledge getKnowledgeById(Long id) {
        return daoSession.queryBuilder(Knowledge.class).
                where(KnowledgeDao.Properties.Id.eq(id)).unique();
    }

    public Knowledge getKnowledgeByContent(String name, String content) {
        User user = getUserByName(name);
        return daoSession.queryBuilder(Knowledge.class)
                .where(KnowledgeDao.Properties.KnowledgeContent.eq(content),
                        KnowledgeDao.Properties.UserId.eq(user.getId())).unique();
    }

    public List<Knowledge> getAllKnowledgeByUserName(String userName) {
        User user = getUserByName(userName);
        if (user == null) {
            return null;
        }
        return user.getKnowledgeList();
    }


    public int insertKnowledge(String userName,
                               String knowledgeContent,
                               String title,
                               Bitmap knowledgePhoto) {
        User user = getUserByName(userName);
        if (title.isEmpty()) {
            return 3;
        }
        if (knowledgeContent.isEmpty()) {
            return 4;
        }
        List<Knowledge> knowledgeList = getAllKnowledgeByUserName(userName);
        for (Knowledge knowledge : knowledgeList) {
            if (knowledge.getKnowledgeContent().equals(knowledgeContent)) {
                return 0;
            }
        }
        if (user != null) {
            Knowledge knowledge = new Knowledge();
            knowledge.setKnowledgeCorrect(0);
            knowledge.setTimes(0);
            knowledge.setKnowledgeVague(0);
            knowledge.setUserId(user.getId());
            knowledge.setTitle(title);
            knowledge.setKnowledgeContent(knowledgeContent);
            knowledge.setKnowledgeDate(new Date());
            knowledge.setKnowledgePhoto(bitmapToByteArray(knowledgePhoto));
            user.getKnowledgeList().add(knowledge);
            // knowledge.setKnowledgeDate(currentDate()); 刚开始没背诵过，为 Null
            daoSession.insert(knowledge);
            daoSession.update(user);

            knowledge = getKnowledgeByContent(userName, knowledgeContent);
            updateKnowledgeTask(knowledge, userName);
            return 1;
        }
        return 2;
    }

    public int updateKnowledge(String userName,
                               String oldContent,
                               String knowledgeContent,
                               String title,
                               Bitmap knowledgePhoto,
                               String labelContent) {
        if (title.isEmpty()) {
            return 2;
        }
        if (knowledgeContent.isEmpty()) {
            return 3;
        }

        if (!oldContent.equals(knowledgeContent)) {
            List<Knowledge> knowledgeList = getAllKnowledgeByUserName(userName);
            for (Knowledge knowledge : knowledgeList) {
                if (knowledge.getKnowledgeContent().equals(knowledgeContent)) {
                    return 0;
                }
            }
        }
        User user = getUserByName(userName);
        for (Knowledge knowledge : user.getKnowledgeList()) {
            if (knowledge.getKnowledgeContent().equals(oldContent)) {
                user.getKnowledgeList().remove(knowledge);
                break;
            }
        }
        Knowledge knowledge = getKnowledgeByContent(userName, oldContent);
        ArrayList<KnowledgeBlank> knowledgeBlanks = (ArrayList<KnowledgeBlank>) knowledge.getKnowledgeBlankList();
        Iterator<KnowledgeBlank> it = knowledgeBlanks.iterator();
        while (it.hasNext()) {
            KnowledgeBlank blank = it.next();
            if (!knowledgeContent.contains(blank.getBlank())) {
                deleteKnowledgeBlank(blank);
                it.remove();
            }
        }

        user.getKnowledgeList().remove(knowledge);
        knowledge.setTitle(title);
        knowledge.setKnowledgeContent(knowledgeContent);
        knowledge.setKnowledgePhoto(bitmapToByteArray(knowledgePhoto));
        user.getKnowledgeList().add(knowledge);
        daoSession.update(knowledge);
        daoSession.update(user);

        List<Label> labelList = getKnowledgeLabel(knowledge);
        if (labelList == null || labelList.size() == 0) {
            knowledgeAddLabel(knowledge, labelContent);
            return 1;
        } else {
            knowledgeDeleteLabel(knowledge);
            knowledgeAddLabel(knowledge, labelContent);
            return 1;
        }
    }


    public void uploadKnowledgePhoto(Knowledge knowledge, Bitmap bitmap) {
        knowledge.setKnowledgePhoto(bitmapToByteArray(bitmap));
        daoSession.update(knowledge);
    }

    // 删除知识点，需要先删除 知识点标签 和 知识点挖空
    public void deleteKnowledge(Knowledge knowledge) {
        if (knowledge != null) {
            List<KnowledgeBlank> knowledgeBlankList = knowledge.getKnowledgeBlankList();
            List<KnowledgeLabel> knowledgeLabelList = knowledge.getKnowledgeLabelList();

            for (KnowledgeBlank knowledgeBlank : knowledgeBlankList) {
                deleteKnowledgeBlank(knowledgeBlank);
            }
            for (KnowledgeLabel knowledgeLabel : knowledgeLabelList) {
                deleteKnowledgeLabel(knowledgeLabel);
            }
            daoSession.delete(knowledge);
        }
    }

    public void deleteKnowledge(String userName, String content) {
        Knowledge knowledge = getKnowledgeByContent(userName, content);

        User user = getUserByName(userName);
        for (Knowledge knowledge1 : user.getKnowledgeList()) {
            if (knowledge1.getId().equals(knowledge.getId())) {
                user.getKnowledgeList().remove(knowledge1);
                break;
            }
        }
        if (knowledge.getKnowledgeLabelList() != null && knowledge.getKnowledgeLabelList().size() != 0) {
            if (knowledge.getKnowledgeLabelList().size() != 1) {
                System.err.println("删除单词，不满足label一对一");
            } else {
                daoSession.delete(knowledge.getKnowledgeLabelList().get(0));
                knowledge.getKnowledgeLabelList().remove(0);
            }
        }

        List<KnowledgeTask> tasks = daoSession.queryBuilder(KnowledgeTask.class).list();
        for (KnowledgeTask task : tasks) {
            if (task.getKnowledgeId().equals(knowledge.getId())) {
                daoSession.delete(task);
            }
        }

        List<KnowledgeHistory> histories = daoSession.queryBuilder(KnowledgeHistory.class).list();
        for (KnowledgeHistory history : histories) {
            if (history.getKnowledgeId().equals(knowledge.getId())) {
                daoSession.delete(history);
            }
        }

        List<KnowledgeBlank> blankList = daoSession.queryBuilder(KnowledgeBlank.class).list();
        for (KnowledgeBlank blank : blankList) {
            if (blank.getKnowledgeId().equals(knowledge.getId())) {
                daoSession.delete(blank);
            }
        }

        daoSession.delete(knowledge);
        daoSession.update(user);

    }

    /**
     * User、Knowledge、KnowledgeLabel 相关操作
     */

    public List<KnowledgeLabel> getAllKnowledgeLabelByKnowledge(Knowledge knowledge) {
        return knowledge.getKnowledgeLabelList();
    }

    public List<KnowledgeLabel> getAllKnowledgeLabelByUser(User user) {
        List<Knowledge> knowledgeList = getAllKnowledgeByUserName(user.getName());
        if (knowledgeList != null) {
            List<KnowledgeLabel> labels = new ArrayList<>();
            for (Knowledge knowledge : knowledgeList) {
                labels.addAll(knowledge.getKnowledgeLabelList());
            }
            return labels;
        }
        return null;
    }

    public List<KnowledgeLabel> getAllKnowledgeLabelByUserName(String username) {
        List<Knowledge> knowledgeList = getAllKnowledgeByUserName(username);
        if (knowledgeList != null) {
            List<KnowledgeLabel> labels = new ArrayList<>();
            for (Knowledge knowledge : knowledgeList) {
                labels.addAll(knowledge.getKnowledgeLabelList());
            }
            return labels;
        }
        return null;
    }


    public void updateKnowledgeLabel(KnowledgeLabel knowledgeLabel) {
        daoSession.update(knowledgeLabel);
    }

    public void deleteKnowledgeLabel(KnowledgeLabel knowledgeLabel) {
        daoSession.delete(knowledgeLabel);
    }

    /**
     * User、Knowledge、KnowledgeBlank 相关操作
     */

    public void deleteTodayKnowledgeTask(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<KnowledgeTask> taskList = daoSession.queryBuilder(KnowledgeTask.class)
                .where(KnowledgeTaskDao.Properties.Date.eq(date),
                        KnowledgeTaskDao.Properties.UserId.eq(user.getId())).list();

        for (KnowledgeTask knowledgeTask : taskList) {
            daoSession.delete(knowledgeTask);
        }
    }

    public void updateKnowledgeTask(Knowledge knowledge, String username) {
        List<Knowledge> tasks = findTodayKnowledgeForReview(username);
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        if (tasks != null && tasks.size() < user.getKnowledgeTaskNum()) {
            System.out.println("new task！");
            KnowledgeTask task = new KnowledgeTask();
            task.setKnowledgeId(knowledge.getId());
            task.setDate(date);
            task.setUserId(knowledge.getUserId());
            task.setStatus(0);
            daoSession.insert(task);
        } else if (tasks == null) {
            tasks = getNewKnowledgeTask(username);
            for (Knowledge knowledge1 : tasks) {
                KnowledgeTask task = new KnowledgeTask();
                task.setKnowledgeId(knowledge1.getId());
                task.setDate(date);
                task.setUserId(knowledge1.getUserId());
                task.setStatus(0);
                daoSession.insert(task);
            }
        }
    }


    public List<Knowledge> getKnowledgeTaskForReview(String username) {
        List<Knowledge> tasks = findTodayKnowledgeForReview(username);
        User user = getUserByName(username);
        if (tasks != null) {
            return tasks;
        }
        tasks = getNewKnowledgeTask(username);
        String date = sdf.format(new Date());
        for (Knowledge knowledge : tasks) {
            KnowledgeTask task = new KnowledgeTask();
            task.setKnowledgeId(knowledge.getId());
            task.setDate(date);
            task.setUserId(knowledge.getUserId());
            task.setStatus(0);
            daoSession.insert(task);
        }
        return tasks;
    }

    private List<Knowledge> findTodayKnowledgeForReview(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<KnowledgeTask> taskList = daoSession.queryBuilder(KnowledgeTask.class)
                .where(KnowledgeTaskDao.Properties.Date.eq(date),
                        KnowledgeTaskDao.Properties.UserId.eq(user.getId())).list();
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }
        ArrayList<Knowledge> knowledgeList = new ArrayList<>();

        for (KnowledgeTask task : taskList) {
            Knowledge knowledge = getKnowledgeById(task.getKnowledgeId());
            knowledgeList.add(knowledge);
        }
        return knowledgeList;
    }

    public List<Knowledge> getKnowledgeTask(String username) {
        List<Knowledge> tasks = findTodayKnowledgeTask(username);
        if (tasks != null) {
            System.out.println("get task1:" + tasks.size());
            return tasks;
        }
        if (finishTodayKnowledgeTask(username)) {
            System.out.println("get task:" + 0);
            return new ArrayList<>();
        }
        tasks = getNewKnowledgeTask(username);
        String date = sdf.format(new Date());
        for (Knowledge knowledge : tasks) {
            KnowledgeTask task = new KnowledgeTask();
            task.setKnowledgeId(knowledge.getId());
            task.setDate(date);
            task.setUserId(knowledge.getUserId());
            task.setStatus(0);
            daoSession.insert(task);
        }
        System.out.println("get task:" + tasks.size());
        return tasks;
    }

    public void setKnowledgeTaskFinish(Knowledge knowledge) {
        String date = sdf.format(new Date());
        KnowledgeTask task = daoSession.queryBuilder(KnowledgeTask.class)
                .where(KnowledgeTaskDao.Properties.Date.eq(date),
                        KnowledgeTaskDao.Properties.KnowledgeId.eq(knowledge.getId())).unique();

        task.setStatus(1);
        daoSession.update(task);
    }

    private boolean finishTodayKnowledgeTask(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<KnowledgeTask> taskList = daoSession.queryBuilder(KnowledgeTask.class)
                .where(KnowledgeTaskDao.Properties.Date.eq(date),
                        KnowledgeTaskDao.Properties.UserId.eq(user.getId())).list();

        List<KnowledgeTask> finishList = daoSession.queryBuilder(KnowledgeTask.class)
                .where(KnowledgeTaskDao.Properties.Date.eq(date),
                        KnowledgeTaskDao.Properties.Status.eq(1),
                        KnowledgeTaskDao.Properties.UserId.eq(user.getId())).list();

        if (taskList != null && !taskList.isEmpty()) {
            if (finishList == null || finishList.isEmpty()) {
                return false;
            }
            return taskList.size() == finishList.size();
        }
        return false;
    }

    private List<Knowledge> findTodayKnowledgeTask(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<KnowledgeTask> taskList = daoSession.queryBuilder(KnowledgeTask.class)
                .where(KnowledgeTaskDao.Properties.Date.eq(date),
                        KnowledgeTaskDao.Properties.Status.eq(0),
                        KnowledgeTaskDao.Properties.UserId.eq(user.getId())).list();

        if (taskList == null || taskList.isEmpty()) {
            return null;
        }
        ArrayList<Knowledge> knowledgeList = new ArrayList<>();

        for (KnowledgeTask task : taskList) {
            //System.out.println("taskstatus:" + task.getStatus() + " " + task.getKnowledgeId());
            Knowledge knowledge = getKnowledgeById(task.getKnowledgeId());
            knowledgeList.add(knowledge);
        }
        return knowledgeList;
    }

    public List<Knowledge> getNewKnowledgeTask(String userName) {
        User user = getUserByName(userName);
        ArrayList<Knowledge> allKnowledge = (ArrayList<Knowledge>) getAllKnowledgeByUserName(userName);
        int totalTask = user.getKnowledgeTaskNum();
        if (allKnowledge.size() <= totalTask) {
            return allKnowledge;
        } else { //选择策略：先选没背过的，之后平分任务量，一半为时间最久的，另一半为正确率最低的(方法需要测试)
            ArrayList<Knowledge> task = new ArrayList<>();
            ArrayList<Knowledge> restTask = new ArrayList<>();
            for (Knowledge knowledge : allKnowledge) {
                if (knowledge.getKnowledgeDate() == null) {
                    task.add(knowledge);
                    if (task.size() == totalTask) {
                        return task;
                    }
                } else {
                    restTask.add(knowledge);
                }
            }
            int wrongTask = (totalTask - task.size()) / 2;
            int oldTask = totalTask - wrongTask - task.size();
            restTask.sort((n1, n2) -> {
                try {
                    return n1.getKnowledgeDate().compareTo(n2.getKnowledgeDate());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            for (int i = oldTask - 1; i >= 0; i--) {
                task.add(restTask.get(i));
                restTask.remove(i);
            }
            restTask.sort((n1, n2) -> {
                try {
                    return Double.compare(n1.getAccuracy(), n2.getAccuracy());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            for (int i = 0; i < wrongTask; i++) {
                task.add(restTask.get(i));
            }
            return task;
        }
    }

    public void addCorrectTimeForKnowledge(Knowledge knowledge) {
        addKnowledgeHistory(knowledge, "Correct");
        setKnowledgeTaskFinish(knowledge);
        knowledge.setTimes(knowledge.getTimes() + 1);
        knowledge.setKnowledgeCorrect(knowledge.getKnowledgeCorrect() + 1);
        Date date = new Date();
        knowledge.setKnowledgeDate(date);
        daoSession.update(knowledge);
    }

    public void addVagueTimeForKnowledge(Knowledge knowledge) {
        addKnowledgeHistory(knowledge, "Vague");
        setKnowledgeTaskFinish(knowledge);
        knowledge.setTimes(knowledge.getTimes() + 1);
        knowledge.setKnowledgeVague(knowledge.getKnowledgeVague() + 1);
        Date date = new Date();
        knowledge.setKnowledgeDate(date);
        daoSession.update(knowledge);
    }

    public void addWrongTimeForKnowledge(Knowledge knowledge) {
        addKnowledgeHistory(knowledge, "Wrong");
        setKnowledgeTaskFinish(knowledge);
        knowledge.setTimes(knowledge.getTimes() + 1);
        Date date = new Date();
        knowledge.setKnowledgeDate(date);
        daoSession.update(knowledge);
    }

    // 返回挖空后的知识点内容，每个元素之间即为空
    public String[] getKnowledgeContentWithBlanks(Knowledge knowledge) {
        // 挖的所有空
        Set<String> blanks = knowledge.getKnowledgeBlankList().stream().map(
                KnowledgeBlank::getBlank).collect(Collectors.toSet());
        // splitters 分隔符的正则表达式
        String splitters = String.join("|", blanks);
        return knowledge.getKnowledgeContent().split(splitters);
    }

    public List<KnowledgeBlank> getAllKnowledgeBlankByKnowledge(Knowledge knowledge) {
        return knowledge.getKnowledgeBlankList();
    }

    public List<KnowledgeBlank> getAllKnowledgeBlankByUser(User user) {
        List<Knowledge> knowledgeList = getAllKnowledgeByUserName(user.getName());
        if (knowledgeList != null) {
            List<KnowledgeBlank> blanks = new ArrayList<>();
            for (Knowledge knowledge : knowledgeList) {
                blanks.addAll(knowledge.getKnowledgeBlankList());
            }
            return blanks;
        }
        return null;
    }

    public List<KnowledgeBlank> getAllKnowledgeBlankByUsername(String username) {
        List<Knowledge> knowledgeList = getAllKnowledgeByUserName(username);
        if (knowledgeList != null) {
            List<KnowledgeBlank> blanks = new ArrayList<>();
            for (Knowledge knowledge : knowledgeList) {
                blanks.addAll(knowledge.getKnowledgeBlankList());
            }
            return blanks;
        }
        return null;
    }

    public void insertKnowledgeBlank(Knowledge knowledge, ArrayList<String> blanks) {
        for (String blank : blanks) {
            insertKnowledgeBlank(knowledge, blank);
        }
    }

    public List<KnowledgeBlank> getKnowledgeAllBlank(Knowledge knowledge) {
        return daoSession.queryBuilder(KnowledgeBlank.class)
                .where(KnowledgeBlankDao.Properties.KnowledgeId.eq(knowledge.getId())).list();
    }

    public void insertKnowledgeBlank(Knowledge knowledge, String blank) {
        List<KnowledgeBlank> blankList = getKnowledgeAllBlank(knowledge);
        for (KnowledgeBlank knowledgeBlank : blankList) {
            if (knowledgeBlank.getBlank().equals(blank)) {
                return;
            }
        }
        KnowledgeBlank knowledgeBlank = new KnowledgeBlank();
        knowledgeBlank.setKnowledgeId(knowledge.getId());
        knowledgeBlank.setBlank(blank);
        knowledge.getKnowledgeBlankList().add(knowledgeBlank);
        daoSession.insert(knowledgeBlank);
        daoSession.update(knowledge);
    }

    public void updateKnowledgeBlank(KnowledgeBlank knowledgeBlank) {
        daoSession.update(knowledgeBlank);
    }

    public void deleteKnowledgeBlank(KnowledgeBlank knowledgeBlank) {
        daoSession.delete(knowledgeBlank);
    }

    /**
     * User、Word 相关操作
     */
    public Word getWordById(Long id) {
        return daoSession.queryBuilder(Word.class).
                where(WordDao.Properties.Id.eq(id)).unique();
    }

    public int updateWord(String userName, String oldEnglish,
                          String wordChinese,
                          String wordEnglish,
                          Bitmap wordPhoto,
                          String labelContent) {
        if (wordEnglish.isEmpty()) {
            return 0;
        }
        if (!oldEnglish.equals(wordEnglish)) {
            List<Word> wordList = getAllWordByUserName(userName);
            for (Word word : wordList) {
                if (word.getWordEnglish().equals(wordEnglish)) {
                    return 2;
                }
            }
        }
        User user = getUserByName(userName);
        for (Word word1 : user.getWordList()) {
            if (word1.getWordEnglish().equals(oldEnglish)) {
                user.getWordList().remove(word1);
                break;
            }
        }
        Word word = getWordByEnglish(userName, oldEnglish);
        user.getWordList().remove(word);
        word.setWordChinese(wordChinese);
        word.setWordEnglish(wordEnglish);
        word.setWordPhoto(bitmapToByteArray(wordPhoto));
        user.getWordList().add(word);
        daoSession.update(word);
        daoSession.update(user);

        List<Label> labelList = getWordLabel(word);
        if (labelList == null || labelList.size() == 0) {
            wordAddLabel(word, labelContent);
            return 1;
        } else {
            wordDeleteLabel(word);
            wordAddLabel(word, labelContent);
            return 1;
        }
    }

    public void deleteWordTask(WordTask wordTask) {
        daoSession.delete(wordTask);
    }

    public void deleteTodayWordTask(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<WordTask> taskList = daoSession.queryBuilder(WordTask.class)
                .where(WordTaskDao.Properties.Date.eq(date),
                        WordTaskDao.Properties.UserId.eq(user.getId())).list();

        for (WordTask wordTask : taskList) {
            daoSession.delete(wordTask);
        }
    }

    public void updateWordTask(Word newWord, String username) {
        List<Word> tasks = findTodayWordForReview(username);
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        if (tasks != null && tasks.size() < user.getWordTaskNum()) {
            System.out.println("new task！");
            WordTask task = new WordTask();
            task.setWordId(newWord.getId());
            task.setDate(date);
            task.setUserId(newWord.getUserId());
            task.setStatus(0);
            daoSession.insert(task);
        } else if (tasks == null) {
            tasks = getNewWordTask(username);
            for (Word word : tasks) {
                WordTask task = new WordTask();
                task.setWordId(word.getId());
                task.setDate(date);
                task.setUserId(word.getUserId());
                task.setStatus(0);
                daoSession.insert(task);
            }
        }

    }

    public List<Word> getWordTaskForReview(String username) {
        List<Word> tasks = findTodayWordForReview(username);
        User user = getUserByName(username);
        if (tasks != null) {
            return tasks;
        }

        tasks = getNewWordTask(username);
        String date = sdf.format(new Date());
        for (Word word : tasks) {
            WordTask task = new WordTask();
            task.setWordId(word.getId());
            task.setDate(date);
            task.setUserId(word.getUserId());
            task.setStatus(0);
            daoSession.insert(task);
        }
        return tasks;
    }

    private List<Word> findTodayWordForReview(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<WordTask> taskList = daoSession.queryBuilder(WordTask.class)
                .where(WordTaskDao.Properties.Date.eq(date),
                        WordTaskDao.Properties.UserId.eq(user.getId())).list();
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }
        ArrayList<Word> wordList = new ArrayList<>();

        for (WordTask task : taskList) {
            Word word = getWordById(task.getWordId());
            wordList.add(word);
        }
        return wordList;
    }

    public List<Word> getWordTask(String username) {
        List<Word> tasks = findTodayWordTask(username);
        //任务不为空直接返回word列表
        if (tasks != null) {
            return tasks;
        }
        //任务为空时判断是否完成当日任务，完成后返回空列表
        if (finishTodayWordTask(username)) {
            return new ArrayList<>();
        }
        //此时可判断还未建立当日任务，获取新任务，添加到每日任务中并返回word列表
        tasks = getNewWordTask(username);
        String date = sdf.format(new Date());
        for (Word word : tasks) {
            WordTask task = new WordTask();
            task.setWordId(word.getId());
            task.setDate(date);
            task.setUserId(word.getUserId());
            task.setStatus(0);
            daoSession.insert(task);
        }
        return tasks;
    }

    public void setWordTaskFinish(Word word) {
        String date = sdf.format(new Date());
        WordTask task = daoSession.queryBuilder(WordTask.class)
                .where(WordTaskDao.Properties.Date.eq(date),
                        WordTaskDao.Properties.WordId.eq(word.getId())).unique();

        task.setStatus(1);
        daoSession.update(task);
    }

    private boolean finishTodayWordTask(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<WordTask> taskList = daoSession.queryBuilder(WordTask.class)
                .where(WordTaskDao.Properties.Date.eq(date),
                        WordTaskDao.Properties.UserId.eq(user.getId())).list();

        List<WordTask> finishList = daoSession.queryBuilder(WordTask.class)
                .where(WordTaskDao.Properties.Date.eq(date),
                        WordTaskDao.Properties.Status.eq(1),
                        WordTaskDao.Properties.UserId.eq(user.getId())).list();

        if (taskList != null && !taskList.isEmpty()) {
            if (finishList == null || finishList.isEmpty()) {
                return false;
            }
            return taskList.size() == finishList.size();
        }
        return false;
    }

    private List<Word> findTodayWordTask(String username) {
        User user = getUserByName(username);
        String date = sdf.format(new Date());
        List<WordTask> taskList = daoSession.queryBuilder(WordTask.class)
                .where(WordTaskDao.Properties.Date.eq(date),
                        WordTaskDao.Properties.Status.eq(0),
                        WordTaskDao.Properties.UserId.eq(user.getId())).list();
        if (taskList == null || taskList.isEmpty()) {
            return null;
        }
        ArrayList<Word> wordList = new ArrayList<>();

        for (WordTask task : taskList) {
            Word word = getWordById(task.getWordId());
            wordList.add(word);
        }
        return wordList;
    }


    public List<Word> getNewWordTask(String userName) {
        User user = getUserByName(userName);
        ArrayList<Word> allWords = (ArrayList<Word>) user.getWordList();
        int totalTask = user.getWordTaskNum();
        if (allWords.size() <= totalTask) {
            return allWords;
        } else { //选择策略：先选没背过的，之后平分任务量，一半为时间最久的，另一半为正确率最低的(方法需要测试)
            ArrayList<Word> task = new ArrayList<>();
            ArrayList<Word> restTask = new ArrayList<>();
            for (Word word : allWords) {
                if (word.getWordDate() == null) {
                    task.add(word);
                    if (task.size() == totalTask) {
                        return task;
                    }
                } else {
                    restTask.add(word);
                }
            }
            int wrongTask = (totalTask - task.size()) / 2;
            int oldTask = totalTask - wrongTask - task.size();
            restTask.sort((n1, n2) -> {
                try {
                    return n1.getWordDate().compareTo(n2.getWordDate());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            for (int i = oldTask - 1; i >= 0; i--) {
                task.add(restTask.get(i));
                restTask.remove(i);
            }
            restTask.sort((n1, n2) -> {
                try {
                    return Double.compare(n1.getAccuracy(), n2.getAccuracy());
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            });
            for (int i = 0; i < wrongTask; i++) {
                task.add(restTask.get(i));
            }
            return task;
        }
    }

    public void addCorrectTimeForWord(Word word) {
        addWordHistory(word, "Correct");
        setWordTaskFinish(word);
        word.setWordTimes(word.getWordTimes() + 1);
        word.setWordCorrect(word.getWordCorrect() + 1);
        Date date = new Date();
        word.setWordDate(date);
        daoSession.update(word);
    }

    public void addVagueTimeForWord(Word word) {
        addWordHistory(word, "Vague");
        setWordTaskFinish(word);
        word.setWordTimes(word.getWordTimes() + 1);
        word.setWordVague(word.getWordVague() + 1);
        Date date = new Date();
        word.setWordDate(date);
        daoSession.update(word);
    }

    public void addWrongTimeForWord(Word word) {
        addWordHistory(word, "Wrong");
        setWordTaskFinish(word);
        word.setWordTimes(word.getWordTimes() + 1);
        Date date = new Date();
        word.setWordDate(date);
        daoSession.update(word);
    }


    public List<Word> getAllWordByUserName(String userName) {
        User user = getUserByName(userName);
        if (user == null) {
            return null;
        }
        return user.getWordList();
    }

    public Word getWordByEnglish(String username, String english) {
        User user = getUserByName(username);

        return daoSession.queryBuilder(Word.class).
                where(WordDao.Properties.WordEnglish.eq(english),
                        WordDao.Properties.UserId.eq(user.getId())).unique();
    }

    public void deleteWordByEnglish(String username, String english) {
        Word word = getWordByEnglish(username, english);
        if (word != null) {
            daoSession.delete(word);
        }
    }

    public int insertWord(String userName,
                          String wordChinese,
                          String wordEnglish,
                          Bitmap wordPhoto) {
        User user = getUserByName(userName);
        if (wordEnglish.isEmpty()) {
            return 3;
        }
        List<Word> wordList = getAllWordByUserName(userName);
        for (Word word : wordList) {
            if (word.getWordEnglish().equals(wordEnglish)) {
                return 0;
            }
        }
        if (user != null) {
            Word word = new Word();
            word.setUserId(user.getId());
//            if (isChinese) {
//                word.setWordChinese(wordContent);
//            } else {
//                word.setWordEnglish(wordContent);
//            }
            word.setWordChinese(wordChinese);
            word.setWordEnglish(wordEnglish);
            word.setWordDate(null);
            word.setWordPhoto(bitmapToByteArray(wordPhoto));
            word.setWordTimes(0);
            word.setWordCorrect(0);
            word.setWordVague(0);
            user.getWordList().add(word);
            daoSession.insert(word);
            daoSession.update(user);

            word = getWordByEnglish(userName, wordEnglish);
            updateWordTask(word, userName);
            return 1;
        }
        return 2;
    }

    public void uploadWordPhoto(Word word, Bitmap wordPhoto) {
        word.setWordPhoto(bitmapToByteArray(wordPhoto));
        daoSession.update(word);
    }

    //public void updateWord(Word word) {
//        daoSession.update(word);
//    }

    // 删除知识点，需要先删除 知识点标签 和 知识点挖空
    public void deleteWord(Word word) {
        if (word != null) {
            List<WordLabel> wordLabelList = word.getWordLabelList();

            for (WordLabel wordLabel : wordLabelList) {
                deleteWordLabel(wordLabel);
            }
            daoSession.delete(word);
        }
    }

    public void deleteWord(String userName, String English) {
        Word word = getWordByEnglish(userName, English);
        User user = getUserByName(userName);
        for (Word word1 : user.getWordList()) {
            if (word1.getId().equals(word.getId())) {
                user.getWordList().remove(word1);
                break;
            }
        }
        if (word.getWordLabelList() != null && word.getWordLabelList().size() != 0) {
            if (word.getWordLabelList().size() != 1) {
                System.err.println("删除单词，不满足label一对一");
            } else {
                daoSession.delete(word.getWordLabelList().get(0));
                word.getWordLabelList().remove(0);
            }
        }

        List<WordTask> tasks = daoSession.queryBuilder(WordTask.class).list();
        for (WordTask task : tasks) {
            if (task.getWordId().equals(word.getId())) {
                daoSession.delete(task);
            }
        }

        List<WordHistory> histories = daoSession.queryBuilder(WordHistory.class).list();
        for (WordHistory history : histories) {
            if (history.getWordId().equals(word.getId())) {
                daoSession.delete(history);
            }
        }

        daoSession.delete(word);
        daoSession.update(user);

    }


    /**
     * User、Word、WordLabel 相关操作
     */
    public List<WordLabel> getAllWordLabelByWord(Word word) {
        return word.getWordLabelList();
    }

    public List<WordLabel> getAllWordLabelByUser(User user) {
        List<Word> wordList = getAllWordByUserName(user.getName());
        if (wordList != null) {
            List<WordLabel> labels = new ArrayList<>();
            for (Word word : wordList) {
                labels.addAll(word.getWordLabelList());
            }
            return labels;
        }
        return null;
    }

    public List<WordLabel> getAllWordLabelByUsername(String username) {
        List<Word> wordList = getAllWordByUserName(username);
        if (wordList != null) {
            List<WordLabel> labels = new ArrayList<>();
            for (Word word : wordList) {
                labels.addAll(word.getWordLabelList());
            }
            return labels;
        }
        return null;
    }

    public void updateWordLabel(WordLabel wordLabel) {
        daoSession.update(wordLabel);
    }

    public void deleteWordLabel(WordLabel wordLabel) {
        daoSession.delete(wordLabel);
    }

    /**
     * WordHistory 单词记录历史
     * KnowledgeHistory 知识点记录历史
     */
    public void addWordHistory(Word word, String status) {
        String date = sdf.format(new Date());
        WordHistory history = new WordHistory();
        history.setDate(date);
        history.setWordId(word.getId());
        history.setStatus(status);
        daoSession.insert(history);
    }

    public void addKnowledgeHistory(Knowledge knowledge, String status) {
        String date = sdf.format(new Date());
        KnowledgeHistory history = new KnowledgeHistory();
        history.setDate(date);
        history.setKnowledgeId(knowledge.getId());
        history.setStatus(status);
        daoSession.insert(history);
    }

    public List<WordHistory> getWordHistoryByWord(Word word) {
        return daoSession.queryBuilder(WordHistory.class).
                where(WordHistoryDao.Properties.WordId.eq(word.getId())).list();
    }

    public List<KnowledgeHistory> getKnowledgeHistoryByWord(Knowledge knowledge) {
        return daoSession.queryBuilder(KnowledgeHistory.class).
                where(KnowledgeLabelDao.Properties.KnowledgeId.eq(knowledge.getId())).list();
    }

    public int countUserTodayWord(String username) {
        List<WordHistory> histories = daoSession.queryBuilder(WordHistory.class).
                where(WordHistoryDao.Properties.Date.eq(sdf.format(currentDate()))).list();
        User user = getUserByName(username);
        List<Word> wordList = new ArrayList<>();
        for (WordHistory history : histories) {
            Word word = getWordById(history.getWordId());
            if (word.getUserId().equals(user.getId())) {
                wordList.add(word);
            }
        }
        return wordList.size();
    }

    //查询用户某日单词背诵数量（参数：String 用户名， String date）
    //注意日期形式为"yyyy-MM-dd"例如: 2022-12-29
    public int countUserDailyWord(String username, String date) {
        List<WordHistory> histories = daoSession.queryBuilder(WordHistory.class).
                where(WordHistoryDao.Properties.Date.eq(date)).list();
        User user = getUserByName(username);
        List<Word> wordList = new ArrayList<>();
        for (WordHistory history : histories) {
            Word word = getWordById(history.getWordId());
            if (word.getUserId().equals(user.getId())) {
                wordList.add(word);
            }
        }
        return wordList.size();
    }

    // 查询用户近一周单词背诵数量
    public int[] countUserWeekWord(String username) {
        int[] wordNum = new int[7];  // wordNum[i]: i 天前背诵单词数量
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; ++i) {
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            wordNum[i] = countUserDailyWord(username, sdf.format(calendar.getTime()));
        }
        return wordNum;
    }

    public int countUserTodayKnowledge(String username) {
        List<KnowledgeHistory> histories = daoSession.queryBuilder(KnowledgeHistory.class).
                where(KnowledgeHistoryDao.Properties.Date.eq(sdf.format(currentDate()))).list();
        User user = getUserByName(username);
        List<Knowledge> knowledgeList = new ArrayList<>();
        for (KnowledgeHistory history : histories) {
            Knowledge knowledge = getKnowledgeById(history.getKnowledgeId());
            if (knowledge.getUserId().equals(user.getId())) {
                knowledgeList.add(knowledge);
            }
        }
        return knowledgeList.size();
    }

    public int countUserDailyKnowledge(String username, String date) {
        List<KnowledgeHistory> histories = daoSession.queryBuilder(KnowledgeHistory.class).
                where(KnowledgeHistoryDao.Properties.Date.eq(date)).list();
        User user = getUserByName(username);
        List<Knowledge> knowledgeList = new ArrayList<>();
        for (KnowledgeHistory history : histories) {
            Knowledge knowledge = getKnowledgeById(history.getKnowledgeId());
            if (knowledge.getUserId().equals(user.getId())) {
                knowledgeList.add(knowledge);
            }
        }
        return knowledgeList.size();
    }

    // 查询用户近一周知识点背诵数量
    public int[] countUserWeekKnowledge(String username) {
        int[] knowledgeNum = new int[7];  // wordNum[i]: i 天前背诵单词数量
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; ++i) {
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            knowledgeNum[i] = countUserDailyKnowledge(username, sdf.format(calendar.getTime()));
        }
        return knowledgeNum;
    }

    /**
     * 查询相关操作
     */
    public List<Word> searchWord(String userName, String key) {
        User user = getUserByName(userName);
        List<Word> result = new ArrayList<>();
        List<Word> wordList = daoSession.queryBuilder(Word.class).
                where(WordDao.Properties.UserId.eq(user.getId())).list();

        for (Word word : wordList) {
            if (word.getWordEnglish().contains(key)) {
                result.add(word);
                continue;
            }
            if (word.getWordChinese().contains(key)) {
                result.add(word);
                continue;
            }
            List<Label> labels = getWordLabel(word);
            if (labels != null && labels.size() != 0) {
                if (labels.get(0).getLabel().contains(key)) {
                    result.add(word);
                }
            }
        }
        return result;
    }

    public List<Knowledge> searchKnowledge(String userName, String key) {
        User user = getUserByName(userName);
        List<Knowledge> result = new ArrayList<>();
        List<Knowledge> knowledgeList = daoSession.queryBuilder(Knowledge.class).
                where(KnowledgeDao.Properties.UserId.eq(user.getId())).list();

        for (Knowledge knowledge : knowledgeList) {
            if (knowledge.getTitle().contains(key)) {
                result.add(knowledge);
                continue;
            }
            if (knowledge.getKnowledgeContent().contains(key)) {
                result.add(knowledge);
                continue;
            }
            List<Label> labels = getKnowledgeLabel(knowledge);
            if (labels != null && labels.size() != 0) {
                if (labels.get(0).getLabel().contains(key)) {
                    result.add(knowledge);
                }
            }
        }
        return result;
    }

    /**
     * 更新每日任务数量
     */
    public int setUserWordGoal(String username, String numString) {
        Integer num;
        try {
            num = Integer.parseInt(numString);
        } catch (NumberFormatException e) {
            return 0;
        }
        if(num <= 0 || num > 200) {
            return 2;
        }
        User user = getUserByName(username);
        user.setWordTaskNum(num);
        daoSession.update(user);
        return 1;
    }

    public int setUserKnowledgeGoal(String username, String numString) {
        Integer num;
        try {
            num = Integer.parseInt(numString);
        } catch (NumberFormatException e) {
            return 0;
        }
        if(num <= 0 || num > 200) {
            return 2;
        }
        User user = getUserByName(username);
        user.setKnowledgeTaskNum(num);
        daoSession.update(user);
        return 1;
    }

    /**
     * User 之间多对多关系 (好友关系)
     */
    private FriendShip getFriendShip(String user1Name, String user2Name) {
        User user1 = getUserByName(user1Name);
        User user2 = getUserByName(user2Name);
        List<FriendShip> user1_user2_list = daoSession.queryBuilder(FriendShip.class).where(
                FriendShipDao.Properties.User1Id.eq(user1.getId()),
                FriendShipDao.Properties.User2Id.eq(user2.getId())
        ).list();
        if (user1_user2_list != null && (!user1_user2_list.isEmpty())) {
            return user1_user2_list.get(0);
        }
        List<FriendShip> user2_user1_list = daoSession.queryBuilder(FriendShip.class).where(
                FriendShipDao.Properties.User1Id.eq(user2.getId()),
                FriendShipDao.Properties.User2Id.eq(user1.getId())
        ).list();
        if (user2_user1_list != null && (!user2_user1_list.isEmpty())) {
            return user2_user1_list.get(0);
        }
        return null;
    }

    // 判断 user1 和 user2 是不是朋友
    // 注：如果申请了但是还没通过也返回 false
    public boolean isFriends(String user1Name, String user2Name) {
        FriendShip friendShip = getFriendShip(user1Name, user2Name);
        return friendShip != null && friendShip.getStatus() == 1;
    }

    // 返回 user 所有的朋友 (需要已经确认的，即 status = 1)
    public List<User> getAllFriends(String username) {
        List<User> friends = new ArrayList<>();
        User user = getUserByName(username);
        if (user != null) {
            List<FriendShip> friendShips1 = daoSession.queryBuilder(FriendShip.class).where(
                    FriendShipDao.Properties.User1Id.eq(user.getId()),
                    FriendShipDao.Properties.Status.eq(1)
            ).list();
            for (FriendShip friendShip : friendShips1) {
                friends.add(getUserById(friendShip.getUser2Id()));
            }
            List<FriendShip> friendShips2 = daoSession.queryBuilder(FriendShip.class).where(
                    FriendShipDao.Properties.User2Id.eq(user.getId()),
                    FriendShipDao.Properties.Status.eq(1)
            ).list();
            for (FriendShip friendShip : friendShips2) {
                friends.add(getUserById(friendShip.getUser1Id()));
            }
        }
        return friends;
    }

    // 返回所有用户
    public List<User> getAllUsers() {
        return daoSession.loadAll(User.class);
    }

    // 返回名字里含有 subString 子串的所有用户
    public List<User> getUsersNameContains(String subString) {
        List<User> allUsers = getAllUsers();
        return allUsers.stream().filter(
                user -> user.getName().contains(subString)
        ).collect(Collectors.toList());
    }

    // 返回 user 收到的所有好友申请中的发送者 (未确认的，即 status = 0)
    public List<User> getAllRequestReceived(String username) {
        List<User> friends = new ArrayList<>();
        User user = getUserByName(username);
        if (user != null) {
            List<FriendShip> friendShips2 = daoSession.queryBuilder(FriendShip.class).where(
                    FriendShipDao.Properties.User2Id.eq(user.getId()),
                    FriendShipDao.Properties.Status.eq(0)
            ).list();
            for (FriendShip friendShip : friendShips2) {
                friends.add(getUserById(friendShip.getUser1Id()));
            }
        }
        return friends;
    }

    // 返回 user 发出的所有好友申请中的接收者 (未确认的，即 status = 0)
    public List<User> getAllRequestSent(String username) {
        List<User> friends = new ArrayList<>();
        User user = getUserByName(username);
        if (user != null) {
            List<FriendShip> friendShips1 = daoSession.queryBuilder(FriendShip.class).where(
                    FriendShipDao.Properties.User1Id.eq(user.getId()),
                    FriendShipDao.Properties.Status.eq(0)
            ).list();
            for (FriendShip friendShip : friendShips1) {
                friends.add(getUserById(friendShip.getUser2Id()));
            }
        }
        return friends;
    }

    // sender 向 receiver 发出好友邀请，在 Friendship 中添加关系，此时 status = 0 (申请中)
    public void applyForAddingFriend(String senderName, String receiverName) {
        User sender = getUserByName(senderName);
        User receiver = getUserByName(receiverName);
        if (sender != null && receiver != null) {
            FriendShip friendShip = new FriendShip();
            friendShip.setUser1Id(sender.getId());
            friendShip.setUser2Id(receiver.getId());
//            friendShip.setStatus(0);
            // FIXME: 没时间了，这里设置只要申请添加后就是好友了
            friendShip.setStatus(1);
            daoSession.insert(friendShip);
        }
    }

    // receiver 通过 sender 的申请，将 status 改为 1
    public void acceptFriendApplication(String receiverName, String senderName) {
        User sender = getUserByName(senderName);
        User receiver = getUserByName(receiverName);
        if (sender != null && receiver != null) {
            List<FriendShip> ret = daoSession.queryBuilder(FriendShip.class).where(
                    FriendShipDao.Properties.User1Id.eq(sender.getId()),
                    FriendShipDao.Properties.User2Id.eq(receiver.getId())
            ).list();
            if (ret != null && !ret.isEmpty()) {
                FriendShip friendShip = ret.get(0);
                friendShip.setStatus(1);
                daoSession.update(friendShip);
            }
        }
    }

    // user1 和 user2 绝交，这里不缺分 sender 和 receiver
    public void deleteFriendShip(String user1Name, String user2Name) {
        User user1 = getUserByName(user1Name);
        User user2 = getUserByName(user2Name);
        if (user1 != null && user2 != null) {
            QueryBuilder<FriendShip> qb = daoSession.queryBuilder(FriendShip.class);
            List<FriendShip> ret = qb.whereOr(
                    qb.and(FriendShipDao.Properties.User1Id.eq(user1.getId()),
                            FriendShipDao.Properties.User2Id.eq(user2.getId())),
                    qb.and(FriendShipDao.Properties.User1Id.eq(user2.getId()),
                            FriendShipDao.Properties.User2Id.eq(user1.getId()))
            ).list();
            if (ret != null && !ret.isEmpty()) {
                for (FriendShip friendShip : ret) {
                    daoSession.delete(friendShip);
                }
            }
        }
    }


    /* --------------------------------- 私有方法 ------------------------------------ */

    private Date currentDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 将 byte[] 转换为 Bitmap 类型；
     * 返回值 bitmap 使用方法如下：
     * ImageView image = new ImageView(context);
     * image.setImageBitmap(bitmap);
     */
    private static Bitmap byteArrayToBitmap(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;

    }

    /**
     * 将 Bitmap 转换为 byte[] 类型；
     * 返回类型 byte[] 才能存入数据库
     */
    private static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    /**
     * 测试方法：添加相关数据
     */
    public void testWord() {

        User user = getUserByName("123");
        for (int i = 0; i < 10; i++) {
            Word word = new Word();
            word.setUserId(user.getId());
            word.setWordEnglish("apple" + i);
            word.setWordChinese("n.苹果" + i);
            word.setWordVague(0);
            word.setWordVague(0);
            word.setWordDate(new Date());
            user.getWordList().add(word);
            daoSession.insert(word);
            daoSession.update(user);
        }
    }

    public void testKnowledge() {
        User user = getUserByName("123");
        for (int i = 0; i < 10; i++) {
            insertKnowledge("123",
                    "知识点测试，内容为，测试多个空测试：" + i,
                    "测试" + i,
                    null);
            Knowledge knowledge = getKnowledgeById((long) (i + 1));
            insertKnowledgeBlank(knowledge, "测试");
            insertKnowledgeBlank(knowledge, "内容");
        }
    }

}


