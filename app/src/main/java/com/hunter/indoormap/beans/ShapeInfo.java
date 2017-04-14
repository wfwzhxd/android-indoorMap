package com.hunter.indoormap.beans;

import com.hunter.indoormap.CoordinateUtils;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by hunter on 3/25/17.
 */

public class ShapeInfo {

    public static class Shape extends MObj{

        Point[] points;

        public Shape(int id, Point[] points) {
            super(id, null);
            setPoints(points);
        }

        public Point[] getPoints() {
            return points;
        }

        public void setPoints(Point[] points) {
            this.points = points;
            bounds = null;
        }

        @Override
        protected void calculateBounds() {
            int boundsMinX = Integer.MAX_VALUE;
            int boundsMinY = Integer.MAX_VALUE;
            int boundsMaxX = Integer.MIN_VALUE;
            int boundsMaxY = Integer.MIN_VALUE;

            for (int i = 0; i < points.length; i++) {
                int x = points[i].x;
                boundsMinX = Math.min(boundsMinX, x);
                boundsMaxX = Math.max(boundsMaxX, x);
                int y = points[i].y;
                boundsMinY = Math.min(boundsMinY, y);
                boundsMaxY = Math.max(boundsMaxY, y);
            }
            bounds = new Rect(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY);
        }

        /**
         *
         * @param point
         * @return
         */
        public boolean contains(Point point) {
            boolean flag = false;
            int px = point.x;
            int py = point.y;
            System.out.println(px + "," + py);
            int i = 0;
            int j = points.length - 1;
            for (; i < points.length; j = i, i++) {
                int sx = points[i].x;
                int sy = points[i].y;
                int tx = points[j].x;
                int ty = points[j].y;
                System.out.println(i + "： " + sx + ", "+ sy + ", "+ tx + ", "+ ty);
                if ((sx == px && sy == py) || (tx == px && ty == py)) {
                    System.out.println(i + "顶点重合");
                    return true;
                }
                if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
                    // 线段上与射线 Y 坐标相同的点的 X 坐标
                    double x = sx + (py - sy) * (tx - sx) / (ty - sy);
                    // 点在多边形的边上
                    if (x == px) {
                        System.out.println(i + "在边上");
                        return true;
                    }

                    // 射线穿过多边形的边界
                    if (x > px) {
                        System.out.println(i + "穿过边界");
                        flag = !flag;
                    }
                }
            }
            System.out.println("return end");
            return flag;
        }

        @Override
        public String toString() {
            return "Shape{" +
                    "points=" + Arrays.toString(points) +
                    '}';
        }
    }

    private Shape shape;
    private float shapeDegree;
    Rect bounds;

    public ShapeInfo(Shape shape) {
        set(shape, shapeDegree);
    }

    public ShapeInfo(Shape shape, float shapeDegree) {
        set(shape, shapeDegree);
    }

    private ShapeInfo set(Shape shape, float shapeDegree) {
        if (shapeDegree != this.shapeDegree || (shape==null || shape != this.shape)) {
            bounds = null;
        }
        this.shape = shape;
        this.shapeDegree = shapeDegree;
        return this;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        set(shape, shapeDegree);
    }

    public float getShapeDegree() {
        return shapeDegree;
    }

    public void setShapeDegree(float shapeDegree) {
        set(shape, shapeDegree);
    }

    public boolean contains(Point testPoint) {
        return shape.contains(CoordinateUtils.rotateAtPoint(Point.ORIGIN, testPoint, -shapeDegree, false));
    }

    public Rect getBounds() {
        if (bounds == null) {
            calculateBounds();
        }
        return bounds;
    }

    protected void calculateBounds() {
        bounds = new Shape(-1, CoordinateUtils.rotateAtPoint(Point.ORIGIN, shape.points, shapeDegree, false)).getBounds();
    }

    @Override
    public String toString() {
        return "ShapeInfo{" +
                "shape=" + shape +
                ", shapeDegree=" + shapeDegree +
                '}';
    }
}