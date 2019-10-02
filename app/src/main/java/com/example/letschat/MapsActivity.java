package com.example.letschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity {

    private ListView mListView;
    private Toolbar mToolbar;

    static ArrayList <String> places = new ArrayList<>();
    static ArrayList<LatLng> locations = new ArrayList<>();

    static ArrayAdapter arrayAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mListView = (ListView)findViewById(R.id.listView);
        mToolbar=(Toolbar)findViewById(R.id.maps_toolbar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maps");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        setListView();
    }


    private void setListView() {




        places.add("Add new place...");
        locations.add(new LatLng(0,0));     //this is 0th position value of location arraylist hardcoded


         arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);

        //connecting list view and array adapter
        mListView.setAdapter(arrayAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(MapsActivity.this,ShareLocation.class);
                intent.putExtra("placeNumber",i);
                startActivity(intent);
            }
        });

    }
}
