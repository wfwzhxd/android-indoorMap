package com.hunter.indoormap.beans;

import java.io.PipedOutputStream;

/**
 * Created by hunter on 4/12/17.
 */

public class Point {
    public static final Point ORIGIN = new Point(0, 0); //原点

    public int x;
    public int y;

    public Point() {}

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Point)) {
            return false;
        }
        Point obj2 = (Point) obj;
        return x==obj2.x && y==obj2.y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
