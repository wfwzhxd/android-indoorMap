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
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.Way;

import java.util.List;

/**
 * Created by hunter on 4/17/17.
 */

public class WayOverlay extends Overlay {

    private static final String TAG = WayOverlay.class.getSimpleName();

    Paint fillPaint;

    public WayOverlay() {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.parseColor("#a29e89"));
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        List<Way> ways = mv.getDataSource().getWays(mv.getMapRect().enlarge(1.1f), mv.getLevel());
        Matrix matrix = mv.getMapMatrix();
        Edges edges;
        Point[] points;
        for (Way way : ways) {
            for (Way.WayLine wayLine : way.getWayLines()) {
                edges = wayLine.getEdges();
                if (edges == null) {
                    Log.d(TAG, "edges is null " + wayLine);
                    continue;
                }
                Log.d(TAG, "draw wayLine " + wayLine);
                points = edges.getPoints();
                points = MatrixUtils.applyMatrix(points, matrix);
                Path edge = new Path();
                edge.moveTo(points[points.length-1].x, points[points.length-1].y);
                for (Point point : points){
                    edge.lineTo(point.x, point.y);
                }
                c.drawPath(edge, fillPaint);
            }
        }
    }
}
