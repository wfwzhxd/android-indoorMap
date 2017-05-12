package com.hunter.indoormap.beans;

import java.util.Arrays;

/**
 * Created by hunter on 3/25/17.
 */

public class Edges extends Tagger{

    Point[] points;

    transient Rect bounds;
    float area;

    public Edges(Point[] points) {
        setPoints(points);
    }

    public Point[] getPoints() {
        return points;
    }

    public void setPoints(Point[] points) {
        this.points = points;
        bounds = null;
        area = -1;
    }

    public Rect getBounds() {
        if (bounds == null) {
            calculateBounds();
        }
        return bounds;
    }

    protected void calculateBounds() {
        int boundsMinX = Integer.MAX_VALUE;
        int boundsMinY = Integer.MAX_VALUE;
        int boundsMaxX = Integer.MIN_VALUE;
        int boundsMaxY = Integer.MIN_VALUE;

        for (int i = 0; i < points.length; i++) {
            int x = (int) points[i].x;
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            int y = (int) points[i].y;
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }
        bounds = new Rect(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
    }

    public float getArea() {
        if (area < 0) {
            if(points.length < 3) {
                area = 0;
            } else {
                double s = 0;
                for(int i = 0; i < points.length; i++) {
                    s += points[i].x * points[(i+1)%points.length].y - points[i].y * points[(i+1)%points.length].x;
                }
                area = (float) Math.abs(s/2.0);
            }
        }
        return area;
    }

    /**
     *
     * @param point
     * @return
     */
    public boolean contains(Point point) {
        if (points.length < 3 || !getBounds().contains(point.x, point.y)) {
            return false;
        }
        boolean flag = false;
        float px = point.x;
        float py = point.y;
        int i = 0;
        int j = points.length - 1;
        for (; i < points.length; j = i, i++) {
            float sx = points[i].x;
            float sy = points[i].y;
            float tx = points[j].x;
            float ty = points[j].y;
            if ((sx == px && sy == py) || (tx == px && ty == py)) {
//                System.out.println(i + "顶点重合");
                return true;
            }
            if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
                // 线段上与射线 Y 坐标相同的点的 X 坐标
                double x = sx + (py - sy) * (tx - sx) / (ty - sy);
                // 点在多边形的边上
                if (x == px) {
//                    System.out.println(i + "在边上");
                    return true;
                }

                // 射线穿过多边形的边界
                if (x > px) {
//                    System.out.println(i + "穿过边界");
                    flag = !flag;
                }
            }
        }
        return flag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Edges)) {
            return false;
        }
        Edges obj2 = (Edges) obj;
        return Arrays.equals(obj2.points, points);
    }

    @Override
    public String toString() {
        return "Edges{" +
                "points=" + Arrays.toString(points) +
                '}';
    }
}