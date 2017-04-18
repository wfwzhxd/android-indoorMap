// Created by plusminus on 20:32:01 - 27.09.2008
package com.hunter.indoormap.overlay;

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.hunter.indoormap.MapView;

/**
 * {@link Overlay}: Base class representing an overlay which may be displayed on top of a {@link MapView}.
 *
 * To add an overlay, subclass this class, create an instance, and add it to the list obtained from
 * getOverlays() of {@link MapView}.
 *
 * This class implements a form of Gesture Handling similar to
 * {@link GestureDetector.SimpleOnGestureListener} and
 * {@link GestureDetector.OnGestureListener}. The difference is there is an additional argument for
 * the item.
 *
 * <img alt="Class diagram around Marker class" width="686" height="413" src='./doc-files/marker-classes.png' />
 *
 * @author Nicolas Gramlich
 */
public abstract class Overlay {

	// ===========================================================
	// Constants
	// ===========================================================

	private static AtomicInteger sOrdinal = new AtomicInteger();

	// From Google Maps API
	protected static final float SHADOW_X_SKEW = -0.8999999761581421f;
	protected static final float SHADOW_Y_SCALE = 0.5f;

	// ===========================================================
	// Fields
	// ===========================================================

	private static final Rect mRect = new Rect();
	private boolean mEnabled = true;

	// ===========================================================
	// Constructors
	// ===========================================================

	/** Use {@link #Overlay()} instead */
	@Deprecated
	public Overlay(final Context ctx) {
	}

	public Overlay() {
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Sets whether the Overlay is marked to be enabled. This setting does nothing by default, but
	 * should be checked before calling draw().
	 */
	public void setEnabled(final boolean pEnabled) {
		this.mEnabled = pEnabled;
	}

	/**
	 * Specifies if the Overlay is marked to be enabled. This should be checked before calling
	 * draw().
	 *
	 * @return true if the Overlay is marked enabled, false otherwise
	 */
	public boolean isEnabled() {
		return this.mEnabled;
	}

	/**
	 * Since the menu-chain will pass through several independent Overlays, menu IDs cannot be fixed
	 * at compile time. Overlays should use this method to obtain and store a menu id for each menu
	 * item at construction time. This will ensure that two overlays don't use the same id.
	 *
	 * @return an integer suitable to be used as a menu identifier
	 */
	protected final static int getSafeMenuId() {
		return sOrdinal.getAndIncrement();
	}

	/**
	 * Similar to {@link #getSafeMenuId()}, except this reserves a sequence of IDs of length
	 * {@code count}. The returned number is the starting index of that sequential list.
	 *
	 * @return an integer suitable to be used as a menu identifier
	 * @see #getSafeMenuId()
	 */
	protected final static int getSafeMenuIdSequence(final int count) {
		return sOrdinal.getAndAdd(count);
	}

	// ===========================================================
	// Methods for SuperClass/Interfaces
	// ===========================================================


	public abstract void draw(final Canvas c, final MapView mv);

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * Override to perform clean up of resources before shutdown. By default does nothing.
	 */
	public void onDetach(final MapView mapView) {
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onKeyDown(final int keyCode, final KeyEvent event, final MapView mapView) {
		return false;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onKeyUp(final int keyCode, final KeyEvent event, final MapView mapView) {
		return false;
	}

	/**
	 * <b>You can prevent all(!) other Touch-related events from happening!</b><br>
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onTouchEvent(final MotionEvent event, final MapView mapView) {
		return false;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onTrackballEvent(final MotionEvent event, final MapView mapView) {
		return false;
	}

	/** GestureDetector.OnDoubleTapListener **/

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onDoubleTap(final MotionEvent e, final MapView mapView) {
		return false;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onDoubleTapEvent(final MotionEvent e, final MapView mapView) {
		return false;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
		return false;
	}

	/** OnGestureListener **/

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onDown(final MotionEvent e, final MapView mapView) {
		return false;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onFling(final MotionEvent pEvent1, final MotionEvent pEvent2,
			final float pVelocityX, final float pVelocityY, final MapView pMapView) {
		return false;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onLongPress(final MotionEvent e, final MapView mapView) {
		return false;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onScroll(final MotionEvent pEvent1, final MotionEvent pEvent2,
			final float pDistanceX, final float pDistanceY, final MapView pMapView) {
		return false;
	}

	public void onShowPress(final MotionEvent pEvent, final MapView pMapView) {
		return;
	}

	/**
	 * By default does nothing ({@code return false}). If you handled the Event, return {@code true}
	 * , otherwise return {@code false}. If you returned {@code true} none of the following Overlays
	 * or the underlying {@link MapView} has the chance to handle this event.
	 */
	public boolean onSingleTapUp(final MotionEvent e, final MapView mapView) {
		return false;
	}

	/**
	 * Convenience method to draw a Drawable at an offset. x and y are pixel coordinates. You can
	 * find appropriate coordinates from latitude/longitude using the MapView.getProjection() method
	 * on the MapView passed to you in draw(Canvas, MapView, boolean).
	 *
	 * @param shadow
	 *            If true, draw only the drawable's shadow. Otherwise, draw the drawable itself.
	 * @param aMapOrientation
	 */
	protected synchronized static void drawAt(final Canvas canvas, final Drawable drawable,
											  final int x, final int y, final boolean shadow,
											  final float aMapOrientation) {
		canvas.save();
		canvas.rotate(-aMapOrientation, x, y);
		drawable.copyBounds(mRect);
		drawable.setBounds(mRect.left + x, mRect.top + y, mRect.right + x, mRect.bottom + y);
		drawable.draw(canvas);
		drawable.setBounds(mRect);
		canvas.restore();
	}

}
