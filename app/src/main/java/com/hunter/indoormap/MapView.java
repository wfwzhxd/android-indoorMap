package com.hunter.indoormap;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Rect;
import com.hunter.indoormap.data.DataSource;
import com.hunter.indoormap.overlay.DefaultOverlayManager;
import com.hunter.indoormap.overlay.Overlay;
import com.hunter.indoormap.overlay.OverlayManager;

import java.util.List;

public class MapView extends RelativeLayout {
    public static final String TAG = MapView.class.getSimpleName();

    private final GestureDetector mGestureDetector;
    private OverlayManager mOverlayManager;

    private DataSource mDataSource;
    private int floor;

    private float scale = 1.0f;

    private int mapScrollX;
    private int mapScrollY;

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
    }

    public void mapScrollBy(int dx, int dy) {
        mapScrollTo(mapScrollX + dx, mapScrollY + dy);
    }

    public void mapScrollTo(int x, int y) {
        mapScrollX = x;
        mapScrollY = y;
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public int getMapScrollX() {
        return mapScrollX;
    }

    public int getMapScrollY() {
        return mapScrollY;
    }

    public Point[] translatePoint(Point[] points) {
        for (Point point : points) {
            translatePoint(point);
        }
        return points;
    }

    public Point translatePoint(Point point) {
        point.x -= mapScrollX;
        point.y -= mapScrollY;
        return point;
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

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Rect getMapRect() {
        return getScreenRect().scale(1/scale).enlarge(1.1f);
    }

    public Rect getScreenRect() {
        return new Rect(getScrollX(), getScrollY(), getScrollX()+getWidth(), getScrollY()+getHeight());
    }

    @Override
    protected void dispatchDraw(final Canvas c) {
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
        Log.i(TAG, "dispatchTouchEvent " + event);
        if (super.dispatchTouchEvent(event)) {
            Log.d(MapView.TAG,"super handled touchEvent");
            return true;
        }

        if (this.getOverlayManager().onTouchEvent(event, this)) {
            Log.d(MapView.TAG,"Overlay handled touchEvent");
            return true;
        }

        if (mGestureDetector.onTouchEvent(event)) {
            Log.d(MapView.TAG,"mGestureDetector handled touchEvent");
            return true;
        }
        Log.i(TAG, "not handle event " + event);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    private class MapViewGestureDetectorListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(final MotionEvent e) {
            Log.i(TAG, "onDown");
            MapView.this.getOverlayManager().onDown(e, MapView.this);
            return true;
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2,
                               final float velocityX, final float velocityY) {
            Log.i(TAG, "onFling " + velocityX + " " + velocityY);
            return MapView.this.getOverlayManager().onFling(e1, e2, velocityX, velocityY, MapView.this);
        }

        @Override
        public void onLongPress(final MotionEvent e) {
            Log.i(TAG, "onLongPress");
            MapView.this.getOverlayManager().onLongPress(e, MapView.this);
        }

        @Override
        public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX,
                                final float distanceY) {
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
            Log.i(TAG, "onShowPress");
            MapView.this.getOverlayManager().onShowPress(e, MapView.this);
        }

        @Override
        public boolean onSingleTapUp(final MotionEvent e) {
            Log.i(TAG, "onSingleTapUp");
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
