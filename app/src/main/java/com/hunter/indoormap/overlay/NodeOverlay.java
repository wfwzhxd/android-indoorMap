package com.hunter.indoormap.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.hunter.indoormap.MapView;
import com.hunter.indoormap.MatrixUtils;
import com.hunter.indoormap.beans.Edges;
import com.hunter.indoormap.beans.Node;
import com.hunter.indoormap.beans.Point;

import java.util.List;

/**
 * Created by hunter on 4/20/17.
 */

public class NodeOverlay extends Overlay {
    private final String TAG = NodeOverlay.class.getSimpleName();

    Paint fillPaint;
    Paint strokePaint;

    public NodeOverlay() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#00f6ff"));
        strokePaint = new Paint();
        strokePaint.setStrokeWidth(2.0f);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.parseColor("#ffb71c"));
        strokePaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        List<Node> nodes = mv.getDataSource().getNodes(mv.getMapRect().enlarge(1.1f), mv.getLevel());
        if (nodes == null) {
            Log.d(TAG, "nodes is null");
            return;
        }
        Matrix matrix = mv.getMapMatrix();
        for (Node node : nodes) {
            Edges edges = node.getEdges();
            if (edges == null) {
                Log.d(TAG, "edges is null " + node);
                continue;
            }
            Log.d(TAG, node.toString());
            Path edge = new Path();
            Point[] points = edges.getPoints();
            points = MatrixUtils.applyMatrix(points, matrix);
            edge.moveTo(points[points.length-1].x, points[points.length-1].y);
            for (Point point : points){
                edge.lineTo(point.x, point.y);
            }
            c.drawPath(edge, fillPaint);
            c.drawPath(edge, strokePaint);
        }
    }

    public void setFillColor(int color) {
        fillPaint.setColor(color);
    }
}
