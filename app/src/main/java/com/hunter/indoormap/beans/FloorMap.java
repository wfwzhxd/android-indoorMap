package com.hunter.indoormap.beans;


/**
 * Created by hunter on 3/25/17.
 */

public class FloorMap extends MObj{
    int z;
    ShapeInfo edge;

    public FloorMap(int id, String name, int z, ShapeInfo edge) {
        super(id, name);
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof FloorMap)) {
            return false;
        }
        FloorMap obj2 = (FloorMap) obj;
        return z == obj2.z;
    }
}
