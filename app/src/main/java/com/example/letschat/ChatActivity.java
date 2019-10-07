package com.example.letschat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String mChatUser;


    private androidx.appcompat.widget.Toolbar mChatToolBar;
    DatabaseReference mRootreff;
    DatabaseReference mChatRef;
    DatabaseReference mChatUserRef;
    DatabaseReference mChatIdRef;
    StorageReference mStoragRef;

    private String mCurrentUser;



    private ImageButton mChatAddButton;
    private ImageButton mChatSendButton;
    private EditText mchatMessage;

    private RecyclerView mMessageList;
    private SwipeRefreshLayout mRefreshLayout;
    private MessageAdapter messageAdapter;
    private List<Message> mConversationList =new ArrayList<Message>();


    String mLastMessageKey="";

    @Override
    protected void onPause() {
        super.onPause();
        mRootreff.child("users").child(mCurrentUser).child("online").setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onStart() {


        super.onStart();
        mRootreff.child("users").child(mCurrentUser).child("online").setValue(0);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatUser=getIntent().getStringExtra("userId");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRootreff= FirebaseDatabase.getInstance().getReference();
        mStoragRef= FirebaseStorage.getInstance().getReference();



        mChatToolBar=(Toolbar) findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolBar);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view =inflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        mChatToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {      ////sending to the userProfile
                Intent profileIntent = new Intent(ChatActivity.this, UserProfilePage.class);
                profileIntent.putExtra("userId", mChatUser);
                startActivity(profileIntent);
            }
        });







        mRefreshLayout=findViewById(R.id.chat_swipe_refresh_layout);
        mChatAddButton = findViewById(R.id.chat_add_button);
        mChatSendButton= findViewById(R.id.chat_send_button);
        mchatMessage=findViewById(R.id.chat_message_edit_text);
        mMessageList =findViewById(R.id.chat_mesasage_recycler_view);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter=new MessageAdapter(mConversationList);
        mMessageList.setAdapter(messageAdapter);




        mChatUserRef =mRootreff.child("users").child(mChatUser);
        mChatIdRef = mRootreff.child("chats").child(mCurrentUser);


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMoreMessages();
                mRefreshLayout.setRefreshing(false);
            }
        });




        /////filling the toolbar
        mChatUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextView mDispalyName =findViewById(R.id.chat_display_name);
                TextView mLastSeen=findViewById(R.id.chat_last_seen);
                CircleImageView userImage =findViewById(R.id.chat_display_image);

                mDispalyName.setText(dataSnapshot.child("user_name").getValue().toString());
               // String online = dataSnapshot.child("online").getValue().toString();

                long online =(long)dataSnapshot.child("online").getValue();

                if(online==0){
                    mLastSeen.setText("online");
                }
                else
                    mLastSeen.setText(TimeAgo.getTimeAgo(online));

                Picasso.with(ChatActivity.this).load(dataSnapshot.child("thumb_image").getValue().toString())
                        .placeholder(R.drawable.images).into(userImage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        ////// conversation
        mChatIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mChatId;

                if(!dataSnapshot.hasChild(mChatUser)){
                    mChatId =mRootreff.push().getKey();

                    mChatRef=mRootreff.child("conversation").child(mChatId);


                    Map chatUserMap =new HashMap();
                    chatUserMap.put("chats/"+mCurrentUser+"/"+mChatUser,mChatId);
                    chatUserMap.put("chats/"+mChatUser+"/"+mCurrentUser,mChatId);
                  // chatUserMap.put("conversation/"+mChatId,"does exsist");
                    mRootreff.updateChildren(chatUserMap);
                    Log.i("set",mChatRef.toString());
                    loadMessages();

                }
                else{
                    mChatId=dataSnapshot.child(mChatUser).getValue().toString();
                    mChatRef=mRootreff.child("conversation").child(mChatId);

                    Log.i("wellset",mChatRef.toString());

                    loadMessages();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        mChatAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFiles();
            }
        });

        mChatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message =mchatMessage.getText().toString();
                sendMessage(message,"text");
                mchatMessage.setText("");
            }
        });





    }




    private void addFiles() {

        CharSequence options[]=new CharSequence[]
                {
                        "Images",
                        "PDF Files",
                        "MS Word Files",
                        "Share Location"

                };



        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Select Files");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position)
            {
                if(position==0)
                {     Toast.makeText(ChatActivity.this,"option one selected",Toast.LENGTH_LONG).show();
                   /*

                    Intent galleryIntent =new Intent();    ///using gallery intent
                    galleryIntent.setType("images/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);

                 */
                 //   mFileType="image";
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent,"Select Image"),0);


                }

                if(position==1)
                {
                    Toast.makeText(ChatActivity.this,"option two selected",Toast.LENGTH_LONG).show();


                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent.createChooser(intent,"Select Pdf File"),1);


                }

                if(position==2)
                {
                    Toast.makeText(ChatActivity.this,"option three selected",Toast.LENGTH_LONG).show();

                }

                if(position==3)
                {
                    Toast.makeText(ChatActivity.this,"option four selected",Toast.LENGTH_LONG).show();
                    Intent map = new Intent(ChatActivity.this,MapActivity.class);
                    startActivity(map);

                }
            }
        });

        builder.show();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && data!=null && data.getData()!=null){

            ////it's image
            if(requestCode==0){
                 final Uri imageUri =data.getData();  // Url of the uploaded image
                String uniqueKey =mRootreff.push().getKey();
                final StorageReference filePath =mStoragRef.child("Conversation").child("images").child(uniqueKey+".jpg");
                filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final  String downloadUri = uri.toString();
                                    Log.i("URL",downloadUri);
                                    sendMessage(downloadUri,"image");

                                }
                            });

                        }
                    }
                });





            }

            if(requestCode==1){




                final Uri pdfUri =data.getData();  // Url of the uploaded image
                String uniqueKey =mRootreff.push().getKey();
                final StorageReference filePath =mStoragRef.child("Conversation").child("pdf").child(uniqueKey+".pdf");
                filePath.putFile(pdfUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final  String downloadUri = uri.toString();
                                    Log.i("URL",downloadUri);
                                    sendMessage(downloadUri,"pdf");

                                }
                            });

                        }
                    }
                });



            }
        }
       // final Uri imageUri = result.getUri();  // Url of the uploaded image





    }

    private void loadMoreMessages() {


        Query messageQuery =mChatRef.orderByKey().endAt(mLastMessageKey).limitToLast(5);

        messageQuery.addChildEventListener(new ChildEventListener() {

            int i=0;
            String previous = mLastMessageKey;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(i==0){
                    mLastMessageKey=dataSnapshot.getKey();
                }



                if(!dataSnapshot.getKey().equals(previous)){
                    Message message =dataSnapshot.getValue(Message.class);



                    ///for seen feature 
                    if(!dataSnapshot.child("sender").getValue().toString().equals( mCurrentUser)&&dataSnapshot.child("seen").getValue().toString().equals("false")){     ///sended by someone else
                        Log.i("there",message.getMessage());
                        mChatRef.child(dataSnapshot.getKey()).child("seen").setValue("true");
                    }

                    mConversationList.add(i++,message);
                    messageAdapter.notifyDataSetChanged();


                }






            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void loadMessages() {

           Query messageQuery =mChatRef.limitToLast(5);
           messageQuery.addChildEventListener(new ChildEventListener() {
               int i=0;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message =dataSnapshot.getValue(Message.class);
                if(i==0){
                    mLastMessageKey=dataSnapshot.getKey();
                }
                i++;

                ////for seen feature
                if(!dataSnapshot.child("sender").getValue().toString().equals( mCurrentUser)&&dataSnapshot.child("seen").getValue().toString().equals("false")){     ///sended by someone else

                    mChatRef.child(dataSnapshot.getKey()).child("seen").setValue("true");
                }

                Log.i("there",message.getMessage());
                mConversationList.add(message);
                messageAdapter.notifyDataSetChanged();

                mMessageList.scrollToPosition(mConversationList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    private void sendMessage(String message,String type) {


        if(!TextUtils.isEmpty(message)){
            Map messageMap =new HashMap();
            messageMap.put("message" ,message);
            messageMap.put("type",type);
            messageMap.put("seen","false");
            messageMap.put("time",ServerValue.TIMESTAMP);
            messageMap.put("sender",mCurrentUser);

            String uniqeMessageId =mRootreff.push().getKey();


            mChatRef.child(uniqeMessageId).setValue(messageMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(ChatActivity.this,"can't write in database",Toast.LENGTH_LONG).show();
                }
            });

        }

    }





}
