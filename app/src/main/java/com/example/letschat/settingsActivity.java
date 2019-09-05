package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class settingsActivity extends AppCompatActivity {



    ////data base referance
    private DatabaseReference mUserDatabase;
    private FirebaseUser current_user;

    ///user information
    private CircleImageView userImage;
    private TextView userName;
    private TextView userStatus;

    /////
    private Button changeStatusButton;
    private Button changeImageButton;

    private static final int GALLERY_PICK=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        changeStatusButton=(Button)findViewById(R.id.setting_change_status_button);
        changeImageButton =(Button)findViewById(R.id.setting_change_image_button);


        userImage =(CircleImageView) findViewById(R.id.user_display_image);
        userName =(TextView) findViewById(R.id.setting_display_name);
        userStatus= (TextView) findViewById(R.id.setting_status);



        ///uid of current user
       current_user= FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUid=current_user.getUid();

        ///getting the data from the database
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name =dataSnapshot.child("user_name").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumbnail =dataSnapshot.child("thumb_image").getValue().toString();

                userName.setText(name);
                userStatus.setText(status);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { ///to handle error


            }



        });

        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent statusIntent =new Intent(settingsActivity.this,StatusActivity.class);
                startActivity(statusIntent);


            }




        });

        changeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent =new Intent();
                galleryIntent.setType("images");

                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);


            }

        });

    }

}
