package com.example.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MapActivity extends AppCompatActivity {


   private Button shareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        shareBtn = (Button)findViewById(R.id.share_loc);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

                Intent shareLocIntent = new Intent(MapActivity.this,GoogleMapsActivity.class);
                startActivity(shareLocIntent);

                }
        });

    }
}
