package com.garbagemon.jonas.garbagemongo;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class TrashHandler {
    private List<Trash> trashLocations = new ArrayList<Trash>();
    private Map map;

    public TrashHandler(Map map) {
        this.map = map;
    }

    public void addTrash(LatLng location) {
        if (isCloseToTrash(location) != -1) {
            Log.d("Trash", "Could not add trash: Too close to other trash");
            return;
        }

        int markerID = map.addTrash(location);
        trashLocations.add(new Trash(location, markerID));

        Log.d("Trash","Added trash to: " + location);
    }

    public int isCloseToTrash(LatLng player) {
        int closestTrash = -1;
        double distanceToClosestTrash = Double.MAX_VALUE;

        Location location = new Location("");
        location.setLatitude(player.latitude);
        location.setLongitude(player.longitude);

        for (int i = 0; i < trashLocations.size(); i++) {
            Trash trash = trashLocations.get(i);
            Location trashLocation = new Location("");
            trashLocation.setLatitude(trash.location.latitude);
            trashLocation.setLongitude(trash.location.longitude);

            double dist = location.distanceTo(trashLocation);

            if (dist < distanceToClosestTrash) {
                distanceToClosestTrash = dist;
                closestTrash = i;
            }
        }

        if (distanceToClosestTrash >= 5*5) closestTrash = -1;

        Log.d("Trash","Closest trash is: " + closestTrash);

        return closestTrash;
    }

    public void collectTrash(int token) {
        if (!map.removeTrash(trashLocations.get(token).id)) return;

        trashLocations.remove(token);

        Log.d("Trash","Collected trash");
    }

    private class Trash {
        public LatLng location;
        public int id;

        public Trash(LatLng location, int id) {
            this.location = location;
            this.id = id;
        }
    }
}
