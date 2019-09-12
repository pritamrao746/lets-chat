package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

public class UserProfilePage extends AppCompatActivity {

    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private ImageView mProfileImage;
    private Button mProfileSendRequestBtn;

    private DatabaseReference mUserDataBase;
    private DatabaseReference mFriendReqDataBase;

    private FirebaseUser mCurrentUser;

    //private ProgressDialog mProgressDialog;

    private String mCurrentFrienshipState;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);

        /*
                 Wasn't Working !! Don't know why ??

        mProgressDialog = new ProgressDialog(UserProfilePage.this);
        mProgressDialog.setTitle("Loading User's Data");
        mProgressDialog.setMessage("Please wait while we load the user data !");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


         */
        Toast.makeText(UserProfilePage.this,"Please wait while we load the data !",Toast.LENGTH_SHORT).show();

        String uidOfClickedPerson = getIntent().getStringExtra("userId");



        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uidOfClickedPerson);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //New Directory at the same level as user's directory
        mFriendReqDataBase = FirebaseDatabase.getInstance().getReference().child("friend_requests");



        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName =(TextView)findViewById(R.id.profile_displayName);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount=(TextView)findViewById(R.id.profile_totalFriends);
        mProfileSendRequestBtn =(Button)findViewById(R.id.profile_send_req_btn);


        mCurrentFrienshipState= "Not Friends";



        loadUserData();
        sendFriendRequest(uidOfClickedPerson);




    }

    private void sendFriendRequest(final String uidOfClickedPerson) {


        mProfileSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(mCurrentFrienshipState.equals("Not Friends"))
                {

                    mFriendReqDataBase.child(mCurrentUser.getUid()).child(uidOfClickedPerson).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                mFriendReqDataBase.child(uidOfClickedPerson).child(mCurrentUser.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {

                                        Toast.makeText(UserProfilePage.this,"Request Sent!",Toast.LENGTH_SHORT).show();

                                    }
                                });




                            }else {

                                Toast.makeText(UserProfilePage.this,"Failed Sending Request, "
                                        +"Please try again later !",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }//End of if

            }
        });


    }


    private void loadUserData(){

        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String displayName = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(displayName);
                mProfileStatus.setText(status);

                Picasso.with(UserProfilePage.this).load(image).placeholder(R.drawable.images).into(mProfileImage);

               // mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(UserProfilePage.this,"There is some problem with server"
                        + ", Please try again !",Toast.LENGTH_SHORT).show();
            }
        });

    }



}
