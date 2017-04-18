package com.hunter.indoormap.overlay;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.hunter.indoormap.CoordinateUtils;
import com.hunter.indoormap.MapView;
import com.hunter.indoormap.beans.Point;
import com.hunter.indoormap.beans.ShapeInfo;
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
        fillPaint.setColor(Color.GRAY);
    }

    @Override
    public void draw(Canvas c, MapView mv) {
        List<Way> ways = mv.getDataSource().getWays(mv.getMapRect(), mv.getFloor());
        float scale = mv.getScale();
        Point[] points;
        Point mPoint;
        ShapeInfo[] shapeInfos;
        for (Way way : ways) {
            if (!way.isShow()) {
                continue;
            }
            shapeInfos = way.getShapeInfos();
            for (int i = 0; i < shapeInfos.length; i++) {
                points = CoordinateUtils.absoluteCoord(mv.translatePoint(CoordinateUtils.pointScale(way.getWayNodes()[i], scale)), shapeInfos[i].getScaledPoints(scale));
                Path edge = new Path();
                edge.moveTo(points[points.length-1].x, points[points.length-1].y);
                for (Point point : points){
                    edge.lineTo(point.x, point.y);
                }
                c.drawPath(edge, fillPaint);
            }
            for (Way.WayNode wayNode : way.getWayNodes()) {
                mPoint = mv.translatePoint(CoordinateUtils.pointScale(wayNode, scale));
                Log.i(TAG, mPoint.toString());
                c.drawCircle(mPoint.x, mPoint.y, wayNode.getWide()/2*scale, fillPaint);
            }
        }
    }
}
