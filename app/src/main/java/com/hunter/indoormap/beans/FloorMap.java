package com.hunter.indoormap.beans;


/**
 * Created by hunter on 3/25/17.
 */

public class FloorMap extends MObj{
    int z;
    ShapeInfo edge;

    public FloorMap(int z, ShapeInfo edge) {
        super(-1, null);
        this.z = z;
        this.edge = edge;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public ShapeInfo getEdge() {
        return edge;
    }

    public void setEdge(ShapeInfo edge) {
        this.edge = edge;
    }

    @Override
    public boolean contains(Point point){
        return edge.contains(point);
    }

    @Override
    public Rect getBounds() {
        return edge.getBounds();
    }
}
