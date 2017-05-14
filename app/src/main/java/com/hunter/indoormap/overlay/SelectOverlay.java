package com.hunter.indoormap.overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hunter.indoormap.IMyLocationController;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.MatrixUtils;
import com.hunter.indoormap.R;
import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Rect;

import java.util.List;


/**
 * Created by hunter on 4/21/17.
 */

public class SelectOverlay extends Overlay implements IMyLocationController.OnMyLocationChangedListener{
    private static final String TAG = SelectOverlay.class.getSimpleName();

    private static final int WIDTH = 35;

    Paint paint;
    Bitmap bitmap;
    Node node;
    private int width;
    final Rect rect;
    final float density;
    android.graphics.Rect rect2;
    Point point;
    MapView mapView;

    public SelectOverlay(MapView mapView) {
        this.mapView = mapView;
        paint = new Paint();
        paint.setAntiAlias(true);
        bitmap = BitmapFactory.decodeResource(mapView.getResources(), R.mipmap.b_poi);
        density = mapView.getResources().getDisplayMetrics().density;
        width = (int) (WIDTH * density);
        rect = new Rect(0, 0, width, width);
        if (mapView.getMyLocationController() != null) {
            mapView.getMyLocationController().addOnMyLocationChangedListener(this);
        }
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        if (isEnabled() && node != null) {
            if (node.getXyz().z != mv.getLevel()) {
                closePanel();
                return;
            }
            point = MatrixUtils.applyMatrix(node.getXyz(), mv.getMapMatrix());
            point.offset(-rect.width()>>1, -rect.height());
            rect2 = new Rect(rect).offset((int)point.x, (int)point.y).toRect();
            c.drawBitmap(bitmap, null, rect2, paint);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (!isEnabled()) return false;
        Point point = new Point(e.getX(), e.getY());
        point = MatrixUtils.applyMatrix(point, mapView.getInvertMatrix());
        boolean shouleInvalidate = this.node == null ? false : true;
        this.node = null;
        List<Node> nodes = mapView.getDataSource().getNodes(mapView.getMapRect().enlarge(1.1f), mapView.getLevel());
        if (nodes == null) {
            return false;
        }
        for (Node node : nodes) {

            if (!node.contains(point)) {
                continue;
            }
            if (this.node == null || this.node.getArea() > node.getArea()) {
                this.node = node;
                shouleInvalidate = true;
            }
        }
        Log.i(TAG, "select node : " + node);
        if (node != null) {
            openPanel(node);
        } else {
            closePanel();
        }
        if (shouleInvalidate) {
            mapView.invalidate();
        }
        return false;
    }

    private boolean isPanelOpened;
    private View view;
    private RelativeLayout.LayoutParams layoutParams;

    private void openPanel(Node node) {
        if (node == null) {
            return;
        }
        if (view == null) {
            initView();
        }
        TextView textView = (TextView) view.findViewById(R.id.name);
        textView.setText(node.getName());
        View goThere = view.findViewById(R.id.button_right);
        if (mapView.getRouter() == null) {
            goThere.setEnabled(false);
            goThere.setVisibility(View.INVISIBLE);
        } else {
            GPoint myLocation;
            if (mapView.getMyLocationController() != null) {
                if ((myLocation = mapView.getMyLocationController().getMyLocation()) != null) {
                    if (!new GPoint(myLocation).equals(node.getXyz())) {
                        goThere.setVisibility(View.VISIBLE);
                        goThere.setEnabled(true);
                    } else {
                        goThere.setEnabled(false);
                        goThere.setVisibility(View.INVISIBLE);
                    }
                } else {
                    goThere.setEnabled(false);
                    goThere.setVisibility(View.INVISIBLE);
                }
            } else {
                goThere.setEnabled(false);
                goThere.setVisibility(View.INVISIBLE);
            }
        }

        if (!isPanelOpened) {
            this.mapView.addView(view, layoutParams);
        }
        isPanelOpened = true;
    }

    private void initView() {
        view = LayoutInflater.from(mapView.getContext()).inflate(R.layout.overlay_select, (ViewGroup)(mapView.getParent()), false);
        view.findViewById(R.id.button_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.getMyLocationController().setMyLocation(node.getXyz());
            }
        });
        view.findViewById(R.id.button_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapView.getRouter() != null) {
                    mapView.getRouter().route(mapView.getMyLocationController().getMyLocation(), node.getXyz(), null);
                }
                closePanel();
            }
        });
        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        int margin = (int) (10 * density);
        layoutParams.setMargins(margin, margin, margin, margin);
    }

    private void closePanel() {
        if (isPanelOpened) {
            mapView.removeView(view);
        }
        this.node = null;
        isPanelOpened = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event, MapView mapView) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (isPanelOpened) {
                closePanel();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event, mapView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView) {
        if (isPanelOpened) {
            Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
            if (rect.contains(event.getX(), event.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(event, mapView);
    }

    @Override
    public void onMyLocationChanged(GPoint newLocation) {
        if (node != null && node.getXyz().equals(newLocation)) {
            if (view != null) {
                view.findViewById(R.id.button_right).setEnabled(false);
            }
        }
    }
}
