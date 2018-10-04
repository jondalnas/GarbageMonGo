package com.garbagemon.jonas.garbagemongo;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class TouchableWrapper extends FrameLayout {
    public TouchableWrapper(Context c, GoogleMap gMap) {
        super(c);
    }

    Point touchPoint = new Point();
    @Override
    public boolean dispatchTouchEvent(MotionEvent eve) {
        final int action = eve.getActionMasked();
        int pointerIndex = eve.getActionIndex();
        GestureDetector g = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener());
        g.onTouchEvent(eve);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // get the point the user's finger touches
                touchPoint.x = (int) eve.getX(pointerIndex);
                touchPoint.y = (int) eve.getY(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                if (eve.getPointerCount() < 2) {   // leave two finger gestures for other actions
                    final Point newTouchPoint = new Point();  // the new position of user's finger on screen after movement is detected
                    newTouchPoint.x = (int) eve.getX(pointerIndex);
                    newTouchPoint.y = (int) eve.getY(pointerIndex);
                    Point centerOfMap = getCenterOfMapAsPoint();   // center of map(as Point object) for calculation of angle
                    // now you need to calculate the angle betwwen 2 lines with centerOfMap as center:
                    //line 1: imaginary line between first touch detection on screen - and the center of the map
                    //line 2: imaginary line between last place finger moved on screen - and the center of the map
                    final float angle = angleBetweenLines(centerOfMap, touchPoint, newTouchPoint);
                    final LatLng latlng = Map.getgMap().getCameraPosition().target;  //set camera movement to that position
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            // move the camera (NOT animateCamera() ) to new position with "bearing" updated
                            if ((angle+"").contains("NaN")) return;

                            Log.d("Angle",angle+"");

                            Map.getgMap().moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(latlng).tilt(67.5f).zoom(18).bearing(Map.getgMap().getCameraPosition().bearing - angle).build()));
                        }
                    });

                    touchPoint = newTouchPoint; // update touchPoint value
                    return true;
                } else
                    break;
        }

        return true;
    }

    public Point getCurrentLocation() {
        Projection projection = Map.getgMap().getProjection();
        return projection.toScreenLocation(Map.getgMap().getCameraPosition().target);
    }

    public float angleBetweenLines(Point center, Point endLine1, Point endLine2) {
        if (endLine1.x == endLine2.x && endLine1.y == endLine2.y) return 0;

        float v0x = endLine1.x - center.x;
        float v0y = endLine1.y - center.y;
        float v1x = endLine2.x - center.x;
        float v1y = endLine2.y - center.y;

        float angle = (float) Math.atan2(v1y, v1x) - (float) Math.atan2(v0y, v0x);

        return (float) (angle * 180.0 / Math.PI);
    }

    public Point getCenterOfMapAsPoint() {
        return new Point((int) (getX() + getWidth() / 2), (int) (getY() + getHeight() / 2));
    }
}
