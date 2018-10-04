package com.garbagemon.jonas.garbagemongo;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map extends SupportMapFragment implements OnMapReadyCallback {

    private View mapView;
    private static GoogleMap gMap;
    private TouchableWrapper tw;
    private Marker player;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mapView = super.onCreateView(inflater, parent, savedInstanceState);
        tw = new TouchableWrapper(getActivity(), gMap);
        tw.addView(mapView);
        getMapAsync(this);
        return tw;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setScrollGesturesEnabled(false);
    }

    public void update(LatLng location) {
        if (location == null) return;

        if (player == null)
            player = gMap.addMarker(new MarkerOptions().position(location).title("Player"));
        else
            player.setPosition(location);

        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(location).tilt(67.5f).zoom(18).bearing(gMap.getCameraPosition().bearing).build()));
    }

    @Override
    public View getView() {
        return mapView;
    }

    public static GoogleMap getgMap() {
        return gMap;
    }
}
