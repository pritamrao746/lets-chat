package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShareLocation extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;


    public void centerMapOnLocation(Location location ,String title)
    {
       if(location != null)
       {
           LatLng userLocation = new LatLng(location.getLatitude(),location.getLongitude());
           mMap.clear();
           mMap.addMarker(new MarkerOptions().position(userLocation)).setTitle(title);
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16));
       }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //Location Permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mLocationListener);
                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                try {
                    centerMapOnLocation(lastKnownLocation,"Your Location");
                }catch (Exception e)
                {
                    Toast.makeText(ShareLocation.this, "Null last loc ", Toast.LENGTH_SHORT).show();
                }


            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
     if( intent.getIntExtra("placeNumber",0) == 0)
     {
         //Zoom in on user location

         mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

         mLocationListener = new LocationListener() {
             @Override
             public void onLocationChanged(Location location) {
                 centerMapOnLocation(location,"Your Location");
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
         };

         //Checking Access Permission for map just like above
         if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
         {
             mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mLocationListener);

             //get last known location if permission is given
             Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             centerMapOnLocation(lastKnownLocation,"Your Location");
         }
         else
         {
             ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1 );
         }

     }else //if add places is not selected then we have to show the location saved
     {

         Location placeLocation = new Location(LocationManager.GPS_PROVIDER);

         int i = intent.getIntExtra("placeNumber",0);
         //all this means getting latlng.latitude since latlng is stored in locations arraylist of maps activity
         placeLocation.setLatitude(MapsActivity.locations.get(intent.getIntExtra("placeNumber",0)).latitude);
         placeLocation.setLongitude(MapsActivity.locations.get(intent.getIntExtra("placeNumber",0)).longitude);
         centerMapOnLocation(placeLocation,MapsActivity.places.get(i));

         //MapsActivity.places.get(i)  i.e get the value of ith position of places arraylist of MapsActivity

     }


    }

    @Override
    public void onMapLongClick(LatLng latLng)
    {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address ="";



        try{

            List<Address> listAdresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(listAdresses != null && listAdresses.size() >0)
            {
                if(listAdresses.get(0).getSubThoroughfare() != null)
                {
                    address += listAdresses.get(0).getSubThoroughfare()+" ";
                }

                address += listAdresses.get(0).getThoroughfare();
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }



    /*
        Didn't work out
        if(address.equals(""))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
            address = address + sdf.format(new Date());


        }


     */

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

        MapsActivity.places.add(address);
        MapsActivity.locations.add(latLng);

        MapsActivity.arrayAdapter.notifyDataSetChanged();

        Toast.makeText(ShareLocation.this, "Loaction Saved", Toast.LENGTH_SHORT).show();

    }
}
