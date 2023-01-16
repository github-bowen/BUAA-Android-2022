package com.example.success;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Home extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature((Window.FEATURE_NO_TITLE));
        setContentView(R.layout.activity_home);
        Button signIn = (Button) findViewById(R.id.signInButton);
        signIn.getBackground().setAlpha(65);
        Button signUp = (Button) findViewById(R.id.signUpButton);
        signUp.getBackground().setAlpha(65);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startSignIn = new Intent(Home.this,Sign_In.class);
                startActivity(startSignIn);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startSignUp = new Intent(Home.this,Sign_up.class);
                startActivity(startSignUp);
            }
        });
    }

}
