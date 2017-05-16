package com.hunter.indoormap;


import com.hunter.indoormap.beans.GPoint;
import com.hunter.indoormap.beans.Point;

/**
 * Created by hunter on 4/2/17.
 */

public class CoordinateUtils {

    public static Point relativeCoord(Point origin, Point point) {
        return new Point(point.x - origin.x, point.y - origin.y);
    }

    public static Point[] relativeCoord(Point origin, Point[] points) {
        return pointOffset(points, -origin.x, -origin.y);
    }

    public static Point absoluteCoord(Point origin, Point point) {
        return new Point(point.x + origin.x, point.y + origin.y);
    }

    public static Point[] absoluteCoord(Point origin, Point[] points) {
        return pointOffset(points, origin.x, origin.y);
    }

    public static Point rotateAtPoint(Point center, Point point, float degree, boolean reuse) {
        float distance = calDistance(center, point);
        float oriDrgree = calDegree(center, point);
        float desDegree = (oriDrgree + degree)%360;
        if (desDegree<0) {
            desDegree += 360;
        }
//        System.out.println(degree + " " + oriDrgree + " " + desDegree);
        double radians = degree2radians(desDegree);
        float x = (float) (center.x + Math.cos(radians) * distance);
        float y = (float) (center.y + Math.sin(radians) * distance);
        return reuse ? point.set(x, y) : new Point(x, y);
    }

    public static Point[] rotateAtPoint(Point center, Point[] points, float degree, boolean reuse) {
        Point[] newPoints = reuse ? points : new Point[points.length];
        for (int i=0; i<points.length; i++) {
            newPoints[i] = rotateAtPoint(center, points[i], degree, reuse);
        }
        return newPoints;
    }

    public static float calDistance(Point start, Point end) {
        return (float) Math.sqrt(Math.pow(end.x-start.x, 2) + Math.pow(end.y-start.y, 2));
    }

    public static float calDistance(GPoint start, GPoint end) {
        return (float) Math.sqrt(Math.pow(end.x-start.x, 2) + Math.pow(end.y-start.y, 2) + Math.pow(end.z-start.z, 2));
    }

    public static float calDegree(Point start, Point end) {
        Point relPoint = relativeCoord(start, end);
        double degree = radians2degree(Math.atan2(relPoint.y, relPoint.x));
        return (float) (degree < 0 ? 360+degree : degree);
    }

    public static Point pointOffset(Point point, float dx, float dy) {
        return new Point(point.x + dx, point.y + dy);
    }

    public static Point[] pointOffset(Point[] points, float dx, float dy) {
        Point[] newPoints = new Point[points.length];
        for (int i=0; i<points.length; i++) {
            newPoints[i] = pointOffset(points[i], dx, dy);
        }
        return newPoints;
    }

    public static Point pointScale(Point point, float scale) {
        return new Point(point.x*scale, point.y*scale);
    }

    public static Point[] pointScale(Point[] points, float scale) {
        Point[] newPoints = new Point[points.length];
        for (int i=0; i<points.length; i++) {
            newPoints[i] = pointScale(points[i], scale);
        }
        return newPoints;
    }

    public static double degree2radians(float degree) {
        return degree*(2*Math.PI)/360;
    }

    public static float radians2degree(double radians) {
        return (float) (radians/(2*Math.PI)*360);
    }
}
