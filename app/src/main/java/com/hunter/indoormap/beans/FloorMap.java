package com.hunter.indoormap.beans;


/**
 * Created by hunter on 3/25/17.
 */

public class FloorMap {
    int z;
    ShapeInfo bounds;

    public FloorMap(int z, ShapeInfo bounds) {
        this.z = z;
        this.bounds = bounds;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public ShapeInfo getBounds() {
        return bounds;
    }

    public void setBounds(ShapeInfo bounds) {
        this.bounds = bounds;
    }

    boolean contains(Point point){
        return bounds.contains(point);
    }
}
