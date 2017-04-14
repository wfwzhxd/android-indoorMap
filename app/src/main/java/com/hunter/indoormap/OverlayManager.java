package com.hunter.indoormap;

import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import java.util.List;

public interface OverlayManager extends List<Overlay> {
    Overlay get(int pIndex);

    int size();

    void add(int pIndex, Overlay pElement);

    Overlay remove(int pIndex);

    Overlay set(int pIndex, Overlay pElement);

    List<Overlay> overlays();

    Iterable<Overlay> overlaysReversed();

    void onDraw(Canvas c, MapView pMapView);

    void onDetach(MapView pMapView);

    boolean onKeyDown(int keyCode, KeyEvent event, MapView pMapView);

    boolean onKeyUp(int keyCode, KeyEvent event, MapView pMapView);

    boolean onTouchEvent(MotionEvent event, MapView pMapView);

    boolean onTrackballEvent(MotionEvent event, MapView pMapView);

    boolean onDoubleTap(MotionEvent e, MapView pMapView);

    boolean onDoubleTapEvent(MotionEvent e, MapView pMapView);

    boolean onSingleTapConfirmed(MotionEvent e, MapView pMapView);

    boolean onDown(MotionEvent pEvent, MapView pMapView);

    boolean onFling(MotionEvent pEvent1, MotionEvent pEvent2,
                    float pVelocityX, float pVelocityY, MapView pMapView);

    boolean onLongPress(MotionEvent pEvent, MapView pMapView);

    boolean onScroll(MotionEvent pEvent1, MotionEvent pEvent2,
                     float pDistanceX, float pDistanceY, MapView pMapView);

    void onShowPress(MotionEvent pEvent, MapView pMapView);

    boolean onSingleTapUp(MotionEvent pEvent, MapView pMapView);

    void setOptionsMenusEnabled(boolean pEnabled);

    boolean onCreateOptionsMenu(Menu pMenu, int menuIdOffset, MapView mapView);

    boolean onPrepareOptionsMenu(Menu pMenu, int menuIdOffset, MapView mapView);

    boolean onOptionsItemSelected(MenuItem item, int menuIdOffset, MapView mapView);

}
