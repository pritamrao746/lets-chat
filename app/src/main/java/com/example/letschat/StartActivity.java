package com.example.letschat;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {



    //Opens Register Page
    public void newAccount(View view){

        Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
        startActivity(reg_intent);

    }

    //Opens Login Page
    public void loginPage(View view) {
        Intent login_intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(login_intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



    }
}
