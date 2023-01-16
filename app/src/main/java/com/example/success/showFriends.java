package com.example.success;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.success.entity.User;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class showFriends extends Activity {
    private final ArrayList<Friend> friends = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_friends);
        initTask();
        FriendAdapter friendAdapter = new FriendAdapter(showFriends.this,
                R.layout.friend_show, this.friends);
        ListView listView = (ListView) findViewById(R.id.friendList);
        listView.setAdapter(friendAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Friend friend = friends.get(position);
                String friendName = friend.getFriendName();
                AlertDialog.Builder dialog = new AlertDialog.Builder(showFriends.this);
                dialog.setMessage("确认删除好友<" + friendName + ">吗？");
                dialog.setIcon(R.mipmap.ic_launcher);
                dialog.setCancelable(false);            //点击对话框以外的区域是否让对话框消失

                //设置正面按钮
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 删除好友
                        DatabaseInterface db = MainActivity.db;
                        User currentUser = CurrentUser.getUser();
                        db.deleteFriendShip(currentUser.getName(), friendName);

                        Intent callNewPage = new Intent(showFriends.this, showFriends.class);
                        startActivity(callNewPage);
                        finish();
                    }
                });
                //设置反面按钮
                dialog.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(showFriends.this, "你点击了取消", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    public void initTask() {
        // 取该用户的好有赋到friends数组里
        // 要求含有头像信息，名称，当日已背数目
        // 待测试byte[]是否可显示
//        Friend kl = new Friend(R.drawable.kl,"Klily",12);
//        Friend lbh = new Friend(R.drawable.lbh,"LBH",13);
//        Friend wgq = new Friend(R.drawable.wgq,"WGQ",14);
//        Friend pxt = new Friend(R.drawable.pxt,"PXT",15);
//        friends.add(kl);
//        friends.add(lbh);
//        friends.add(wgq);
//        friends.add(pxt);
        DatabaseInterface db = MainActivity.db;
        User currentUser = CurrentUser.getUser();
        // 当日已背数目
        final int wordAndKnowledgeTodayNum = db.countUserTodayKnowledge(currentUser.getName()) +
                db.countUserTodayWord(currentUser.getName());

        friends.clear();

        friends.addAll(db.getAllFriends(currentUser.getName()).stream().map(
                user -> new Friend(user.getUserPhoto(), user.getName(), db.countUserTodayKnowledge(user.getName()) +
                        db.countUserTodayWord(user.getName()))
        ).collect(Collectors.toList()));

    }
}
