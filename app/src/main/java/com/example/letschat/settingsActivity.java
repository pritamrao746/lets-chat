package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;


public class settingsActivity extends AppCompatActivity {



    ////data base referance
    private DatabaseReference mUserDatabase;
    private FirebaseUser current_user;

    private StorageReference mStorageRef;



    ///user information
    private CircleImageView userImage;
    private TextView userName;
    private TextView userStatus;
    private Toolbar mToolBar;
    private ProgressDialog mProgressDialog;
    /////
    private Button changeStatusButton;
    private Button changeImageButton;



    private static final int GALLERY_PICK=1;    ////to use gallery intent to pick the image



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        changeStatusButton=(Button)findViewById(R.id.setting_change_status_button);
        changeImageButton =(Button)findViewById(R.id.setting_change_image_button);


        userImage =(CircleImageView) findViewById(R.id.user_display_image);
        userName =(TextView) findViewById(R.id.setting_display_name);
        userStatus= (TextView) findViewById(R.id.setting_status);




        mToolBar = (Toolbar) findViewById(R.id.settingAppBar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        ///uid of current user
        current_user= FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUid=current_user.getUid();

        ///getting the data from the database
        mStorageRef = FirebaseStorage.getInstance().getReference();
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


                Picasso.with(settingsActivity.this).load(image).into(userImage);


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
                /*
                Intent galleryIntent =new Intent();    ///using gallery intent
                galleryIntent.setType("images/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);
                   */


                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(settingsActivity.this);


            }

        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog =new ProgressDialog(settingsActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("wait , while we uploade your image ");
                mProgressDialog.show();



                final Uri imageUri = result.getUri();

                final StorageReference filePath =mStorageRef.child("profile_image").child(current_user.getUid()+".jpeg");
                filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUri=uri.toString();
                                    mUserDatabase.child("image").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                mProgressDialog.dismiss();
                                                Toast.makeText(settingsActivity.this,"image updated",Toast.LENGTH_LONG);
                                            }
                                        }
                                    });

                                }
                            });
                        }else{
                            Toast.makeText(settingsActivity.this,"can't store the image",Toast.LENGTH_LONG);
                            mProgressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}