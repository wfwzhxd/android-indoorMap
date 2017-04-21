package com.hunter.indoormap;

import android.graphics.Matrix;

import com.hunter.indoormap.beans.Point;

/**
 * Created by hunter on 4/21/17.
 */

public class MatrixUtils {

    public static Point[] applyMatrix(Point[] points, Matrix matrix) {
        float[] ps = new float[points.length*2];
        for (int i=0; i<points.length; i++) {
            ps[i*2] = points[i].x;
            ps[i*2+1] = points[i].y;
        }
        matrix.mapPoints(ps);
        for (int i=0; i<points.length; i++) {
            points[i].x = ps[i*2];
            points[i].y = ps[i*2+1];
        }
        return points;
    }

    public static Point applyMatrix(Point point, Matrix matrix) {
        float[] ps = new float[]{point.x, point.y};
        matrix.mapPoints(ps);
        return new Point(ps[0], ps[1]);
    }
}
