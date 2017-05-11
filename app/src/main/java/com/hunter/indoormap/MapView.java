package com.hunter.indoormap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.data.DataSource;
import com.hunter.indoormap.overlay.DefaultOverlayManager;
import com.hunter.indoormap.overlay.Overlay;
import com.hunter.indoormap.overlay.OverlayManager;

import org.metalev.multitouch.controller.MultiTouchController;

import java.util.Arrays;
import java.util.List;

public class MapView extends RelativeLayout implements MultiTouchController.MultiTouchObjectCanvas<Object>{
    public static final String TAG = MapView.class.getSimpleName();

    private final GestureDetector mGestureDetector;
    private MultiTouchController<Object> multiTouchController;
    private OverlayManager mOverlayManager;

    private DataSource mDataSource;
    private int floor = Integer.MIN_VALUE;

    private IMyLocationController mIMyLocationController;

    private int mapScrollX;
    private int mapScrollY;

    private float scale;
    private float rotate;

    private boolean rotatable;

    private Matrix matrix;
    private Matrix invertMatrix;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mOverlayManager = new DefaultOverlayManager();
        mGestureDetector = new GestureDetector(context, new MapViewGestureDetectorListener()) {
            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                Log.i("GestureDetector", "onTouchEvent " + ev);
                return super.onTouchEvent(ev);
            }
        };
        mGestureDetector.setOnDoubleTapListener(new MapViewDoubleClickListener());
        multiTouchController = new MultiTouchController<Object>(this, false);

        matrix = new Matrix();
        invertMatrix = new Matrix();
        matrix.invert(invertMatrix);
    }

    public void mapScrollBy(int dx, int dy) {
        matrix.postTranslate(-dx, -dy);
        matrix.invert(invertMatrix);
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void mapScrollTo(int x, int y) {
        mapScrollBy(x-mapScrollX, y-mapScrollY);
        mapScrollX = x;
        mapScrollY = y;
    }

    public int getMapScrollX() {
        return mapScrollX;
    }

    public int getMapScrollY() {
        return mapScrollY;
    }

    public float getRotate() {
        return rotate;
    }

    public boolean isRotatable() {
        return rotatable;
    }

    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }

    /**
     * You can add/remove/reorder your Overlays using the List of {@link Overlay}. The first (index
     * 0) Overlay gets drawn first, the one with the highest as the last one.
     */
    public List<Overlay> getOverlays() {
        return this.getOverlayManager().overlays();
    }

    public OverlayManager getOverlayManager() {
        return mOverlayManager;
    }

    public void setOverlayManager(final OverlayManager overlayManager) {
        mOverlayManager = overlayManager;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public void setDataSource(DataSource mDataSource) {
        this.mDataSource = mDataSource;
    }

    public IMyLocationController getMyLocationController() {
        return mIMyLocationController;
    }

    public void setMyLocationController(IMyLocationController mIMyLocationController) {
        this.mIMyLocationController = mIMyLocationController;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        /*
        if (getDataSource().getFloorMap(floor) != null && this.floor != floor) {
            this.floor = floor;
            matrix = new Matrix();
            invalidate();
        }*/
    }

    public Matrix getMapMatrix() {
        return matrix;
    }

    public Matrix getInvertMatrix() {
        return invertMatrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public Rect getMapRect() {
        RectF rectF = new Rect(0, 0, getWidth(), getHeight()).enlarge(1.1f).toRectF();
        invertMatrix.mapRect(rectF);
        Log.i(TAG, "mapRect " + rectF);
        return new Rect(rectF);
    }

    @Override
    protected void dispatchDraw(final Canvas c) {
        if (new Matrix().equals(matrix)) {
//            Rect fBounds = getDataSource().getFloorMap(floor).getBounds();
            /*
            Log.i(TAG, "fBounds: " + fBounds);
            scale = getWidth()/fBounds.width();
            setMapCenter(new Point(fBounds.centerX(), fBounds.centerY()));*/
        }

        // Save the current canvas matrix
        c.save();
		/* Draw all Overlays. */
        this.getOverlayManager().onDraw(c, this);
        // Restore the canvas matrix
        c.restore();
        super.dispatchDraw(c);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
//        Log.i(TAG, "dispatchTouchEvent " + event);

        if (super.dispatchTouchEvent(event)) {
//            Log.d(MapView.TAG,"super handled touchEvent");
            return true;
        }


//        rotateTouchEvent(event);

        if (this.getOverlayManager().onTouchEvent(event, this)) {
//            Log.d(MapView.TAG,"Overlay handled touchEvent");
            return true;
        }

        if (multiTouchController != null && multiTouchController.onTouchEvent(event)) {
//            Log.d(MapView.TAG,"multiTouchController handled touchEvent");
            return true;
        }

        if (mGestureDetector.onTouchEvent(event)) {
//            Log.d(MapView.TAG,"mGestureDetector handled touchEvent");
            return true;
        }
//        Log.i(TAG, "not handle event " + event);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getOverlayManager().onKeyDown(keyCode, event, this)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public Object getDraggableObjectAtPoint(MultiTouchController.PointInfo touchPoint) {
        return this;
    }

    @Override
    public void getPositionAndScale(Object obj, MultiTouchController.PositionAndScale objPosAndScaleOut) {
        objPosAndScaleOut.set(0, 0, true, scale, false, 0, 0, true, (float) CoordinateUtils.degree2radians(rotate));
    }

    long lastMultiTouch;

    @Override
    public boolean setPositionAndScale(Object obj, MultiTouchController.PositionAndScale newObjPosAndScale, MultiTouchController.PointInfo touchPoint) {
        lastMultiTouch = System.currentTimeMillis();
        if (rotatable) {
            rotate = CoordinateUtils.radians2degree(newObjPosAndScale.getAngle());
        }
        scale = newObjPosAndScale.getScale();
        float[] centers = new float[]{touchPoint.getX(), touchPoint.getY()};
        Log.i(TAG, "rotate: " + rotate + " center: " + Arrays.toString(centers));
        invertMatrix.mapPoints(centers);
        Log.i(TAG, "centers " + Arrays.toString(centers));
        matrix.setScale(scale, scale, centers[0], centers[1]);
        matrix.postRotate(rotate, centers[0], centers[1]);
        matrix.postTranslate(touchPoint.getX()-centers[0], touchPoint.getY()-centers[1]);
        matrix.invert(invertMatrix);
        invalidate();
        return true;
    }

    @Override
    public void selectObject(Object obj, MultiTouchController.PointInfo touchPoint) {

    }

    public void setMapCenter(GPoint gPoint) {
        /*
        if (getDataSource().getFloorMap(gPoint.z) != null) {
            setFloor(gPoint.z);
            setMapCenter((Point)gPoint);
        }*/
    }

    private void setMapCenter(Point point) {
        float halfWidth = getWidth()>>1;
        float halfHeight = getHeight()>>1;
        matrix.setScale(scale, scale, point.x, point.y);
        matrix.postRotate(rotate, point.x, point.y);
        matrix.postTranslate(halfWidth-point.x, halfHeight-point.y);
        matrix.invert(invertMatrix);
        invalidate();
    }

    public GPoint getMapCenter() {
        Rect rect = getMapRect();
        return new GPoint(rect.centerX(), rect.centerY(), getFloor());
    }

    private class MapViewGestureDetectorListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(final MotionEvent e) {
//            Log.i(TAG, "onDown");
            MapView.this.getOverlayManager().onDown(e, MapView.this);
            return true;
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2,
                               final float velocityX, final float velocityY) {
//            Log.i(TAG, "onFling " + velocityX + " " + velocityY);
            return MapView.this.getOverlayManager().onFling(e1, e2, velocityX, velocityY, MapView.this);
        }

        @Override
        public void onLongPress(final MotionEvent e) {
//            Log.i(TAG, "onLongPress");
            MapView.this.getOverlayManager().onLongPress(e, MapView.this);
        }

        @Override
        public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX,
                                final float distanceY) {
            if (System.currentTimeMillis() - lastMultiTouch < 200) {
                Log.i(TAG, "too close with lastMultiTouch");
                return false;
            }
            Log.i(TAG, "onScroll " + distanceX + " " + distanceY);
            if (MapView.this.getOverlayManager().onScroll(e1, e2, distanceX, distanceY,
                    MapView.this)) {
                return true;
            }
            mapScrollBy((int) distanceX, (int) distanceY);
            return true;
        }

        @Override
        public void onShowPress(final MotionEvent e) {
//            Log.i(TAG, "onShowPress");
            MapView.this.getOverlayManager().onShowPress(e, MapView.this);
        }

        @Override
        public boolean onSingleTapUp(final MotionEvent e) {
//            Log.i(TAG, "onSingleTapUp");
            if (MapView.this.getOverlayManager().onSingleTapUp(e, MapView.this)) {
                return true;
            }

            return false;
        }

    }

    private class MapViewDoubleClickListener implements GestureDetector.OnDoubleTapListener {
        @Override
        public boolean onDoubleTap(final MotionEvent e) {
            return MapView.this.getOverlayManager().onDoubleTap(e, MapView.this);
        }

        @Override
        public boolean onDoubleTapEvent(final MotionEvent e) {
            return MapView.this.getOverlayManager().onDoubleTapEvent(e, MapView.this);
        }

        @Override
        public boolean onSingleTapConfirmed(final MotionEvent e) {
            return MapView.this.getOverlayManager().onSingleTapConfirmed(e, MapView.this);
        }
    }

}
