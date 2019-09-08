package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {



    private Toolbar mToolbar;

    private TextView mStatus;
    private Button saveChangesButton;

    ///progress dialog
    private ProgressDialog mProgressDialog;



    ////
    private DatabaseReference mUserDataBaseReferance;
    private FirebaseUser current_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);


        //Previous status
        final String statusValue = getIntent().getStringExtra("status");

        ///uid of current user
        current_user= FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUid=current_user.getUid();
        mUserDataBaseReferance= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserUid);


        ///appbar
        mToolbar = (Toolbar) findViewById(R.id.statuAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("status setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        mStatus=(TextView)findViewById(R.id.user_status);
        mStatus.setText(statusValue);

        saveChangesButton=(Button)findViewById(R.id.status_save_button);


        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                mProgressDialog=new ProgressDialog(StatusActivity.this);
                //Progress Bar
                mProgressDialog.setTitle("Saving Changes");
                mProgressDialog.setMessage("Please wait while we save the changes");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                String status =mStatus.getText().toString();
                mUserDataBaseReferance.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgressDialog.dismiss();

                            Intent settingsIntent = new Intent(StatusActivity.this,settingsActivity.class);
                            startActivity(settingsIntent);

                        }
                        else{
                            Toast.makeText(StatusActivity.this, "Cannot save changes", Toast.LENGTH_LONG).show();
                        }

                    }
                });



            }
        });


    }
}
