//Made by Jonas Jensen

package com.garbagemon.jonas.garbagemongo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ClipDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
    private static GPSTracker GPS;
    private static Map map;
    private TrashHandler trash;
    private Exp exp;
    private Thread main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkPermissions();

        ClipDrawable XPBar = (ClipDrawable) ((ImageView) findViewById(R.id.XPBarFill)).getDrawable();
        TextView level = (TextView) findViewById(R.id.level);
        ClipDrawable modifier = (ClipDrawable) ((ImageView) findViewById(R.id.modifierbarFill)).getDrawable();
        TextView currentModifier = (TextView) findViewById(R.id.current);
        TextView nextModifier = (TextView) findViewById(R.id.next);

        exp = new Exp(this, XPBar, level, modifier, currentModifier, nextModifier);

        map = new Map();
        trash = new TrashHandler(map, exp);
        GPS = new GPSTracker(MainActivity.this);

        main = new Thread(new Runnable() {
            @Override
            public void run() {
                loop();
            }
        });
        main.start();
    }

    private void loop() {

        while(true) {
            long startTime = System.nanoTime();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    exp.updateExp();
                    if (GPS.location == null) GPS.getLocation();
                }
            });

            try {
                Thread.sleep((1000L/60L)-(System.nanoTime() - startTime) / 1000000L);
            } catch (InterruptedException | IllegalArgumentException e) {e.printStackTrace();}
        }
    }

    public static void updateMap() {
        map.update(GPS.getLocation());
    }

    public static void focusOn(LatLng location) {
        map.focus(location);
    }

    public void addTrash(View view) {
        trash.addTrash(GPS.getLocation());
    }

    public void collectTrash(View view) {
        int token = trash.isCloseToTrash(GPS.getLocation());

        if (token == -1) return;

        trash.collectTrash(token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        map.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        map.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    private void checkPermissions() {
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, MainActivity.this);
    }

    static int requestCode;
    public static boolean checkPermission(String permission, Context context) {
        boolean gotPermission = true;

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                gotPermission = false;
                Log.w("Permission", "Didn't get permission: " + permission);
            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, requestCode++);
            }
        }

        return gotPermission;
    }
}
