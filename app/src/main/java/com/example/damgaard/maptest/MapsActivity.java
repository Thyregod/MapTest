package com.example.damgaard.maptest;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        android.location.LocationListener {

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    private LocationManager locationManager;
    private String provider;
    private String latitude = "";
    private String longitude = "";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private String userID;
    private Marker friendMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);


        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            System.out.println("Location not available");
        }
        System.out.println("UserID is : " + userID);

        Thread timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("Doing a 5 second sleep");
                    sleep(5000);
                    Response.Listener<String> othersLocationResponseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    int i = 1;
                                    Map<String, String> userList = new HashMap<>();
                                    while(jsonResponse.length() > i) {
                                        userList.put("user" + i, jsonResponse.getString("user" + i));
                                        i++;
                                    }
                                    System.out.println(userList);

                                    int c = 1;
                                    System.out.println("userList is so long : " + userList.size());
                                    while (c <= userList.size() ) {
                                        //add a marker for each user in the database
                                        String user = userList.get("user" + c);
                                        String[] output = user.split(":");
                                        String output0 = output[0];
                                        System.out.println("output 0 is :" + output[0]);
                                        System.out.println("output 1 is :" + output[1]);

                                        if(output[0] != null && !output0.isEmpty()) {
                                            addFriendMarker(output[0], output[1], output[2], output[3]);
                                        }

                                        c++;
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    loadAllLocation loadAllLocation = new loadAllLocation(othersLocationResponseListener);
                    RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                    queue.add(loadAllLocation);
                }
            }
        });
        timeThread.start();
    }

    private static void sleep(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        } catch(InterruptedException e){
        // ignore this exception; it won't happen anyhow
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("changedLocation");
        float lat = (float) (location.getLatitude());
        float lng = (float) (location.getLongitude());

        latitude = "" + lat;
        longitude = "" + lng;
        System.out.println("Latitude is: " + latitude);
        System.out.println("Longitude is " + longitude);
        System.out.println("Location is:" + lat + "," + lng);

        System.out.println("Trying to update new location on the database");
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        System.out.println("Success fully update the location");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        LocationUpdate locationUpdate = new LocationUpdate(latitude, longitude, userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        queue.add(locationUpdate);

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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            mGoogleMap.setMyLocationEnabled(true);
        }

        addFriendMarker("55.907765", "9.259482", "Henrik", "Datalogi");
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    public float chooseMarkerColor(String study) {
        if(study.equals("Datalogi")){
            return BitmapDescriptorFactory.HUE_GREEN;
        }
        else if (study.equals("IT")){
            return BitmapDescriptorFactory.HUE_CYAN;
        }
        else if (study.equals("Medievidenskab")){
            return BitmapDescriptorFactory.HUE_ROSE;
        }
        else if (study.equals("Informationsvidenskab")){
            return BitmapDescriptorFactory.HUE_MAGENTA;
        }
        else if (study.equals("Digital Design")){
            return BitmapDescriptorFactory.HUE_VIOLET;
        }
        else if (study.equals("Molekylærbiologi")){
            return BitmapDescriptorFactory.HUE_ORANGE;
        }
        return BitmapDescriptorFactory.HUE_AZURE;
    }

    public void addFriendMarker(String latitude, String longitude, String name, String study){
        double lat = Double.parseDouble(latitude);
        double lng = Double.parseDouble(longitude);
        LatLng latLng = new LatLng(lat, lng);
        friendMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(name)
                .icon(BitmapDescriptorFactory.defaultMarker(chooseMarkerColor(study))));
    }

}
