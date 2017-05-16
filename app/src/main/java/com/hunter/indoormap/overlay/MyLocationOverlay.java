package com.hunter.indoormap.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;

import com.hunter.indoormap.IMyLocationController;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.MatrixUtils;
import com.hunter.indoormap.R;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.util.Observable;
import com.hunter.indoormap.util.Observer;

/**
 * Created by hunter on 4/22/17.
 */

public class MyLocationOverlay extends Overlay implements IMyLocationController{
    private static final String TAG = MyLocationOverlay.class.getSimpleName();
    private static final int WIDTH = 27;
    GPoint myLocation;
    MapView mapView;
    Bitmap bitmap;
    final Rect rect;
    int width;
    Point point;
    Paint paint;
    Observable observable = new Observable() {
        @Override
        protected void doNotify(Observer observer, Object data) {
            ((OnMyLocationChangedListener)observer).onMyLocationChanged((GPoint) data);
        }
    };

    public MyLocationOverlay(MapView mapView) {
        this.mapView = mapView;
        paint = new Paint();
        paint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.my_location);
        final float density = mapView.getResources().getDisplayMetrics().density;
        width = (int) (WIDTH * density);
        rect = new Rect(0, 0, width, width);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        if (myLocation != null && mv.getLevel() == myLocation.z) {
            point = MatrixUtils.applyMatrix(myLocation, mv.getMapMatrix());
            point.offset(-rect.width()>>1, -rect.height()>>1);
            c.drawBitmap(bitmap, null, new Rect(rect).offset(point.x, point.y).toRect(), paint);
        }
    }

    public GPoint getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(GPoint myLocation) {
        if (myLocation == null) return;
        Log.i(TAG, "myLocation: " + myLocation);
        this.myLocation = myLocation;
        if (mapView.getLevel() != myLocation.z || !mapView.getMapRect().contains(myLocation.x, myLocation.y)) {
            mapView.setMapCenter(myLocation);
        }
        mapView.invalidateInMain();
        observable.setChanged();
        observable.notifyObservers(myLocation);
    }

    @Override
    public void addOnMyLocationChangedListener(OnMyLocationChangedListener listener) {
        observable.addObserver(listener);
    }

    @Override
    public void removeOnMyLocationChangedListener(OnMyLocationChangedListener listener) {
        observable.deleteObserver(listener);
    }

    @Override
    public boolean onLongPress(MotionEvent e, MapView mapView) {
        // For Test
        Point point = new Point(e.getX(), e.getY());
        point = MatrixUtils.applyMatrix(point, mapView.getInvertMatrix());
        setMyLocation(new GPoint(point.x, point.y , mapView.getLevel()));
        return true;
    }

}
