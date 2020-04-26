package com.Tow_Buddy.Tow_Buddy_Customer_Side;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.Tow_Buddy.R;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.here.android.mpa.mapping.Map.Animation.NONE;

public class Activity_SetLocation extends AppCompatActivity
{
    public static String customerName, customerPhoneNumber, latLongForDatabase;
    private static final String TAG = Dialog_ConfirmCoordinates.class.getSimpleName();
    public Map userMap = null; //This is the map that will be used for the coordinate service
    public static double latitude, longitude;
    public static String  message;
    private static LocationManager locationManager;


    /**
     * permissions request code
     */
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent welcomeScreenIntent = this.getIntent();
        this.customerName = welcomeScreenIntent.getStringExtra("customerName");
        this.customerPhoneNumber = welcomeScreenIntent.getStringExtra("customerPhoneNumber");
        checkPermissions();
    }
    /**
     * Checks the dynamically-controlled permissions and requests missing permissions from end user.
     */
    public void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            Log.d("PermissionsGranted.", "Permissions Granted.");
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);

        }
    } //CheckPermissions() calls onRequestPermissionsResult

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], //calls initialize()
                                           @NonNull int[] grantResults)  {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                Log.d("PermissionsGranted", "Ready to initialize.");
                initialize();
                break;
        }
    }
    private void initialize() {
        // Search for the map fragment to finish setup by calling init().
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.init(this, new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted (OnEngineInitListener.Error error){
                if (error == OnEngineInitListener.Error.NONE)
                {
                    mapFragment.setMapMarkerDragListener(new OnDragListenerHandler());
                    // retrieve a reference of the map from the map fragment
                    Map map = mapFragment.getMap();
                    if (map == null)
                    {
                        Log.d("PARTNER,", "No Map");
                    }
                    else{
                        userMap = map; //This is the map that will be used for the coordinate service
                        createCoordinates();
                    }
                }
                else
                { Log.d("PARTNER,", error.getStackTrace()); }
            }
        } );
    } //calls createCoordinates
    public void createCoordinates() {
        if (userMap != null) {

            this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                createCoordinates();
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
            };
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    latLongForDatabase = latitude + ", " + longitude;
                    userMap.setCenter(new GeoCoordinate(latitude, longitude), NONE);
                    userMap.setZoomLevel((userMap.getMaxZoomLevel() + userMap.getMinZoomLevel()) / 1.2);
                    Log.d("Map created", "Map created, ready to create icon");
                    message = "Latitude, Longitude: " + latitude + ", " + longitude;
                    addDraggableMarker();

                }
            }
        } //calls addDraggableMarker
    }

    private void addDraggableMarker()
    {
        try {
            Image image = new Image();
            image.setImageResource(R.drawable.tow_buddy_larger_icon);
            MapMarker myMapMarker =
                    new MapMarker(new GeoCoordinate(latitude, longitude), image).setDraggable(true);
            myMapMarker.setDraggable(true);


            userMap.addMapObject(myMapMarker);

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void isThisYourLocation(View v) //This is the text message, this must be called from somewhere
    {
        Dialog_ConfirmCoordinates dialogConfirmCoordinates = new Dialog_ConfirmCoordinates();
        dialogConfirmCoordinates.show(getSupportFragmentManager(), "Where the choice is made.");
    }


    private class OnDragListenerHandler implements MapMarker.OnDragListener
    {
        @Override
        public void onMarkerDrag(MapMarker mapMarker)
        {
        }

        @Override
        public void onMarkerDragEnd(MapMarker mapMarker)
        {
            GeoCoordinate geoCoordinate = new GeoCoordinate(mapMarker.getCoordinate());
            latitude = geoCoordinate.getLatitude();
            longitude = geoCoordinate.getLongitude();
            message = "Latitude, Longitude: " + latitude + ", " + longitude;
        }

        @Override
        public void onMarkerDragStart(MapMarker mapMarker) {
            Log.i(TAG, "onMarkerDragStart: " + mapMarker.getTitle() + " -> " +mapMarker
                    .getCoordinate());
        }
    }
}


