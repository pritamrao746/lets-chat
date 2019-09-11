package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class UserProfilePage extends AppCompatActivity {

    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);



        textView = (TextView)findViewById(R.id.testText);


        getIncomingIntent();
    }


    private void getIncomingIntent(){



        if(getIntent().hasExtra("userId"))
        {
            Log.d("Info","working");
            String userId = getIntent().getStringExtra("userId");

            setText(userId);
        }
    }


    private void setText(String userId){

        Log.d("test","its working");
        textView.setText(userId);
    }
}
