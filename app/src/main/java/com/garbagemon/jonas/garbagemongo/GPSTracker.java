package com.garbagemon.jonas.garbagemongo;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Timer;
import java.util.TimerTask;

public class GPSTracker extends Service implements LocationListener {

    public LatLng location;
    private final Context context;
    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;//Maybe 1
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5;

    public GPSTracker(Context context) {
        this.context = context;

        /*final long period = 1000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getLocation();
            }
        }, 0, period);*/
        getLocation();
    }

    public LatLng getLocation() {
        if (!MainActivity.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context)) return null;

        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled) {
                this.canGetLocation = true;

                if (location == null) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.d("GPS Enabled", "GPS Enabled");
                    Location lastKnowLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastKnowLocation != null)
                        location = new LatLng(lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = new LatLng(location.getLatitude(), location.getLongitude());
        MainActivity.updateMap();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
