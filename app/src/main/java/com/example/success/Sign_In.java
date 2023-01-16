package com.example.success;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sign_In extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseInterface db = new DatabaseInterface(this);
        setContentView(R.layout.activity_sign_in);
        Button sureButton = (Button) findViewById(R.id.sureButton_signIn);
        sureButton.getBackground().setAlpha(65);
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name_text = (EditText) findViewById(R.id.input_name_signIn);
                EditText passwd_text = (EditText)  findViewById(R.id.input_passwd_signIn);
                EditText sure_text = (EditText) findViewById(R.id.input_sure_passwd);
                String name = name_text.getText().toString();
                String passwd = passwd_text.getText().toString();
                String surePasswd = sure_text.getText().toString();
                Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$");
                Matcher matcher = pattern.matcher(passwd);
                System.out.println(matcher.find(0));
                if(!matcher.find(0)){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Sign_In.this);
                    dialog.setMessage("密码不合要求，请检查");
                    dialog.show();
//                    Toast.makeText(Sign_In.this, "密码不合要求，请检查", Toast.LENGTH_LONG).show();
                }
                else if(!passwd.equals(surePasswd)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Sign_In.this);
                    dialog.setMessage("两次密码不一致，请检查");
                    dialog.show();
//                    Toast.makeText(Sign_In.this, "两次密码不一致，请检查", Toast.LENGTH_LONG).show();
                } else {
                // 添加用户，并返回是否成功
                    int ret = db.insertUser(name, passwd, 0);
                    if (ret == 1) {
                        CurrentUser.logInUser(db.getUserByName(name));
                        Toast.makeText(Sign_In.this, "注册成功", Toast.LENGTH_LONG).show();
                        // 注册成功后跳转页面到主页
                        Intent loginSuccess = new Intent(Sign_In.this,MainActivity.class);
                        loginSuccess.putExtra("name", name);
                        loginSuccess.putExtra("passwd", passwd);
                        startActivity(loginSuccess);
                        finish();
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(Sign_In.this);
                        dialog.setMessage("已有同名用户，注册失败");
                        dialog.show();
                       // Toast.makeText(Sign_In.this, "已有同名用户，注册失败", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
