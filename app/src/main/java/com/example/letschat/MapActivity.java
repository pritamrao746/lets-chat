package com.example.letschat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends AppCompatActivity {


    public static String currentLocation;     // / / / /some shit
   private Button shareBtn;
   private Button viewBtn;
   private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        shareBtn = (Button)findViewById(R.id.share_loc);
        viewBtn = (Button) findViewById(R.id.view_loc);
        locationManager= (LocationManager)getSystemService(LOCATION_SERVICE);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

            shareLocation();


                }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareLocIntent = new Intent(MapActivity.this,ShareLocationActivity.class);
                startActivity(shareLocIntent);
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
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        double latitude = location.getLatitude();
                        double longitude =location.getLongitude();
                        //instantiate the class Latlng
                        currentLocation=latitude+"//"+longitude;
                        Toast.makeText(MapActivity.this,currentLocation, Toast.LENGTH_SHORT).show();




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



                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i("location", location.toString());

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        //instantiate the class Latlng
                        LatLng latLng = new LatLng(latitude, longitude);

                        Toast.makeText(MapActivity.this,latLng.toString(), Toast.LENGTH_SHORT).show();


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
}
