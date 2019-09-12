package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.telecom.Call;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class UserProfilePage extends AppCompatActivity {

    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private ImageView mProfileImage;
    private Button mProfileSendRequestBtn;

    private DatabaseReference mUserDataBase;




    private DatabaseReference mFriendReqDataBase;   //for friend_requests directory
    private DatabaseReference mFriendDatabase;      //for friendData directory

    private FirebaseUser mCurrentUser;

    //private ProgressDialog mProgressDialog;

    private String mCurrentFriendshipState;




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
        //Toast.makeText(UserProfilePage.this,"Please wait while we load the data !",Toast.LENGTH_SHORT).show();

        String uidOfClickedPerson = getIntent().getStringExtra("userId");





        mUserDataBase = FirebaseDatabase.getInstance().getReference().child("users").child(uidOfClickedPerson);
        mUserDataBase.keepSynced(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();


        //New temporary FriendRequest Directory at the same level as user's directory
        mFriendReqDataBase = FirebaseDatabase.getInstance().getReference().child("friend_requests");

        //New permanent FriendData Directory at the same level as user's directory
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("friends");



        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileName =(TextView)findViewById(R.id.profile_displayName);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount=(TextView)findViewById(R.id.profile_totalFriends);
        mProfileSendRequestBtn =(Button)findViewById(R.id.profile_send_req_btn);


        mCurrentFriendshipState= "Not Friends";



        loadUserData(uidOfClickedPerson);
        sendFriendRequest(uidOfClickedPerson);






    }







    private void sendFriendRequest(final String uidOfClickedPerson) {


        mProfileSendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                mProfileSendRequestBtn.setEnabled(false);   //once button is tapped user should not be able to again press the btn





                // ---------------------------    NOT FRIENDS STATE / SEND FRIEND REQUEST    ---------------------------//
                if(mCurrentFriendshipState.equals("Not Friends"))
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



                                        mCurrentFriendshipState = "req_sent";
                                        mProfileSendRequestBtn.setText("Cancel Friend Request");


                                        Toast.makeText(UserProfilePage.this,"Request Sent!",Toast.LENGTH_SHORT).show();

                                    }
                                });




                            }else {

                                Toast.makeText(UserProfilePage.this,"Failed Sending Request, "
                                        +"Please try again later !",Toast.LENGTH_SHORT).show();
                            }

                            mProfileSendRequestBtn.setEnabled(true);

                        }
                    });

                }//End of if




             //   ---------------------------    CANCEL FRIEND REQUEST STATE  ----------------------------//
                if(mCurrentFriendshipState.equals("req_sent"))
                {
                    mFriendReqDataBase.child(mCurrentUser.getUid()).child(uidOfClickedPerson)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {

                                mFriendReqDataBase.child(uidOfClickedPerson).child(mCurrentUser.getUid())
                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>()
                                {

                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {

                                        mProfileSendRequestBtn.setEnabled(true);
                                        mCurrentFriendshipState = "Not Friends";
                                        mProfileSendRequestBtn.setText("Send Friend Request");
                                    }
                                });

                            }else{

                                Toast.makeText(UserProfilePage.this,"Failed Cancelling Request, "
                                        +"Please try again later !",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } //if end









                // ----------------------- ACCEPT FRIEND REQUEST STATE  ---------------------------//

                // -----------------------  START OF FRIENDS DATA STRUCTURE ----------------------- //

                if(mCurrentFriendshipState.equals("req_received"))
                {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mFriendDatabase.child(mCurrentUser.getUid()).child(uidOfClickedPerson)
                            .setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            //i.e friend directory is created successfully then ..
                            if(task.isSuccessful())
                            {

                                mFriendDatabase.child(uidOfClickedPerson).child(mCurrentUser.getUid())
                                        .setValue(currentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {


                                            //.. then remove friendRequest directory


                                            // ----------------- SAME FUNCTION AS ABOVE CANCEL FRIEND REQUEST --------- //
                                            mFriendReqDataBase.child(mCurrentUser.getUid()).child(uidOfClickedPerson)
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {

                                                        mFriendReqDataBase.child(uidOfClickedPerson).child(mCurrentUser.getUid())
                                                                .removeValue().addOnSuccessListener(new OnSuccessListener<Void>()
                                                        {

                                                            @Override
                                                            public void onSuccess(Void aVoid)
                                                            {

                                                                mProfileSendRequestBtn.setEnabled(true);
                                                                mCurrentFriendshipState = "friends";
                                                                mProfileSendRequestBtn.setText("UNFRIEND");
                                                            }
                                                        });

                                                    }else{

                                                        Toast.makeText(UserProfilePage.this,"Failed Deleting Request "
                                                                +" !",Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                            // ----------------- SAME FUNCTION AS ABOVE CANCEL FRIEND REQUEST --------- //
                                        }

                                    }
                                });


                            }else{
                                //ERROR HANDLING IF REQUEST NOT ACCEPTED OR FRIEND DIRECTORY NOT CREATED
                            }

                        }
                    });
                }   //End of accept friend request






                // -------------------  UNFRIEND IF FRIEND ----------------------- //
                if(mCurrentFriendshipState.equals("friends"))
                {

                    mFriendDatabase.child(mCurrentUser.getUid()).child(uidOfClickedPerson)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            if(task.isSuccessful())
                            {

                                mFriendDatabase.child(uidOfClickedPerson).child(mCurrentUser.getUid())
                                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>()
                                {

                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {

                                        mProfileSendRequestBtn.setEnabled(true);
                                        mCurrentFriendshipState = "Not Friends";
                                        mProfileSendRequestBtn.setText("Send Friend Request");
                                    }
                                });

                            }else{

                                Toast.makeText(UserProfilePage.this,"Unfriend Operation Failed "
                                        +" !",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }//End of Unfriend





            }
        });


    }


    private void loadUserData(final String uidOfClickedPerson){

        mUserDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String displayName = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(displayName);
                mProfileStatus.setText(status);


                Picasso.with(UserProfilePage.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.images).into(mProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        //Do Nothing
                    }

                    @Override
                    public void onError() {

                        Picasso.with(UserProfilePage.this).load(image).placeholder(R.drawable.images).into(mProfileImage);

                    }
                });






                //-----------------  FRIEND LIST / REQUEST FEATURE /UPDATING REQUEST BUTTON VALUE  ---------------- //


                /*addListenerForSingleValueEvent works only for one time and for
                one Single Object
                 */
                mFriendReqDataBase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

                    //datasnapshot works until current userid

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild(uidOfClickedPerson))
                        {
                            String reqType = dataSnapshot.child(uidOfClickedPerson).child("request_type").getValue().toString();

                            if(reqType.equals("received")) //i.e the clickedPerson has sent us (current User) request
                            {


                                mCurrentFriendshipState = "req_received";
                                mProfileSendRequestBtn.setText("Accept Friend Request");

                            }
                            else if(reqType.equals("sent"))
                            {
                                mCurrentFriendshipState ="req_sent";
                                mProfileSendRequestBtn.setText("Cancel Friend Request");

                            }
                        }   //Check in friends directory if current user and tapped person are friend or not
                            //If friend then button should show unfriend
                        else{

                            mFriendDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.hasChild(uidOfClickedPerson))
                                    {
                                        mCurrentFriendshipState ="friends";
                                        mProfileSendRequestBtn.setText("Unfriend");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Error handling for updating value of button (only for unfriend after accepting request)
                                }
                            });

                        }



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        // Error handling for updating value of button (i.e ACCEPT , UNFRIEND and all)
                    }
                });
            }


                                 //Error Handling
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(UserProfilePage.this,"There is some problem with server error loading data"
                        + ", Please try again !",Toast.LENGTH_SHORT).show();


            }
        });



    }       //End od Load User Data Function

}
