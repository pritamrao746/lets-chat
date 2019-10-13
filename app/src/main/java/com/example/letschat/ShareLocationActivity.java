package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class ShareLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                try
                {
                    shareLocation();
                }
                catch (Exception e)
                {
                    Toast.makeText(ShareLocationActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        shareLocation();
    }

    private void shareLocation()
    {
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
                        LatLng latLng = new LatLng(latitude,longitude);
                        LatLng target = new LatLng(28.644800,77.216721);
                        //Instantiate the class Geocoder which will convert latlng to address
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try
                        {
                            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                            String loc = addressList.get(0).getSubLocality();
                            loc =loc+","+addressList.get(0).getCountryName();

                            List<Address> addressList1 = geocoder.getFromLocation(28.644800,77.216721,1);
                            String targ = addressList1.get(0).getSubLocality();
                            targ =targ+","+addressList1.get(0).getCountryName();
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(loc));
                            mMap.addMarker(new MarkerOptions().position(target).title(targ));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,4.5f));
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

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

                        double latitude = location.getLatitude();
                        double longitude =location.getLongitude();
                        //instantiate the class Latlng
                        LatLng latLng = new LatLng(latitude,longitude);
                        LatLng target = new LatLng(18.96301,72.82304);
                        //Instantiate the class Geocoder which will convert latlng to address
                        Geocoder geocoder = new Geocoder(getApplicationContext());
                        try
                        {
                            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,1);
                            String loc = addressList.get(0).getSubLocality();
                            loc =loc+","+addressList.get(0).getCountryName();
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(loc));
                            mMap.addMarker(new MarkerOptions().position(target).title("Target Location"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,4.5f));
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

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
