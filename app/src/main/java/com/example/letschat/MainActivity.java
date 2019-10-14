package com.example.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;  //For Fragments
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;



    private DatabaseReference dataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);








        mAuth = FirebaseAuth.getInstance();

        //For main page toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Lets Chat");


        //Tabs
        mViewPager= (ViewPager)findViewById(R.id.main_tab_pager);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.setAdapter(mSectionsPagerAdapter);



    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.main_menu,menu);


         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.main_logout_btn){

             FirebaseUser currentUser = mAuth.getCurrentUser();   ////if two account from one phone
             if (currentUser!=null){

                 dataref.child("online").setValue(ServerValue.TIMESTAMP);

             }




             FirebaseAuth.getInstance().signOut();
             sendToStart();

         }
         if(item.getItemId()==R.id.main_settings_btn){
             Intent settingIntent = new Intent(MainActivity.this,settingsActivity.class);
           startActivity(settingIntent);

             //finish();   this was not letting you go back to main activity






         }

        if(item.getItemId()==R.id.main_all_btn){
            Intent AllUserActivity = new Intent(MainActivity.this,AllUserActivity.class);
            startActivity(AllUserActivity);



        }



        return true;
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {

            sendToStart();
        }

        ////only for non null user
        try {
            dataref = FirebaseDatabase.getInstance().getReference().child("users").
                    child(currentUser.getUid());
            dataref.child("online").setValue(0);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    protected void onPause() {
        super.onPause();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){

            dataref.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }
}

