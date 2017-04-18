package com.hunter.indoormap.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hunter on 4/17/17.
 */

public class NodeOverlay extends Overlay {
    private static final String TAG = NodeOverlay.class.getSimpleName();

    Paint fillPaint;
    Paint strokePaint;

    public NodeOverlay() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.BLUE);
        strokePaint = new Paint();
        strokePaint.setStrokeWidth(2.0f);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        List<Node> nodes = mv.getDataSource().getNodes(mv.getMapRect(), mv.getFloor());
        float scale = mv.getScale();
        for (Node node : nodes) {
            if (!node.isShow()) {
                continue;
            }
            Path edge = new Path();
            Point[] points = CoordinateUtils.absoluteCoord(mv.translatePoint(node.getScaledXyz(scale)), node.getShapeInfo().getScaledPoints(scale));
            Log.i(TAG, node.toString());
            Log.i(TAG, Arrays.toString(points));
            edge.moveTo(points[points.length-1].x, points[points.length-1].y);
            for (Point point : points){
                edge.lineTo(point.x, point.y);
            }
            c.drawPath(edge, fillPaint);
            c.drawPath(edge, strokePaint);
        }
    }

}
