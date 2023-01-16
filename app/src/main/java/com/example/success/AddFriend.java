package com.example.success;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.success.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddFriend extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DatabaseInterface db = MainActivity.db;
        User currentUser = CurrentUser.getUser();

        super.onCreate(savedInstanceState);
        requestWindowFeature((Window.FEATURE_NO_TITLE));
        setContentView(R.layout.activity_add_friend);
        EditText search = (EditText) findViewById(R.id.search_friend);
        search.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 这两个条件必须同时成立，如果仅仅用了enter判断，就会执行两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(AddFriend.this);
                    String friendName = search.getText().toString();
                    System.out.println(friendName);
                    if (friendName.equals("")) {
                        dialog.setMessage("您还没有输入好友名称哦");
                        dialog.show();
                    } else {
                        List<String> userNames = db.getAllUsers().stream().
                                map(User::getName).collect(Collectors.toList());
                        if (friendName.equals(currentUser.getName())) {
                            dialog.setMessage("啊哦，您不可以加自己为好友哦");
                            dialog.show();
                        } else if (!userNames.contains(friendName)) {
                            dialog.setMessage("啊哦，该用户不存在呢");
                            dialog.show();
                        } else if (db.isFriends(currentUser.getName(), friendName)) {
                            // 新加的，如果两人已经是朋友，返回提示信息
                            dialog.setMessage("你和" + friendName + "早就是好友啦！");
                            dialog.show();
                        } else {
                            dialog.setMessage("确认添加<" + friendName + ">为好友？");
                            dialog.setIcon(R.mipmap.ic_launcher);
                            dialog.setCancelable(false);            //点击对话框以外的区域是否让对话框消失

                            //设置正面按钮
                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 添加好友
                                    Toast.makeText(AddFriend.this, "已成功发送好友申请，等待对方通过",
                                            Toast.LENGTH_LONG).show();
                                    db.applyForAddingFriend(currentUser.getName(), friendName);
                                    dialog.dismiss();
                                    search.setText("");
                                }
                            });
                            //设置反面按钮
                            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(AddFriend.this, "你点击了取消", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    }
                }
                return false;
            }
        });

    }
}