package com.hunter.indoormap.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.hunter.indoormap.IMyLocationController;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.MatrixUtils;
import com.hunter.indoormap.R;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Rect;

/**
 * Created by hunter on 4/22/17.
 */

public class MyLocationOverlay extends Overlay implements IMyLocationController{
    private static final int WIDTH = 27;
    GPoint myLocation;
    MapView mapView;
    Bitmap bitmap;
    final Rect rect;
    int width;
    Point point;
    Paint paint;

    public MyLocationOverlay(MapView mapView) {
        this.mapView = mapView;
        myLocation = mapView.getMapCenter();
        paint = new Paint();
        paint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.my_location);
        final float density = mapView.getResources().getDisplayMetrics().density;
        width = (int) (WIDTH * density);
        rect = new Rect(0, 0, width, width);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        point = MatrixUtils.applyMatrix(myLocation, mv.getMatrix());
        point.offset(-rect.width()>>1, -rect.height()>>1);
        c.drawBitmap(bitmap, null, new Rect(rect).offset(point.x, point.y).toRect(), paint);
    }

    public GPoint getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(GPoint myLocation) {
        this.myLocation = myLocation;
        mapView.setMapCenter(myLocation);
    }
}
