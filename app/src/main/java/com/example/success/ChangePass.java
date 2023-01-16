package com.example.success;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.success.databinding.ActivityChangePassBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePass extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseInterface db = MainActivity.db;
        setContentView(R.layout.activity_change_pass);
        Button sureChange = (Button) findViewById(R.id.sureChange);
        TextView pass = (TextView) findViewById(R.id.editTextTextPassword);
        TextView sure = (TextView) findViewById(R.id.editTextTextPassword2);
        sureChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passStr = pass.getText().toString();
                String sureStr = sure.getText().toString();
                Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,16}$");
                Matcher matcher = pattern.matcher(passStr);
                System.out.println(matcher.find(0));
                System.out.println(passStr);
                if (!matcher.find(0)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePass.this);
                    dialog.setMessage("密码不合要求，请检查");
                    dialog.show();
                } else if (!passStr.equals(sureStr)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePass.this);
                    dialog.setMessage("两次密码不一致，请检查");
                    dialog.show();
                } else {
                    Intent intent = getIntent();
                    Bundle bundle = intent.getExtras();
                    String userName = bundle.getString("userName");
                    // 修改密码
                    // 注：当前实现的不需要输入旧密码，如还要加上验证旧密码，需要使用：
                    // db.changedPassword(username, oldPassword, newPassword)
                    db.changedPasswordWithoutOld(userName, passStr);

                    Toast.makeText(ChangePass.this,"密码已修改",Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }
}