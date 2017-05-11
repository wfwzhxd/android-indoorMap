package com.hunter.indoormap.beans;

/**
 * Created by hunter on 5/11/17.
 */

public class Floor {
    private int z;
    private Rect bounds;

    public Floor(int z, Rect bounds) {
        this.z = z;
        this.bounds = bounds;
    }

    public int getZ() {
        return z;
    }

    public Rect getBounds() {
        return bounds;
    }

}
