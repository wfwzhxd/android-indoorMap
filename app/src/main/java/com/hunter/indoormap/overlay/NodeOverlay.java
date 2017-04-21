package com.hunter.indoormap.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.MatrixUtils;
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
        fillPaint.setColor(Color.BLUE);
        strokePaint = new Paint();
        strokePaint.setStrokeWidth(2.0f);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas c, MapView mv) {

        List<Node> nodes = mv.getDataSource().getNodes(mv.getMapRect(), mv.getFloor());
        Matrix matrix = mv.getMatrix();
        for (Node node : nodes) {
            if (!node.isShow()) {
                continue;
            }
            Path edge = new Path();
            Point[] points = node.getShapeInfo().getPoints();
            points = CoordinateUtils.absoluteCoord(node.getXyz(), points);
            MatrixUtils.applyMatrix(points, matrix);
            edge.moveTo(points[points.length-1].x, points[points.length-1].y);
            for (Point point : points){
                edge.lineTo(point.x, point.y);
            }
            c.drawPath(edge, fillPaint);
            c.drawPath(edge, strokePaint);
        }

    }
}
