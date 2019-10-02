package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class FileSharingChatTest extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mTextMessage;
    private Button mSendBtn;
    private Button mShareFilesBtn;
    private String mFileType="",myUrl="";
    private StorageTask uploadTask;

    private String uniqueKey;



    //data base reference

    private DatabaseReference mCurrentUserChat;
    private FirebaseUser mCurrentUser;

    //firebase storage reference
    private StorageReference mStorageReference;


    private Uri mFileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sharing_chat_test);


        mSendBtn=(Button)findViewById(R.id.chat_send_btn);
        mShareFilesBtn=(Button)findViewById(R.id.chat_share_btn);
        mTextMessage=(EditText)findViewById(R.id.chat_message);



        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserChat = FirebaseDatabase.getInstance().getReference().child("chats_image").child(mCurrentUser.getUid());    //chat directory at same level as user
        mStorageReference=FirebaseStorage.getInstance().getReference();        //root directory of storage




        mToolbar = (Toolbar)findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat Page");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mShareFilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //Toast.makeText(FileSharingChatTest.this, "hello", Toast.LENGTH_SHORT).show();

                CharSequence options[]=new CharSequence[]
                        {
                                "Images",
                                "PDF Files",
                                "MS Word Files"

                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(FileSharingChatTest.this);
                builder.setTitle("Select Files");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position)
                    {
                        if(position==0)
                        {
                            mFileType="image";
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select Image"),438);
                        }

                        if(position==1)
                        {
                            mFileType="pdf";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Select Pdf File"),438);

                        }

                        if(position==2)
                        {
                            mFileType="docx";

                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/msword");
                            startActivityForResult(intent.createChooser(intent,"Select Word File"),438);

                        }
                    }
                });

                builder.show();

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            mFileUri = data.getData();  //url of selected image


            if(!mFileType.equals("image"))
            {
                // if file is not image
            }
            else if(mFileType.equals("image"))
            {


                //path of file where image will be stored hence reference

                uniqueKey=null;
                uniqueKey=mCurrentUserChat.push().toString();

                final  StorageReference filePath = mStorageReference.child("chats_image").child(uniqueKey+".jpg");



                filePath.putFile(mFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri)
                                {
                                    String downloadUrl = uri.toString();

                                    Map  msgHashMap = new HashMap<>();
                                    msgHashMap.put("url_img",downloadUrl);
                                    msgHashMap.put("msg_type","image");

                                    mCurrentUserChat.push().setValue(msgHashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(FileSharingChatTest.this, "send " +
                                                    "image", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                            Toast.makeText(FileSharingChatTest.this,"There is an error "+e.toString()+
                                                    " ",Toast.LENGTH_LONG).show();
                                        }
                                    });


                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(FileSharingChatTest.this, "Error sending " +
                                    "image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
            else
            {
                Toast.makeText(FileSharingChatTest.this,"Error ",Toast.LENGTH_SHORT).show();
            }
        }

    }
}


