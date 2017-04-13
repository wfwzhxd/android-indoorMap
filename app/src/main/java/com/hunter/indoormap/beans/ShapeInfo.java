package com.hunter.indoormap.beans;

import com.hunter.indoormap.CoordinateUtils;

import java.util.Arrays;

/**
 * Created by hunter on 3/25/17.
 */

public class ShapeInfo {

    public static class Shape extends MObj{

        Point[] points;

        public Shape(int id, Point[] points) {
            super(id, null);
            this.points = points;
        }

        public Point[] getPoints() {
            return points;
        }

        public void setPoints(Point[] points) {
            this.points = points;
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

    public ShapeInfo(Shape shape) {
        this.shape = shape;
    }

    public ShapeInfo(Shape shape, float shapeDegree) {
        this.shape = shape;
        this.shapeDegree = shapeDegree;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public float getShapeDegree() {
        return shapeDegree;
    }

    public void setShapeDegree(float shapeDegree) {
        this.shapeDegree = shapeDegree;
    }

    public boolean contains(Point testPoint) {
        return shape.contains(CoordinateUtils.rotateAtPoint(Point.ORIGIN, testPoint, -shapeDegree, null));
    }

    @Override
    public String toString() {
        return "ShapeInfo{" +
                "shape=" + shape +
                ", shapeDegree=" + shapeDegree +
                '}';
    }
}