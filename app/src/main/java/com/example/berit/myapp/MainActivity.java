package com.example.berit.myapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mGoogleMap;
    private Menu mOptionsMenu;

    //Actions with GPS location
    private LocationManager locationManager;
    private String provider;
    private Location locationPrevious;

    //Logging
    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Important for display gmap
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.gmap);
        mapFragment.getMapAsync(this);

        //Initialize locationManager. From location service locationManager is asked
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Best service is selected automatically (GPS, Cell tower, WIFI)
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Log.d(TAG, provider);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No FINE location permission!");
        }
        //CELL and WIFI check, no GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No COARSE location permission!");
        }
        locationPrevious = locationManager.getLastKnownLocation(provider);

        //if location is known then this method is called
        if(locationPrevious != null){
            onLocationChanged(locationPrevious);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mOptionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_maptype_normal:
            case R.id.menu_maptype_hybrid:
            case R.id.menu_maptype_satellite:
            case R.id.menu_maptype_terrain:
            case R.id.menu_maptype_none:

                item.setChecked(true);
                changeMapType();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeMapType() {
        //check, if map is working
        if (mGoogleMap == null) {
            return;
        }

        if (mOptionsMenu.findItem(R.id.menu_maptype_normal).isChecked()) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (mOptionsMenu.findItem(R.id.menu_maptype_hybrid).isChecked()) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (mOptionsMenu.findItem(R.id.menu_maptype_satellite).isChecked()) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (mOptionsMenu.findItem(R.id.menu_maptype_terrain).isChecked()) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (mOptionsMenu.findItem(R.id.menu_maptype_none).isChecked()) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }

        Log.d(TAG, "Map type changed!");
    }

    @Override
    //if everything is ok, then map is loaded
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng est = new LatLng(58.6076103, 25.1043362);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(est, 6));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //current location
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    //Notify GPS to send updates
    protected void onResume(){
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No FINE location permission!");
        }
        //CELL and WIFI check, no GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No COARSE location permission!");
        }

        if(locationManager != null){
            locationManager.requestLocationUpdates(provider,500,1,this);
        }
    }

    @Override
    //method to do smth while being on background
    protected void onPause(){
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No COARSE location permission");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "No FINE location permission");
        }

        if(locationManager != null){
            //remove updates
            locationManager.removeUpdates(this);
        }
    }
}
