package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class ShowImage extends AppCompatActivity {

    private Toolbar mToolBar;
    private ImageView chatImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        mToolBar=(Toolbar)findViewById(R.id.image_layout_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Image");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chatImage = (ImageView)findViewById(R.id.chat_image);

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("imageUrl");

        Picasso.with(ShowImage.this).load(message)
                .placeholder(R.drawable.images).into(chatImage);

    }
}
