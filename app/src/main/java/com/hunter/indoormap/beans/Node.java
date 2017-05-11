package com.hunter.indoormap.beans;

/**
 * Created by hunter on 3/25/17.
 */

public class Node extends MObj {
    GPoint xyz;
    Edges edges;    // may be null

    public Node(int id, GPoint xyz) {
        this(id, null, xyz, null);
    }

    public Node(int id, String name, GPoint xyz) {
        this(id, name, xyz, null);
    }

    public Node(int id, String name, GPoint xyz, Edges edges) {
        super(id, name);
        setXyz(xyz);
        setEdges(edges);
    }

    public GPoint getXyz() {
        return xyz;
    }

    public void setXyz(GPoint xyz) {
        this.xyz = xyz;
        bounds = null;
    }

    public Edges getEdges() {
        return edges;
    }

    public void setEdges(Edges edges) {
        this.edges = edges;
        bounds = null;
    }

    public float getArea() {
        return edges == null ? 0 : edges.getArea();
    }

    @Override
    public boolean contains(Point point) {
        return edges == null ? point.equals(xyz) : edges.contains(point);
    }

    @Override
    protected void calculateBounds() {
        bounds = edges == null ? new Rect(xyz.x-1, xyz.y-1, xyz.x+1, xyz.y+1) : edges.getBounds();
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", name=" + name +
                ", xyz=" + xyz +
                ", edges=" + edges +
                '}';
    }
}
