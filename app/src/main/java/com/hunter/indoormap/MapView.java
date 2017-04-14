package com.hunter.indoormap;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.List;

public class MapView extends RelativeLayout {
    public static final String LOGTAG = MapView.class.getSimpleName();

    private final GestureDetector mGestureDetector;
    private OverlayManager mOverlayManager;

    public MapView(Context context) {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mOverlayManager = new DefaultOverlayManager();
        mGestureDetector = new GestureDetector(context, new MapViewGestureDetectorListener());
        mGestureDetector.setOnDoubleTapListener(new MapViewDoubleClickListener());
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

        if (super.dispatchTouchEvent(event)) {
            Log.d(MapView.LOGTAG,"super handled onTouchEvent");
            return true;
        }

        if (this.getOverlayManager().onTouchEvent(event, this)) {
            return true;
        }

        if (mGestureDetector.onTouchEvent(event)) {
            Log.d(MapView.LOGTAG,"mGestureDetector handled onTouchEvent");
            return true;
        }
        return false;
    }

    private class MapViewGestureDetectorListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(final MotionEvent e) {
            return MapView.this.getOverlayManager().onDown(e, MapView.this);
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2,
                               final float velocityX, final float velocityY) {
            return MapView.this.getOverlayManager().onFling(e1, e2, velocityX, velocityY, MapView.this);
        }

        @Override
        public void onLongPress(final MotionEvent e) {
            MapView.this.getOverlayManager().onLongPress(e, MapView.this);
        }

        @Override
        public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX,
                                final float distanceY) {
            if (MapView.this.getOverlayManager().onScroll(e1, e2, distanceX, distanceY,
                    MapView.this)) {
                return true;
            }

            scrollBy((int) distanceX, (int) distanceY);
            return true;
        }

        @Override
        public void onShowPress(final MotionEvent e) {
            MapView.this.getOverlayManager().onShowPress(e, MapView.this);
        }

        @Override
        public boolean onSingleTapUp(final MotionEvent e) {
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
