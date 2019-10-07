package com.example.letschat;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUserActivity extends AppCompatActivity {
    private Toolbar mToolBar;


    private DatabaseReference mDatabaseReferance;
    FirebaseUser current_user;

    MyAdapter adapter;

    RecyclerView recyclerView;
    ArrayList<UserProfile> userList;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_user);
        Log.i("info","this is on creat");



        mToolBar=(Toolbar)findViewById(R.id.all_user_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        recyclerView=(RecyclerView)findViewById(R.id.allUserRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllUserActivity.this));


        current_user= FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserUid=current_user.getUid();



        mDatabaseReferance = FirebaseDatabase.getInstance().getReference().child("users");


        mDatabaseReferance.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList =new ArrayList<UserProfile>();
                for(DataSnapshot d:dataSnapshot.getChildren()) {
                    if(!d.getKey().equals(currentUserUid)) {
                        UserProfile p = d.getValue(UserProfile.class);
                        userList.add(p);
                    }
                }
                adapter = new MyAdapter(AllUserActivity.this,userList);
                recyclerView.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllUserActivity.this,"something is wrong",Toast.LENGTH_LONG).show();

            }
        });




    }


    @Override
    protected void onStart() {
        super.onStart();
        
        mDatabaseReferance.child(current_user.getUid()).child("online").setValue(0);



    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabaseReferance.child(current_user.getUid()).child("online").setValue(ServerValue.TIMESTAMP);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter=null;

    }



}