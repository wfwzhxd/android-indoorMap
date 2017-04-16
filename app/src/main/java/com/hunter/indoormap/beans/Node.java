package com.hunter.indoormap.beans;

import com.hunter.indoormap.CoordinateUtils;

/**
 * Created by hunter on 3/25/17.
 */

public class Node extends MObj {
    GPoint xyz;
    ShapeInfo shapeInfo;

    public Node(int id, GPoint xyz) {
        this(id, null, xyz, null);
    }

    public Node(int id, String name, GPoint xyz) {
        this(id, name, xyz, null);
    }

    public Node(int id, String name, GPoint xyz, ShapeInfo shapeInfo) {
        super(id, name);
        this.xyz = xyz;
        this.shapeInfo = shapeInfo;
    }

    public GPoint getXyz() {
        return xyz;
    }

    public void setXyz(GPoint xyz) {
        this.xyz = xyz;
    }

    public ShapeInfo getShapeInfo() {
        return shapeInfo;
    }

    public void setShapeInfo(ShapeInfo shapeInfo) {
        this.shapeInfo = shapeInfo;
    }

    @Override
    public boolean contains(Point point) {
        return shapeInfo.contains(CoordinateUtils.relativeCoord(xyz, point));
    }

    @Override
    protected void calculateBounds() {
        bounds = new Rect(shapeInfo.getBounds()).offset(xyz.x, xyz.y);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name=" + name +
                ", show=" + show +
                ", xyz=" + xyz +
                ", shapeInfo=" + shapeInfo +
                '}';
    }
}
