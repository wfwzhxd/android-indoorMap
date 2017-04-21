package com.hunter.indoormap.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.hunter.indoormap.MapView;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;


/**
 * Created by hunter on 4/21/17.
 */

public class SelectOverlay extends Overlay {
    Paint paint;
    Node node;
    public SelectOverlay() {
        paint = new Paint();
        paint.setColor(Color.RED);
    }

    @Override
    public void draw(Canvas c, MapView mv) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        e.transform(mapView.getInvertMatrix());
        Point point = new Point(e.getX(), e.getY());
        for (Node node : mapView.getDataSource().getNodes(mapView.getMapRect(), mapView.getFloor())) {
            if (!node.isShow() || !node.contains(point)) {
                continue;
            }
            if (this.node == null) {
                this.node = node;
            } else {
                if (this.node.getArea() > node.getArea()) {
                    this.node = node;
                }
            }
            return true;
        }
        return super.onSingleTapConfirmed(e, mapView);
    }
}
