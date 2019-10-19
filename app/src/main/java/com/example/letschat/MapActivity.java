package com.example.letschat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity {


    public static String currentLocation;     // / / / /some shit
   private Button shareBtn;


    DatabaseReference mRootreff;
    DatabaseReference mChatRef;
    private String mCurrentUser;
    private String mChatId;

   private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRootreff= FirebaseDatabase.getInstance().getReference();
        mChatId=getIntent().getStringExtra("chatId");
        mChatRef=mRootreff.child("conversation").child(mChatId);




        shareBtn = (Button)findViewById(R.id.share_loc);

        locationManager= (LocationManager)getSystemService(LOCATION_SERVICE);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

            shareLocation();


                }
        });



    }

    private void shareLocation() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );

        }
        else
        {

            //check whether network provider is enabled
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100000, 10000, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        double latitude = location.getLatitude();
                        double longitude =location.getLongitude();
                        currentLocation=latitude+","+longitude;
                        sendMessage(currentLocation,"location");

                        Toast.makeText(MapActivity.this,"'location shared successfuly", Toast.LENGTH_SHORT).show();




                       // LatLng latLng = new LatLng(latitude,longitude);
                       // Toast.makeText(MapActivity.this,latLng.toString(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
            }//if network provider is not enabled then use gps provider
            else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {



                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000, 10000, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("location", location.toString());

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        currentLocation=latitude+","+longitude;

                        sendMessage(currentLocation,"location");


                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                });
            }
        }



    }




    private void sendMessage(String message,String type) {


        if(!TextUtils.isEmpty(message)){
            Map messageMap =new HashMap();
            messageMap.put("message" ,message);
            messageMap.put("type",type);
            messageMap.put("seen","false");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("sender",mCurrentUser);



            String uniqeMessageId =mRootreff.push().getKey();


            mChatRef.child(uniqeMessageId).setValue(messageMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(MapActivity.this,"can't write in database",Toast.LENGTH_LONG).show();
                }
            });








        }

    }


}
