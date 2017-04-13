package com.hunter.indoormap.beans;


/**
 * Created by hunter on 3/25/17.
 */

public class GPoint extends Point{

    public int z;

    public static GPoint from(int x, int y) {
        return new GPoint(x, y);
    }

    public static GPoint from(int x, int y, int z) {
        return new GPoint(x, y, z);
    }

    public GPoint(int x, int y) {
        this(x, y, 0);
    }

    public GPoint(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }

    public GPoint(GPoint src) {
        this(src.x, src.y, src.z);
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
