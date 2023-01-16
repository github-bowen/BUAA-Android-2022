package com.example.success;

import static android.content.ContentValues.TAG;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Sign_up extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseInterface db = new DatabaseInterface(this);
        setContentView(R.layout.activity_sign_up);
        Intent intent = new Intent(Sign_up.this, MainActivity.class);
        Button sureButton = (Button) findViewById(R.id.sureButton);
        sureButton.getBackground().setAlpha(65);
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name_text = (EditText) findViewById(R.id.input_name);
                EditText passwd_text = (EditText) findViewById(R.id.input_passwd);
                String name = name_text.getText().toString();
                String passwd = passwd_text.getText().toString();
                intent.putExtra("name", name);
                intent.putExtra("passwd", passwd);
                // 访问数据库，确认name与passwd有效
                int ret = db.userSignIn(name, passwd);
                if (ret == 1) {
                    startActivity(intent);
                    finish();
                    CurrentUser.logInUser(db.getUserByName(name));
                    Toast.makeText(Sign_up.this, "登录成功", Toast.LENGTH_SHORT).show();
                } else if (ret == 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Sign_up.this);
                    dialog.setMessage("用户名不存在,请先注册");
                    dialog.show();
                    //Toast.makeText(Sign_up.this, "用户名不存在,请先注册", Toast.LENGTH_LONG).show();
                } else if (ret == 2) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Sign_up.this);
                    dialog.setMessage("密码错误");
                    dialog.show();
                    //Toast.makeText(Sign_up.this, "密码错误", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}