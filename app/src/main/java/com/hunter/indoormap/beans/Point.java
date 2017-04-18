package com.hunter.indoormap.beans;

import com.hunter.indoormap.MathUtils;

import java.io.PipedOutputStream;

/**
 * Created by hunter on 4/12/17.
 */

public class Point {
    public static final Point ORIGIN = new Point(0, 0); //原点

    public float x;
    public float y;

    public Point() {}

    public Point(float x, float y) {
        set(x, y);
    }

    public Point(int x, int y) {
        this((float) x, (float) y);
    }

    public Point set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Point scale(float scale) {
        x *= scale;
        y *= scale;
        return this;
    }

    public Point offset(int dx, int dy) {
        this.x += dx;
        this.y += dy;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Point)) {
            return false;
        }
        Point obj2 = (Point) obj;
        return MathUtils.isEqual(x, obj2.x) && MathUtils.isEqual(y, obj2.y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
