package com.hunter.indoormap.overlay;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.hunter.indoormap.MapView;
import com.hunter.indoormap.R;

/**
 * Created by hunter on 4/21/17.
 */

public class RotateIndicatorOverlay extends Overlay {

    private static final int WIDTH = 50;
    private Bitmap compassRotate;
    private Bitmap compassLocked;
    private Rect rect;
    private int width;
    private float dRotate;

    private Paint paint;

    public RotateIndicatorOverlay(Resources resources) {
        this(resources, 0);
    }

    public RotateIndicatorOverlay(Resources resources, float dRotate) {
        compassLocked = BitmapFactory.decodeResource(resources, R.mipmap.compass_locked);
        compassRotate = BitmapFactory.decodeResource(resources, R.mipmap.compass_rotate);
        this.dRotate = dRotate;
        final float density = resources.getDisplayMetrics().density;
        width = (int) (WIDTH * density);
        rect = new Rect(width/5, width/5, width, width);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        c.save();
        c.rotate(mv.getRotate()+dRotate, rect.centerX(), rect.centerY());
        c.drawBitmap(mv.isRotatable()?compassRotate:compassLocked, null, rect, paint);
        c.restore();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        Matrix matrix = new Matrix();
        matrix.setRotate(mapView.getRotate()+dRotate, rect.centerX(), rect.centerY());
        matrix.invert(matrix);
        RectF rect2 = new RectF();
        matrix.mapRect(rect2, new RectF(rect.left, rect.top, rect.right, rect.bottom));
        if (rect2.contains((int)(e.getX()), (int)(e.getY()))) {
            mapView.setRotatable(!mapView.isRotatable());
            mapView.invalidate(new com.hunter.indoormap.beans.Rect(rect2).toRect());
            return true;
        }
        return false;
    }

    public float getdRotate() {
        return dRotate;
    }

    public void setdRotate(float dRotate) {
        this.dRotate = dRotate;
    }
}
